package com.example.gymhome.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaiTapLon implements Serializable {
    private String Id;
    private String TenBaiTapLon;
    private String CapDo;
    private int TongThoiGian;
    private String HinhAnh;
    private String MoTa;
    private int SoLuongBaiTapNho;
    private List<BaiTapNho> danhSachBaiTapNho = new ArrayList<>();

    public BaiTapLon() {
    }

    public BaiTapLon(String id, String tenBaiTapLon, String capDo, int tongThoiGian, String hinhAnh, String moTa, int soLuongBaiTapNho) {
        this.Id = id;
        this.TenBaiTapLon = tenBaiTapLon;
        this.CapDo = capDo;
        this.TongThoiGian = tongThoiGian;
        this.HinhAnh = hinhAnh;
        this.MoTa = moTa;
        this.SoLuongBaiTapNho = soLuongBaiTapNho;
    }

    // Getter và Setter
    public String getId() { return Id; }
    public void setId(String id) { Id = id; }

    public String getTenBaiTapLon() { return TenBaiTapLon; }
    public void setTenBaiTapLon(String tenBaiTapLon) { TenBaiTapLon = tenBaiTapLon; }

    public String getCapDo() { return CapDo; }
    public void setCapDo(String capDo) { CapDo = capDo; }

    public int getTongThoiGian() { return TongThoiGian; }
    public void setTongThoiGian(int tongThoiGian) { TongThoiGian = tongThoiGian; }

    public String getHinhAnh() { return HinhAnh; }
    public void setHinhAnh(String hinhAnh) { HinhAnh = hinhAnh; }

    public String getMoTa() { return MoTa; }
    public void setMoTa(String moTa) { MoTa = moTa; }

    public int getSoLuongBaiTapNho() { return SoLuongBaiTapNho; }
    public void setSoLuongBaiTapNho(int soLuongBaiTapNho) { SoLuongBaiTapNho = soLuongBaiTapNho; }

    public List<BaiTapNho> getDanhSachBaiTapNho() { return danhSachBaiTapNho; }
    public void setDanhSachBaiTapNho(List<BaiTapNho> danhSachBaiTapNho) { this.danhSachBaiTapNho = danhSachBaiTapNho; }

    public String getThoiGian() {
        return (TongThoiGian / 60) + " phút";
    }

    // Hàm tính tổng Calo dựa trên cân nặng người dùng
    public double tinhTongCalo(double canNang) {
        double total = 0;
        if (danhSachBaiTapNho != null) {
            for (BaiTapNho bt : danhSachBaiTapNho) {
                total += bt.getMET() * canNang * (bt.getThoiGian() / 3600.0);
            }
        }
        return total;
    }
}
