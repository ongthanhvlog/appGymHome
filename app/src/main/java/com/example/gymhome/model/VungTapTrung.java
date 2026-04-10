package com.example.gymhome.model;

import java.io.Serializable;

public class VungTapTrung implements Serializable {
    private String Id;
    private String TenVung;
    private String HinhAnh;
    private String MoTa;
    public VungTapTrung() {
    }

    public VungTapTrung(String id, String tenVung, String hinhAnh, String moTa) {
        this.Id = id;
        this.TenVung = tenVung;
        this.HinhAnh = hinhAnh;
        this.MoTa = moTa;
    }

    // Getter và Setter
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getTenVung() {
        return TenVung;
    }

    public void setTenVung(String tenVung) {
        this.TenVung = tenVung;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.HinhAnh = hinhAnh;
    }

    public String getMoTa() {
        return MoTa;
    }

    public void setMoTa(String moTa) {
        this.MoTa = moTa;
    }
}
