const admin = require("firebase-admin");
const { setGlobalOptions } = require("firebase-functions/v2");

// Khởi tạo Admin SDK
admin.initializeApp();

// Cấu hình Global Options
setGlobalOptions({ region: "us-central1" });

// Import các modules
const baiviet = require("./baiviet");
const thongbao = require("./thongbao");

// Export các Cloud Functions
exports.triggerCapNhatBaiVietMoi = baiviet.triggerCapNhatBaiVietMoi;
exports.themBaiVietTuLink = baiviet.themBaiVietTuLink;
exports.xoaAllBaiViet = baiviet.xoaAllBaiViet;
exports.tuDongCapNhatBaiVietMoi = baiviet.tuDongCapNhatBaiVietMoi;
exports.tuDongXoaBaiViet = baiviet.tuDongXoaBaiViet;
exports.triggerXoaBaiVietCu = baiviet.triggerXoaBaiVietCu;
exports.triggerXoaBaiVietChuaLuu = baiviet.triggerXoaBaiVietChuaLuu;

exports.guiThongBaoHeThong = thongbao.guiThongBaoHeThong;
exports.tuDongGuiThongBaoHenGio = thongbao.tuDongGuiThongBaoHenGio;
exports.nhacNhoTapLuyenTuDong = thongbao.nhacNhoTapLuyenTuDong;
