package com.example.gymhome.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ngay implements Serializable {
    private String Id;
    private int TongThoiGian;
    private int SoLuongBaiTapLon;
    private String HinhAnh;
    private String MoTa;
    private List<BaiTapLon> danhSachBaiTapLon = new ArrayList<>();

    public Ngay() {
    }

    // Constructor
    public Ngay(String id, int tongThoiGian, int soLuongBaiTapLon, String hinhAnh, String moTa) {
        this.Id = id;
        this.TongThoiGian = tongThoiGian;
        this.SoLuongBaiTapLon = soLuongBaiTapLon;
        this.HinhAnh = hinhAnh;
        this.MoTa = moTa;
    }

    // Getter và Setter
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getTongThoiGian() {
        return TongThoiGian;
    }

    public void setTongThoiGian(int tongThoiGian) {
        TongThoiGian = tongThoiGian;
    }

    public int getSoLuongBaiTapLon() {
        return SoLuongBaiTapLon;
    }

    public void setSoLuongBaiTapLon(int soLuongBaiTapLon) {
        SoLuongBaiTapLon = soLuongBaiTapLon;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }

    public String getMoTa() {
        return MoTa;
    }

    public void setMoTa(String moTa) {
        MoTa = moTa;
    }

    public List<BaiTapLon> getDanhSachBaiTapLon() {
        return danhSachBaiTapLon;
    }

    public void setDanhSachBaiTapLon(List<BaiTapLon> danhSachBaiTapLon) {
        this.danhSachBaiTapLon = danhSachBaiTapLon;
    }

    public String getTongThoiGianDisplay() {
        return String.valueOf(TongThoiGian / 60);
    }

    public String getSoLuongBaiTapLonDisplay() {
        return String.valueOf(SoLuongBaiTapLon);
    }

    public double tinhTongCalo(double canNang) {
        double total = 0;
        if (danhSachBaiTapLon != null) {
            for (BaiTapLon btl : danhSachBaiTapLon) {
                total += btl.tinhTongCalo(canNang);
            }
        }
        return total;
    }

    public int getNgayNumber() {
        try {
            if (Id != null) {
                if (Id.contains("_")) {
                    return Integer.parseInt(Id.split("_")[1]);
                }
                return Integer.parseInt(Id.replaceAll("[^0-9]", ""));
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }
}
