const { onSchedule } = require("firebase-functions/v2/scheduler");
const { onRequest } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");
const moment = require("moment-timezone");

// Khởi tạo admin nếu chưa có
if (!admin.apps.length) {
    admin.initializeApp();
}

/**
 * Lưu thông báo vào sub-collection của người dùng để hiển thị trong app
 */
async function luuVaoThongBaoCaNhan(userId, tieuDe, noiDung) {
    const db = admin.firestore();
    try {
        await db.collection("NguoiDung").doc(userId).collection("ThongBaoNhacNho").add({
            tieuDe: tieuDe,
            noiDung: noiDung,
            ngayGui: admin.firestore.FieldValue.serverTimestamp()
        });
    } catch (e) {
        console.error(`Lỗi khi lưu thông báo cho user ${userId}:`, e);
    }
}

/**
 * API gửi thông báo từ hệ thống (Web Admin)
 */
exports.guiThongBaoHeThong = onRequest({ cors: true }, async (req, res) => {
    const db = admin.firestore();
    const userId = req.body?.userId || req.query.userId;
    const tieuDe = req.body?.tieuDe || req.query.tieuDe || "GymHome Thông Báo 🔔";
    const noiDung = req.body?.noiDung || req.query.noiDung || "Bạn có thông báo mới từ hệ thống.";
    const ngayGui = req.body?.ngayGui;
    const ngayGuiTimestamp = req.body?.ngayGuiTimestamp;

    try {
        let thoiGianHenGio = null;

        if (ngayGuiTimestamp) {
            thoiGianHenGio = new Date(Number(ngayGuiTimestamp));
        } else if (ngayGui) {
            thoiGianHenGio = new Date(ngayGui.includes("T") ? ngayGui : ngayGui.replace(" ", "T"));
        }

        // Hẹn giờ gửi
        if (thoiGianHenGio && thoiGianHenGio.getTime() > new Date().getTime() + 30000) {
            // LƯU VÀO HÀNG ĐỢI HỆ THỐNG
            await db.collection("ThongBao").add({
                userId: userId || null,
                tieuDe,
                noiDung,
                ngayGui: admin.firestore.Timestamp.fromDate(thoiGianHenGio)
            });

            const formattedTime = moment(thoiGianHenGio).tz("Asia/Ho_Chi_Minh").format("HH:mm DD/MM/YYYY");
            return res.status(200).json({
                success: true,
                message: `Đã hẹn giờ gửi thông báo lúc ${formattedTime}`
            });
        }

        // gửi luôn
        if (userId) {
            const userDoc = await db.collection("NguoiDung").doc(userId).get();
            if (userDoc.exists && userDoc.data().fcmToken) {
                await admin.messaging().send({
                    notification: { title: tieuDe, body: noiDung },
                    token: userDoc.data().fcmToken
                });
            }
            await luuVaoThongBaoCaNhan(userId, tieuDe, noiDung);
            return res.status(200).json({ success: true, message: `Đã gửi và lưu thông báo cho user ${userId}` });
        } else {
            const snapshot = await db.collection("NguoiDung").get();
            const messages = [];
            const savePromises = [];

            snapshot.forEach(doc => {
                const userData = doc.data();
                if (userData.fcmToken) {
                    messages.push({ notification: { title: tieuDe, body: noiDung }, token: userData.fcmToken });
                }
                savePromises.push(luuVaoThongBaoCaNhan(doc.id, tieuDe, noiDung));
            });

            if (messages.length > 0) {
                for (let i = 0; i < messages.length; i += 500) {
                    await admin.messaging().sendEach(messages.slice(i, i + 500));
                }
            }
            await Promise.all(savePromises);
            return res.status(200).json({ success: true, message: `Đã gửi và lưu cho ${savePromises.length} người dùng` });
        }
    } catch (e) {
        console.error("Lỗi trong guiThongBaoHeThong:", e);
        res.status(500).json({ success: false, error: e.message });
    }
});

/**
 * Hàm chạy tự động mỗi phút để gửi các thông báo đến hạn
 */
