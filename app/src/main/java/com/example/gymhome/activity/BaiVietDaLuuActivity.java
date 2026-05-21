package com.example.gymhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymhome.R;
import com.example.gymhome.adapter.BaiVietAdapter;
import com.example.gymhome.model.BaiViet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BaiVietDaLuuActivity extends AppCompatActivity {

    private RecyclerView rvBaiVietDaLuu;
    private BaiVietAdapter adapter;
    private List<BaiViet> danhSachBaiViet;
    private FirebaseFirestore db;
    private ProgressBar pbLoading;
    private TextView tvChuaCoBaiViet;
    private ImageButton ibQuayLai;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsbaivietdaluu);

        ibQuayLai = findViewById(R.id.ibQuayLai);
        ibQuayLai.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();
        
        rvBaiVietDaLuu = findViewById(R.id.rvBaiVietDaLuu);
        pbLoading = findViewById(R.id.pbLoading);
        tvChuaCoBaiViet = findViewById(R.id.tvChuaCoBaiViet);

        rvBaiVietDaLuu.setLayoutManager(new LinearLayoutManager(this));
        danhSachBaiViet = new ArrayList<>();
        adapter = new BaiVietAdapter(danhSachBaiViet, item -> {
            Intent intent = new Intent(this, ChiTietBaiVietActivity.class);
            intent.putExtra("BaiViet", item);
            startActivity(intent);
        });
        adapter.setOnSaveClickListener((item, isSaved) -> {
            if (isSaved && userId != null) {
                db.collection("NguoiDung").document(userId)
                        .collection("BaiVietDaLuu").document(item.getId())
                        .delete();
            }
        });
        rvBaiVietDaLuu.setAdapter(adapter);

        loadDanhSachBaiVietDaLuu();
    }

    private void loadDanhSachBaiVietDaLuu() {
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        pbLoading.setVisibility(View.VISIBLE);
        db.collection("NguoiDung").document(userId)
                .collection("BaiVietDaLuu")
                .addSnapshotListener((value, error) -> {
                    pbLoading.setVisibility(View.GONE);
                    if (error != null) {
                        Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        danhSachBaiViet.clear();
                        List<String> savedIds = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            BaiViet item = doc.toObject(BaiViet.class);
                            item.setId(doc.getId());
                            danhSachBaiViet.add(item);
                            savedIds.add(doc.getId());
                        }
                        adapter.setDanhSachIdDaLuu(savedIds);
                        adapter.notifyDataSetChanged();
                        
                        if (danhSachBaiViet.isEmpty()) {
                            tvChuaCoBaiViet.setVisibility(View.VISIBLE);
                        } else {
                            tvChuaCoBaiViet.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
