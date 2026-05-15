package com.example.gymhome.model;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;

public class BaiViet implements Serializable {
    private String tenBaiViet;
    private String moTa;
    private String noiDung;
    private String hinhAnhDaiDien;
    private String ngayDang;
    private String linkBaiViet;
    private String linkLogo;
    private String tag;

    public BaiViet() {}

    @PropertyName("tenBaiViet")
    public String getTenBaiViet() {
        return tenBaiViet;
    }

    @PropertyName("tenBaiViet")
    public void setTenBaiViet(String tenBaiViet) {
        this.tenBaiViet = tenBaiViet;
    }

    @PropertyName("moTa")
    public String getMoTa() {
        return moTa;
    }

    @PropertyName("moTa")
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @PropertyName("noiDung")
    public String getNoiDung() {
        return noiDung;
    }

    @PropertyName("noiDung")
    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    @PropertyName("hinhAnhDaiDien")
    public String getHinhAnhDaiDien() {
        return hinhAnhDaiDien;
    }

    @PropertyName("hinhAnhDaiDien")
    public void setHinhAnhDaiDien(String hinhAnhDaiDien) {
        this.hinhAnhDaiDien = hinhAnhDaiDien;
    }

    @PropertyName("ngayDang")
    public String getNgayDang() {
        if (ngayDang != null && ngayDang.contains("T")) {
            return ngayDang.split("T")[0];
        }
        return ngayDang;
    }

    @PropertyName("ngayDang")
    public void setNgayDang(String ngayDang) {
        this.ngayDang = ngayDang;
    }

    @PropertyName("linkBaiViet")
    public String getLinkBaiViet() {
        return linkBaiViet;
    }

    @PropertyName("linkBaiViet")
    public void setLinkBaiViet(String linkBaiViet) {
        this.linkBaiViet = linkBaiViet;
    }

    @PropertyName("linkLogo")
    public String getLinkLogo() {
        return linkLogo;
    }

    @PropertyName("linkLogo")
    public void setLinkLogo(String linkLogo) {
        this.linkLogo = linkLogo;
    }

    @PropertyName("tag")
    public String getTag() {
        return tag;
    }

    @PropertyName("tag")
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSourceName() {
        if (linkBaiViet == null || linkBaiViet.isEmpty()) return "GymHome";
        try {
            android.net.Uri uri = android.net.Uri.parse(linkBaiViet);
            String host = uri.getHost();
            if (host == null) return "GymHome";
            
            String name = host.replace("www.", "");
            String[] parts = name.split("\\.");
            if (parts.length > 1) {
                String domain = parts[0];
                return domain.substring(0, 1).toUpperCase() + domain.substring(1);
            }
            return name;
        } catch (Exception e) {
            return "GymHome";
        }
    }
}
