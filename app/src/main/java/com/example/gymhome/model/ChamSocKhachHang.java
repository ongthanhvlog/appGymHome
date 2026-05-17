package com.example.gymhome.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class ChamSocKhachHang {
    private String id;
    private String Email;
    private String tieuDe;
    private String noiDung;
    private Timestamp thoiGianTao;
    private String phanHoi;
    private Timestamp thoiGianPhanHoi;
    private int trangThai;

    public ChamSocKhachHang() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("Email")
    public String getEmail() {
        return Email;
    }

    @PropertyName("Email")
    public void setEmail(String Email) {
        this.Email = Email;
    }

    @PropertyName("tieuDe")
    public String getTieuDe() {
        return tieuDe;
    }

    @PropertyName("tieuDe")
    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    @PropertyName("noiDung")
    public String getNoiDung() {
        return noiDung;
    }

    @PropertyName("noiDung")
    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    @PropertyName("thoiGianTao")
    public Timestamp getThoiGianTao() {
        return thoiGianTao;
    }

    @PropertyName("thoiGianTao")
    public void setThoiGianTao(Timestamp thoiGianTao) {
        this.thoiGianTao = thoiGianTao;
    }

    @PropertyName("phanHoi")
    public String getPhanHoi() {
        return phanHoi;
    }

    @PropertyName("phanHoi")
    public void setPhanHoi(String phanHoi) {
        this.phanHoi = phanHoi;
    }

    @PropertyName("thoiGianPhanHoi")
    public Timestamp getThoiGianPhanHoi() {
        return thoiGianPhanHoi;
    }

    @PropertyName("thoiGianPhanHoi")
    public void setThoiGianPhanHoi(Timestamp thoiGianPhanHoi) {
        this.thoiGianPhanHoi = thoiGianPhanHoi;
    }

    @PropertyName("trangThai")
    public int getTrangThai() {
        return trangThai;
    }

    @PropertyName("trangThai")
    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
}
