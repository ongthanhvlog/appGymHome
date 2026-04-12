package com.example.gymhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gymhome.R;
import com.example.gymhome.adapter.BaiTapNhoAdapter;
import com.example.gymhome.model.BaiTapNho;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DanhSachBaiTapNho extends AppCompatActivity {
    private ImageButton ibQuayLai;
    private Button btnCaiDatTapLuyen, btnBatDauTapLuyen;
    private ImageView imgHinhAnhBaiTapLon;
    private TextView tvTenBaiTapLon, tvMoTa;
    private RecyclerView rvBaiTapNho;
    
    private BaiTapNhoAdapter adapter;
    private List<BaiTapNho> danhSachBaiTapNho;
    private FirebaseFirestore db;
    private double canNang;
    
    private String capDo, ngayId, baiTapLonId;
    private boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dsbaitapnho);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //nhan du lieu tu intent
        isRegistered = getIntent().getBooleanExtra("isRegistered", false);
        capDo = getIntent().getStringExtra("CapDo");
        if (capDo == null) {
            capDo = getIntent().getStringExtra("PhanLoai");
        }
        if (capDo == null) {
            capDo = getIntent().getStringExtra("Level"); 
        }
        ngayId = getIntent().getStringExtra("NgayId");
        baiTapLonId = getIntent().getStringExtra("BaiTapLonId");
        String tenBaiTapLon = getIntent().getStringExtra("TenBaiTapLon");
        String moTa = getIntent().getStringExtra("MoTa");
        String hinhAnh = getIntent().getStringExtra("HinhAnh");

        // anh xa id
        ibQuayLai = findViewById(R.id.ibQuayLai);
        btnCaiDatTapLuyen = findViewById(R.id.btnCaiDatTapLuyen);
        btnBatDauTapLuyen = findViewById(R.id.btnBatDauTapLuyen);
        imgHinhAnhBaiTapLon = findViewById(R.id.imgHinhAnhBaiTapLon);
        tvTenBaiTapLon = findViewById(R.id.tvTenBaiTapLon);
        tvMoTa = findViewById(R.id.tvMoTa);
        rvBaiTapNho = findViewById(R.id.rvBaiTapNho);

        tvTenBaiTapLon.setText(tenBaiTapLon);
        tvMoTa.setText(moTa);
        Glide.with(this)
                .load(hinhAnh)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgHinhAnhBaiTapLon);
        db = FirebaseFirestore.getInstance();
        danhSachBaiTapNho = new ArrayList<>();
        rvBaiTapNho.setLayoutManager(new LinearLayoutManager(this));

        loadCanNangNguoiDung();
        
        adapter = new BaiTapNhoAdapter(danhSachBaiTapNho, canNang, item -> {
            int position = danhSachBaiTapNho.indexOf(item);
            Intent intent = new Intent(DanhSachBaiTapNho.this, com.example.gymhome.activity.BaiTapNho.class);
            intent.putExtra("DanhSachBaiTap", (ArrayList<BaiTapNho>) danhSachBaiTapNho);
            intent.putExtra("ViTriHienTai", position);
            startActivity(intent);
        });
        rvBaiTapNho.setAdapter(adapter);

        // xu ly click
        ibQuayLai.setOnClickListener(v -> finish());
        btnBatDauTapLuyen.setOnClickListener(v -> {
            if (!danhSachBaiTapNho.isEmpty()) {
                Intent intent = new Intent(DanhSachBaiTapNho.this, com.example.gymhome.activity.BaiTapNho.class);
                intent.putExtra("DanhSachBaiTap", (ArrayList<BaiTapNho>) danhSachBaiTapNho);
                intent.putExtra("ViTriHienTai", 0);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Không có bài tập nào để bắt đầu", Toast.LENGTH_SHORT).show();
            }
        });

        if (isRegistered && baiTapLonId != null) {
            loadDanhSachBaiTapNhoDaDangKy();
        } else if (capDo != null && ngayId != null && baiTapLonId != null) {
            loadDanhSachBaiTapNho();
        }
    }

    private void loadCanNangNguoiDung() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;
        db.collection("NguoiDung").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double canNangVal = documentSnapshot.getDouble("ThongTinNguoiDung.CanNang");
                        if (canNangVal != null) {
                            this.canNang = canNangVal;
                            adapter = new BaiTapNhoAdapter(danhSachBaiTapNho, this.canNang, item -> {
                                int position = danhSachBaiTapNho.indexOf(item);
                                Intent intent = new Intent(DanhSachBaiTapNho.this, com.example.gymhome.activity.BaiTapNho.class);
                                intent.putExtra("DanhSachBaiTap", (ArrayList<BaiTapNho>) danhSachBaiTapNho);
                                intent.putExtra("ViTriHienTai", position);
                                startActivity(intent);
                            });
                            rvBaiTapNho.setAdapter(adapter);
                        }
                    }
                });
    }

    private void loadDanhSachBaiTapNhoDaDangKy() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        db.collection("NguoiDung")
                .document(userId)
                .collection("DanhSachBaiTapLonDaDangKy")
                .document(baiTapLonId)
                .collection("DanhSachBaiTapNho")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        danhSachBaiTapNho.clear();
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BaiTapNho item = document.toObject(BaiTapNho.class);
                                item.setId(document.getId());
                                danhSachBaiTapNho.add(item);
                            }
                            danhSachBaiTapNho.sort((o1, o2) -> Integer.compare(o1.getSoThuTu(), o2.getSoThuTu()));
                        } else {
                            Toast.makeText(this, "Chưa có bài tập nhỏ nào được lưu", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadDanhSachBaiTapNho() {
        db.collection("KeHoach")
                .document(capDo)
                .collection("Ngay")
                .document(ngayId)
                .collection("DanhSachBaiTapLon")
                .document(baiTapLonId)
                .collection("DanhSachBaiTapNho")
                .orderBy("SoThuTu", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        danhSachBaiTapNho.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BaiTapNho item = document.toObject(BaiTapNho.class);
                            item.setId(document.getId());
                            danhSachBaiTapNho.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
