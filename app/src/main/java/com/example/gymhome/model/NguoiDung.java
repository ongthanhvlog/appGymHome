package com.example.gymhome.model;

import java.io.Serializable;

public class NguoiDung implements Serializable {
    private String UserId;
    private String Email;
    private int SoLuongBaiHocDaDangKy;
    private ThongTinNguoiDung ThongTinNguoiDung;

    public NguoiDung() {
    }

    public NguoiDung(String UserId, String Email, int SoLuongBaiHocDaDangKy, ThongTinNguoiDung ThongTinNguoiDung) {
        this.UserId = UserId;
        this.Email = Email;
        this.SoLuongBaiHocDaDangKy = SoLuongBaiHocDaDangKy;
        this.ThongTinNguoiDung = ThongTinNguoiDung;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public int getSoLuongBaiHocDaDangKy() {
        return SoLuongBaiHocDaDangKy;
    }

    public void setSoLuongBaiHocDaDangKy(int SoLuongBaiHocDaDangKy) {
        this.SoLuongBaiHocDaDangKy = SoLuongBaiHocDaDangKy;
    }

    public ThongTinNguoiDung getThongTinNguoiDung() {
        return ThongTinNguoiDung;
    }

    public void setThongTinNguoiDung(ThongTinNguoiDung ThongTinNguoiDung) {
        this.ThongTinNguoiDung = ThongTinNguoiDung;
    }

    public static class ThongTinNguoiDung implements Serializable {
        private int CanNang;
        private int ChieuCao;
        private String GioiTinh;
        private int SoBaiTapHoanThanh;
        private int ThoiGianTapLuyen;
        private double TongCalo;
        private int Tuoi;

        public ThongTinNguoiDung() {
        }

        public ThongTinNguoiDung(int CanNang, int ChieuCao, String GioiTinh, int SoBaiTapHoanThanh, int ThoiGianTapLuyen, double TongCalo, int Tuoi) {
            this.CanNang = CanNang;
            this.ChieuCao = ChieuCao;
            this.GioiTinh = GioiTinh;
            this.SoBaiTapHoanThanh = SoBaiTapHoanThanh;
            this.ThoiGianTapLuyen = ThoiGianTapLuyen;
            this.TongCalo = TongCalo;
            this.Tuoi = Tuoi;
        }

        public int getCanNang() {
            return CanNang;
        }

        public void setCanNang(int CanNang) {
            this.CanNang = CanNang;
        }

        public int getChieuCao() {
            return ChieuCao;
        }

        public void setChieuCao(int ChieuCao) {
            this.ChieuCao = ChieuCao;
        }

        public String getGioiTinh() {
            return GioiTinh;
        }

        public void setGioiTinh(String GioiTinh) {
            this.GioiTinh = GioiTinh;
        }

        public int getSoBaiTapHoanThanh() {
            return SoBaiTapHoanThanh;
        }

        public void setSoBaiTapHoanThanh(int SoBaiTapHoanThanh) {
            this.SoBaiTapHoanThanh = SoBaiTapHoanThanh;
        }

        public int getThoiGianTapLuyen() {
            return ThoiGianTapLuyen;
        }

        public void setThoiGianTapLuyen(int ThoiGianTapLuyen) {
            this.ThoiGianTapLuyen = ThoiGianTapLuyen;
        }

        public double getTongCalo() {
            return TongCalo;
        }

        public void setTongCalo(double TongCalo) {
            this.TongCalo = TongCalo;
        }

        public int getTuoi() {
            return Tuoi;
        }

        public void setTuoi(int Tuoi) {
            this.Tuoi = Tuoi;
        }
    }
}
