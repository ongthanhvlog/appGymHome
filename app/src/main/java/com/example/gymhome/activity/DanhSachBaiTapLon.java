package com.example.gymhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageButton;
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
import com.example.gymhome.adapter.BaiTapLonAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DanhSachBaiTapLon extends AppCompatActivity {

    private ImageButton ibQuayLai;
    private ImageView imgHinhAnhBaiTapLon;
    private TextView tvNgay;
    private RecyclerView rvBaiTapLon;
    private BaiTapLonAdapter adapter;
    private List<com.example.gymhome.model.BaiTapLon> danhSachBaiTapLon;
    private FirebaseFirestore db;
    private String ngayId;
    private String capDo;
    private double canNang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dsbaitaplon);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //nhan du lieu tu intent
        ngayId = getIntent().getStringExtra("NgayId");
        capDo = getIntent().getStringExtra("CapDo");
        String hinhAnhNgay = getIntent().getStringExtra("HinhAnhNgay");

        // anh xa id
        ibQuayLai = findViewById(R.id.ibQuayLai);
        imgHinhAnhBaiTapLon = findViewById(R.id.imgHinhAnhBaiTapLon);
        tvNgay = findViewById(R.id.tvNgay);
        rvBaiTapLon = findViewById(R.id.rvBaiTapLon);

        if (ngayId != null) {
            String displayNgay = ngayId.replace("ngay_", "Ngày ");
            tvNgay.setText(displayNgay);
        }
        // hien thi hinh anh cua ngay vao header
        Glide.with(this)
                .load(hinhAnhNgay)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgHinhAnhBaiTapLon);
        db = FirebaseFirestore.getInstance();
        danhSachBaiTapLon = new ArrayList<>();
        rvBaiTapLon.setLayoutManager(new LinearLayoutManager(this));
        
        loadCanNangNguoiDung();

        adapter = new BaiTapLonAdapter(danhSachBaiTapLon, canNang, item -> {
            Intent intent = new Intent(DanhSachBaiTapLon.this, DanhSachBaiTapNho.class);
            intent.putExtra("CapDo", capDo);
            intent.putExtra("NgayId", ngayId);
            intent.putExtra("BaiTapLonId", item.getId());
            intent.putExtra("TenBaiTapLon", item.getTenBaiTapLon());
            intent.putExtra("MoTa", item.getMoTa());
            intent.putExtra("HinhAnh", item.getHinhAnh());
            startActivity(intent);
        });
        rvBaiTapLon.setAdapter(adapter);
        ibQuayLai.setOnClickListener(v -> finish());
        if (ngayId != null && capDo != null) {
            loadDanhSachBaiTapLon();
        }
    }

    private void loadDanhSachBaiTapLon() {
        db.collection("KeHoach")
                .document(capDo)
                .collection("Ngay")
                .document(ngayId)
                .collection("DanhSachBaiTapLon")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        danhSachBaiTapLon.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            com.example.gymhome.model.BaiTapLon item = document.toObject(com.example.gymhome.model.BaiTapLon.class);
                            item.setId(document.getId());
                            danhSachBaiTapLon.add(item);
                            loadBaiTapNhoForItem(item);
                        }
                    } else {
                        Toast.makeText(this, "Lỗi tải dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadBaiTapNhoForItem(com.example.gymhome.model.BaiTapLon item) {
        db.collection("KeHoach")
                .document(capDo)
                .collection("Ngay")
                .document(ngayId)
                .collection("DanhSachBaiTapLon")
                .document(item.getId())
                .collection("DanhSachBaiTapNho")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<com.example.gymhome.model.BaiTapNho> listNho = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        listNho.add(doc.toObject(com.example.gymhome.model.BaiTapNho.class));
                    }
                    item.setDanhSachBaiTapNho(listNho);
                    adapter.notifyDataSetChanged();
                });
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
                            adapter = new BaiTapLonAdapter(danhSachBaiTapLon, this.canNang, item -> {
                                Intent intent = new Intent(DanhSachBaiTapLon.this, DanhSachBaiTapNho.class);
                                intent.putExtra("CapDo", capDo);
                                intent.putExtra("NgayId", ngayId);
                                intent.putExtra("BaiTapLonId", item.getId());
                                intent.putExtra("TenBaiTapLon", item.getTenBaiTapLon());
                                intent.putExtra("MoTa", item.getMoTa());
                                intent.putExtra("HinhAnh", item.getHinhAnh());
                                startActivity(intent);
                            });
                            rvBaiTapLon.setAdapter(adapter);
                        }
                    }
                });
    }
}
