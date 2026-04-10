package com.example.gymhome.model;

import java.io.Serializable;

public class BaiTapLon implements Serializable {
    private String Id;
    private String TenBaiTapLon;
    private String CapDo;
    private int TongThoiGian;
    private String HinhAnh;
    private String MoTa;
    private int SoLuongBaiTapNho;

    public BaiTapLon() {
    }

    // Constructor
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
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTenBaiTapLon() {
        return TenBaiTapLon;
    }

    public void setTenBaiTapLon(String tenBaiTapLon) {
        TenBaiTapLon = tenBaiTapLon;
    }

    public String getCapDo() {
        return CapDo;
    }

    public void setCapDo(String capDo) {
        CapDo = capDo;
    }

    public int getTongThoiGian() {
        return TongThoiGian;
    }

    public void setTongThoiGian(int tongThoiGian) {
        TongThoiGian = tongThoiGian;
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

    public int getSoLuongBaiTapNho() {
        return SoLuongBaiTapNho;
    }

    public void setSoLuongBaiTapNho(int soLuongBaiTapNho) {
        SoLuongBaiTapNho = soLuongBaiTapNho;
    }

    public String getThoiGian() {
        int phut = TongThoiGian / 60;
        return phut + " phút";
    }
}
