package com.example.gymhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymhome.R;
import com.example.gymhome.adapter.BaiVietAdapter;
import com.example.gymhome.model.BaiViet;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DanhSachBaiVietActivity extends AppCompatActivity {

    private RecyclerView rvDanhSachBaiViet;
    private BaiVietAdapter adapter;
    private List<BaiViet> danhSachBaiViet;
    private FirebaseFirestore db;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_bai_viet);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        db = FirebaseFirestore.getInstance();
        rvDanhSachBaiViet = findViewById(R.id.rvDanhSachBaiViet);
        pbLoading = findViewById(R.id.pbLoading);

        rvDanhSachBaiViet.setLayoutManager(new LinearLayoutManager(this));
        danhSachBaiViet = new ArrayList<>();
        adapter = new BaiVietAdapter(danhSachBaiViet, item -> {
            Intent intent = new Intent(this, ChiTietBaiVietActivity.class);
            intent.putExtra("BaiViet", item);
            startActivity(intent);
        });
        rvDanhSachBaiViet.setAdapter(adapter);

        loadAllBaiViet();
    }

    private void loadAllBaiViet() {
        pbLoading.setVisibility(View.VISIBLE);
        db.collection("BaiViet")
                .orderBy("ngayDang", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    pbLoading.setVisibility(View.GONE);
                    if (error != null) {
                        Log.e("FirestoreError", "Lỗi: " + error.getMessage());
                        Toast.makeText(this, "Lỗi tải dữ liệu Firestore", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        Log.d("FirestoreData", "Tổng số bài viết: " + value.size());
                        if (value.isEmpty()) {
                            Toast.makeText(this, "Danh sách bài viết trống", Toast.LENGTH_SHORT).show();
                        }
                        danhSachBaiViet.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            BaiViet item = doc.toObject(BaiViet.class);
                            danhSachBaiViet.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}