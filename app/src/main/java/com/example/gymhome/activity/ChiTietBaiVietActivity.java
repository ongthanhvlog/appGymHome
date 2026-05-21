package com.example.gymhome.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.gymhome.R;
import com.example.gymhome.model.BaiViet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChiTietBaiVietActivity extends AppCompatActivity {

    private ImageView imgHinhAnhDaiDien, imgLinkLogo;
    private TextView tvTenBaiViet, tvNgayDang, tvNguon;
    private WebView wvNoiDung;
    private com.google.android.material.button.MaterialButton btnViewOriginal;
    private android.widget.ImageButton ibQuayLai, ibLuuBaiViet;
    private BaiViet baiViet;
    private FirebaseFirestore db;
    private String userId;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chitietbaiviet);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("");
        }
        
        ibQuayLai = findViewById(R.id.ibQuayLai);
        ibQuayLai.setOnClickListener(v -> finish());

        imgHinhAnhDaiDien = findViewById(R.id.imgHinhAnhDaiDien);
        imgLinkLogo = findViewById(R.id.imgLinkLogo);
        tvTenBaiViet = findViewById(R.id.tvTenBaiViet);
        tvNgayDang = findViewById(R.id.tvNgayDang);
        tvNguon = findViewById(R.id.tvNguon);
        wvNoiDung = findViewById(R.id.wvNoiDung);
        wvNoiDung.setNestedScrollingEnabled(false);
        btnViewOriginal = findViewById(R.id.btnViewOriginal);
        ibLuuBaiViet = findViewById(R.id.ibLuuBaiViet);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        baiViet = (BaiViet) getIntent().getSerializableExtra("BaiViet");

        if (baiViet != null) {
            hienThiChiTiet();
            dongBoBaiVietDaLuu();
        }

        ibLuuBaiViet.setOnClickListener(v -> xuLyLuuBaiViet());
    }

    private void dongBoBaiVietDaLuu() {
        if (userId == null || baiViet == null || baiViet.getId() == null) return;

        db.collection("NguoiDung").document(userId)
                .collection("BaiVietDaLuu").document(baiViet.getId())
                .addSnapshotListener((value, error) -> {
                    if (value != null && value.exists()) {
                        isSaved = true;
                        ibLuuBaiViet.setImageResource(R.drawable.ic_bookmark_filled);
                        ibLuuBaiViet.setImageTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.colorSaved)));
                    } else {
                        isSaved = false;
                        ibLuuBaiViet.setImageResource(R.drawable.ic_bookmark_border);
                        ibLuuBaiViet.setImageTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.white)));
                    }
                });
    }

    private void xuLyLuuBaiViet() {
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để lưu bài viết", Toast.LENGTH_SHORT).show();
            return;
        }

        if (baiViet == null || baiViet.getId() == null) {
            Toast.makeText(this, "Không thể lưu bài viết này", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference docRef = db.collection("NguoiDung").document(userId)
                .collection("BaiVietDaLuu").document(baiViet.getId());

        if (isSaved) {
            docRef.delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Đã bỏ lưu bài viết", Toast.LENGTH_SHORT).show();
            });
        } else {
            docRef.set(baiViet).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Đã lưu bài viết", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void hienThiChiTiet() {
        tvTenBaiViet.setText(baiViet.getTenBaiViet());
        
        // Hiển thị ngày đăng từ model
        tvNgayDang.setText("Ngày đăng: " + (baiViet.getNgayDang() != null ? baiViet.getNgayDang() : "N/A"));

        // Load Logo nguồn
        if (baiViet.getLinkLogo() != null && !baiViet.getLinkLogo().isEmpty()) {
            Glide.with(this)
                    .load(baiViet.getLinkLogo())
                    .into(imgLinkLogo);
            imgLinkLogo.setVisibility(View.VISIBLE);
            tvNguon.setVisibility(View.GONE);
        } else {
            imgLinkLogo.setVisibility(View.GONE);
            tvNguon.setVisibility(View.VISIBLE);
            tvNguon.setText(baiViet.getSourceName());
        }

        Glide.with(this)
                .load(baiViet.getHinhAnhDaiDien())
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgHinhAnhDaiDien);

        // 1. Xử lý nội dung (Regex để chuyển đổi thẻ [img])
        String rawContent = baiViet.getNoiDung();
        if (rawContent == null) rawContent = "";

        // Chuyển đổi [img]URL[/img] sang thẻ <img> chuẩn
        String processedContent = rawContent.replaceAll("(?is)\\[img\\]\\s*(.*?)\\s*\\[/img\\]",
                "<img src=\"$1\" loading='lazy' onerror=\"this.style.display='none';\" />");

        // 2. Tối ưu cấu hình WebView
        WebSettings settings = wvNoiDung.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setDefaultTextEncodingName("utf-8");

        wvNoiDung.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                wvNoiDung.requestLayout();
            }
        });

        // 3. Hệ thống CSS cho nội dung bài viết
        String style = "<style>" +
                "body { " +
                "  font-family: 'Roboto', sans-serif; " +
                "  line-height: 1.6; " +
                "  color: #333333; " +
                "  padding: 0; " +
                "  margin: 0; " +
                "  font-size: 17px; " +
                "  text-align: left; " +
                "  white-space: pre-wrap; " +
                "  word-wrap: break-word; " +
                "}" +
                "img { " +
                "  max-width: 100% !important; " +
                "  height: auto !important; " +
                "  border-radius: 8px; " +
                "  margin: 20px 0 10px 0; " +
                "  display: block; " +
                "  margin-left: auto; " +
                "  margin-right: auto; " +
                "}" +
                "p { margin-bottom: 16px; }" +
                "strong { color: #000; font-weight: bold; }" +
                "</style>";

        String fullHtml = "<html>" +
                "<head>" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                style +
                "</head>" +
                "<body>" +
                processedContent +
                "</body>" +
                "</html>";

        wvNoiDung.loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null);

        btnViewOriginal.setVisibility(View.GONE);
    }
}
