package com.example.gymhome.model;

import com.google.firebase.firestore.Exclude;
import java.io.Serializable;

public class ThongBao implements Serializable {
    private String tieuDe;
    private String noiDung;
    private Object ngayGui; // Để Object để tương thích cả String cũ và Timestamp mới
    private String userId; // Bổ sung cho API gửi thông báo

    public ThongBao() {
        // Required for Firestore
    }

    public ThongBao(String tieuDe, String noiDung, Object ngayGui) {
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.ngayGui = ngayGui;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public Object getNgayGui() {
        return ngayGui;
    }

    public void setNgayGui(Object ngayGui) {
        this.ngayGui = ngayGui;
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
