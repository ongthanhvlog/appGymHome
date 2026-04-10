package com.example.gymhome.model;

import java.io.Serializable;

public class ThuThach implements Serializable {
    private String ThuThachId;
    private String TenThuThach;
    private String MoTa;
    private int ThoiGian;
    private String CapDo;
    private String VideoHuongDan;
    private String VideoType;
    private String HinhAnh;
    public ThuThach() {
    }
    //Contructor
    public ThuThach(String thuThachId, String tenThuThach, String moTa, int thoiGian, String capDo, String videoHuongDan, String videoType, String hinhAnh){
        this.ThuThachId = thuThachId;
        this.TenThuThach = tenThuThach;
        this.MoTa = moTa;
        this.ThoiGian = thoiGian;
        this.CapDo = capDo;
        this.VideoHuongDan = videoHuongDan;
        this.VideoType = videoType;
        this.HinhAnh = hinhAnh;
    }
    // Getter và Setter
    public String getId() {
        return ThuThachId;
    }

    public void setId(String id) {
        ThuThachId = id;
    }

    public String getTenThuThach() {
        return TenThuThach;
    }

    public void setTenThuThach(String tenThuThach) {
        TenThuThach = tenThuThach;
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

    public String getCapDo() {
        return CapDo;
    }

    public void setCapDo(String capDo) { CapDo = capDo; }

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
