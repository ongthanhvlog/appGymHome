package com.example.gymhome.model;

import com.google.firebase.firestore.PropertyName;
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
    @PropertyName("Id")
    public String getId() {
        return Id;
    }

    @PropertyName("Id")
    public void setId(String id) {
        Id = id;
    }

    @PropertyName("TenBaiTapNho")
    public String getTenBaiTapNho() {
        return TenBaiTapNho;
    }

    @PropertyName("TenBaiTapNho")
    public void setTenBaiTapNho(String tenBaiTapNho) {
        TenBaiTapNho = tenBaiTapNho;
    }

    @PropertyName("MoTa")
    public String getMoTa() {
        return MoTa;
    }

    @PropertyName("MoTa")
    public void setMoTa(String moTa) {
        MoTa = moTa;
    }

    @PropertyName("ThoiGian")
    public int getThoiGian() {
        return ThoiGian;
    }

    @PropertyName("ThoiGian")
    public void setThoiGian(int thoiGian) {
        ThoiGian = thoiGian;
    }

    @PropertyName("MET")
    public double getMET() {
        return MET;
    }

    @PropertyName("MET")
    public void setMET(double MET) {
        this.MET = MET;
    }

    @PropertyName("SoThuTu")
    public int getSoThuTu() {
        return SoThuTu;
    }

    @PropertyName("SoThuTu")
    public void setSoThuTu(int soThuTu) {
        SoThuTu = soThuTu;
    }

    @PropertyName("VideoHuongDan")
    public String getVideoHuongDan() {
        return VideoHuongDan;
    }

    @PropertyName("VideoHuongDan")
    public void setVideoHuongDan(String videoHuongDan) {
        VideoHuongDan = videoHuongDan;
    }

    @PropertyName("VideoType")
    public String getVideoType() {
        return VideoType;
    }

    @PropertyName("VideoType")
    public void setVideoType(String videoType) {
        VideoType = videoType;
    }

    @PropertyName("HinhAnh")
    public String getHinhAnh() {
        return HinhAnh;
    }

    @PropertyName("HinhAnh")
    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }

    public double tinhCalo(double canNang) {
        // calo = MET * Cân nặng * Thời gian (giờ)
        return MET * canNang * (ThoiGian / 3600.0);
    }
}