exports.tuDongGuiThongBaoHenGio = onSchedule({
    schedule: "every 1 minutes",
    timeZone: "Asia/Ho_Chi_Minh"
}, async (event) => {
    const db = admin.firestore();
    const bayGio = admin.firestore.Timestamp.now();

    const fcmQueueSnap = await db.collection("ThongBao").where("ngayGui", "<=", bayGio).get();
    const moiNgaySnap = await db.collection("ThongBaoMoiNgay").where("trangThai", "==", 1).get();

    if (fcmQueueSnap.empty && moiNgaySnap.empty) return;

    const usersSnap = await db.collection("NguoiDung").get();
    const users = [];
    usersSnap.forEach(doc => users.push({ id: doc.id, ...doc.data() }));

    const messagesToSend = [];
    const deletePromises = [];
    const savePromises = [];

    // XỬ LÝ TIN HẸN GIỜ
    fcmQueueSnap.forEach(doc => {
        const data = doc.data();
        const targetUserId = data.userId;

        for (const user of users) {
            if (!targetUserId || targetUserId === user.id) {
                if (user.fcmToken) {
                    messagesToSend.push({ notification: { title: data.tieuDe, body: data.noiDung }, token: user.fcmToken });
                }
                savePromises.push(luuVaoThongBaoCaNhan(user.id, data.tieuDe, data.noiDung));
            }
        }
        deletePromises.push(doc.ref.delete());
    });

    // XỬ LÝ TIN MỖI NGÀY
    for (const mDoc of moiNgaySnap.docs) {
        const mData = mDoc.data();

        for (const user of users) {
            const userTz = user.timezone || "Asia/Ho_Chi_Minh";
            const userLocalTime = moment().tz(userTz).format("HH:mm");

            if (userLocalTime === mData.thoiGian) {
                if (user.fcmToken) {
                    messagesToSend.push({ notification: { title: mData.tieuDe, body: mData.noiDung }, token: user.fcmToken });
                }
                savePromises.push(luuVaoThongBaoCaNhan(user.id, mData.tieuDe, mData.noiDung));
            }
        }
    }

    if (messagesToSend.length > 0) {
        for (let i = 0; i < messagesToSend.length; i += 500) {
            await admin.messaging().sendEach(messagesToSend.slice(i, i + 500));
        }
    }

    await Promise.all([...deletePromises, ...savePromises]);
    console.log(`[Scheduled] Đã xử lý xong.`);
});

 //Tự động nhắc nhở người dùng tập luyện nếu họ nghỉ quá 3 ngày
 // Chạy vào 8:00 sáng mỗi ngày
exports.nhacNhoTapLuyenTuDong = onSchedule({
    schedule: "0 8 * * *",
    timeZone: "Asia/Ho_Chi_Minh"
}, async (event) => {
    const db = admin.firestore();
    try {
        const usersSnap = await db.collection("NguoiDung").get();
        const messagesToSend = [];
        const savePromises = [];

        usersSnap.forEach(doc => {
            const userData = doc.data();
            const thongTin = userData.ThongTinNguoiDung;

            // Lấy múi giờ của riêng User, nếu không có thì dùng mặc định VN
            const userTz = userData.timezone || "Asia/Ho_Chi_Minh";
            const bayGioUser = moment().tz(userTz);

            if (thongTin && thongTin.ngayTapGanNhat) {
                const ngayTapGanNhat = moment(thongTin.ngayTapGanNhat.toDate()).tz(userTz);

                // Tính toán số ngày chênh lệch
                const diffDays = bayGioUser.startOf('day').diff(ngayTapGanNhat.startOf('day'), 'days');

                if (diffDays >= 3) {
                    const tieuDe = "GymHome Nhắc Nhở Tập Luyện 🏋️‍♂️";
                    const noiDung = `Đã ${diffDays} ngày rồi bạn chưa luyện tập, hãy quay lại tập cùng GymHome nhé! 💪`;

                    if (userData.fcmToken) {
                        messagesToSend.push({
                            notification: { title: tieuDe, body: noiDung },
                            token: userData.fcmToken,
                        });
                    }
                    savePromises.push(luuVaoThongBaoCaNhan(doc.id, tieuDe, noiDung));
                }
            }
        });

        if (messagesToSend.length > 0) {
            for (let i = 0; i < messagesToSend.length; i += 500) {
                await admin.messaging().sendEach(messagesToSend.slice(i, i + 500));
            }
        }

        await Promise.all(savePromises);
        console.log(`[Scheduled] Đã gửi nhắc nhở tập luyện cho ${messagesToSend.length} người dùng.`);
    } catch (e) {
        console.error("Lỗi trong nhacNhoTapLuyenTuDong:", e);
    }
});
