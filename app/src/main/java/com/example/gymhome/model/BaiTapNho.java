package com.example.gymhome.model;

import java.io.Serializable;

public class BaiTapNho implements Serializable {
    private String Id;
    private String TenBaiTapNho;
    private String MoTa;
    private int ThoiGian;
    private double MET;
    private int SoThuTu;
    private String VideoHuongDan;
    private String VideoType;
    private String HinhAnh;

    public BaiTapNho() {
    }

    // Constructor
    public BaiTapNho(String id, String tenBaiTapNho, String moTa, int thoiGian, int soThuTu, String videoHuongDan, String videoType, String hinhAnh) {
        this.Id = id;
        this.TenBaiTapNho = tenBaiTapNho;
        this.MoTa = moTa;
        this.ThoiGian = thoiGian;
        this.SoThuTu = soThuTu;
        this.VideoHuongDan = videoHuongDan;
        this.VideoType = videoType;
        this.HinhAnh = hinhAnh;
    }

    // Getter và Setter
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTenBaiTapNho() {
        return TenBaiTapNho;
    }

    public void setTenBaiTapNho(String tenBaiTapNho) {
        TenBaiTapNho = tenBaiTapNho;
    }

    public String getMoTa() {
        return MoTa;
    }

    public void setMoTa(String moTa) {
        MoTa = moTa;
    }

    public int getThoiGian() {
        return ThoiGian;
    }

    public void setThoiGian(int thoiGian) {
        ThoiGian = thoiGian;
    }

    public double getMET() {
        return MET;
    }

    public void setMET(double MET) {
        this.MET = MET;
    }

    public int getSoThuTu() {
        return SoThuTu;
    }

    public void setSoThuTu(int soThuTu) {
        SoThuTu = soThuTu;
    }

    public String getVideoHuongDan() {
        return VideoHuongDan;
    }

    public void setVideoHuongDan(String videoHuongDan) {
        VideoHuongDan = videoHuongDan;
    }

    public String getVideoType() {
        return VideoType;
    }

    public void setVideoType(String videoType) {
        VideoType = videoType;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }
}
