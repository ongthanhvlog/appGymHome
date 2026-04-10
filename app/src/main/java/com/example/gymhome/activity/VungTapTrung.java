package com.example.gymhome.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.gymhome.adapter.BaiTapLonAdapter;
import com.example.gymhome.model.BaiTapLon;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VungTapTrung extends AppCompatActivity {
    private ImageButton ibQuayLai;
    private ImageView ivVungTapTrung;
    private TextView tvTenVung, tvMoTa;
    private RecyclerView rvBaiTapLon;
    private BaiTapLonAdapter adapter;
    private List<BaiTapLon> baiTapLonList;
    private FirebaseFirestore db;
    private String vungId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.vungtaptrung);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // anh xa id
        ibQuayLai = findViewById(R.id.ibQuayLai);
        ivVungTapTrung = findViewById(R.id.imgHinhAnhBaiTapLon);
        tvTenVung = findViewById(R.id.tvTenVung);
        tvMoTa = findViewById(R.id.tvMoTa);
        rvBaiTapLon = findViewById(R.id.rvBaiTapLon);

        // nhan du lieu tu intent
        vungId = getIntent().getStringExtra("VungId");
        String tenVung = getIntent().getStringExtra("TenVung");
        String hinhAnh = getIntent().getStringExtra("HinhAnh");
        String moTa = getIntent().getStringExtra("MoTa");

        tvTenVung.setText(tenVung);
        tvMoTa.setText(moTa);
        Glide.with(this).load(hinhAnh).placeholder(R.drawable.ic_launcher_background).into(ivVungTapTrung);
        db = FirebaseFirestore.getInstance();
        baiTapLonList = new ArrayList<>();
        rvBaiTapLon.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaiTapLonAdapter(baiTapLonList, item -> {
            Intent intent = new Intent(VungTapTrung.this, DangKyBaiTapLon.class);
            intent.putExtra("BaiTapLon", item);
            intent.putExtra("VungId", vungId);
            startActivity(intent);
        });
        rvBaiTapLon.setAdapter(adapter);
        ibQuayLai.setOnClickListener(v -> finish());
        if (vungId != null) {
            loadDanhSachBaiTapLon();
        }
    }

    private void loadDanhSachBaiTapLon() {
        db.collection("VungTapTrung")
                .document(vungId)
                .collection("DanhSachBaiTapLon")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        baiTapLonList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BaiTapLon item = document.toObject(BaiTapLon.class);
                            item.setId(document.getId());
                            baiTapLonList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
