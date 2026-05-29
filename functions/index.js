const admin = require("firebase-admin");
const { setGlobalOptions } = require("firebase-functions/v2");

// Khởi tạo Admin SDK một lần duy nhất
admin.initializeApp();

// Cấu hình Global Options
setGlobalOptions({ region: "us-central1" });

// Import các modules đã tách
const baiviet = require("./baiviet");
const thongbao = require("./thongbao");

// Export các Cloud Functions
// Liên quan đến Bài Viết
exports.triggerCapNhatBaiVietMoi = baiviet.triggerCapNhatBaiVietMoi;
exports.themBaiVietTuLink = baiviet.themBaiVietTuLink;
exports.xoaAllBaiViet = baiviet.xoaAllBaiViet;
exports.tuDongCapNhatBaiVietMoi = baiviet.tuDongCapNhatBaiVietMoi;
exports.tuDongXoaBaiViet = baiviet.tuDongXoaBaiViet;
exports.triggerXoaBaiVietCu = baiviet.triggerXoaBaiVietCu;
exports.triggerXoaBaiVietChuaLuu = baiviet.triggerXoaBaiVietChuaLuu;

// Liên quan đến Thông Báo
exports.guiThongBaoHeThong = thongbao.guiThongBaoHeThong;
exports.tuDongGuiThongBaoHenGio = thongbao.tuDongGuiThongBaoHenGio;
