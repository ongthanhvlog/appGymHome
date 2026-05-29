package com.example.gymhome.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymhome.R;
import com.example.gymhome.adapter.ThongBaoAdapter;
import com.example.gymhome.model.ThongBao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class DanhSachThongBaoActivity extends AppCompatActivity {

    private RecyclerView rvThongBao;
    private ThongBaoAdapter adapter;
    private List<ThongBao> listThongBao;
    private FirebaseFirestore db;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsthongbao);

        initViews();
        fetchDuLieuThongBao();
    }

    private void initViews() {
        rvThongBao = findViewById(R.id.rvThongBao);
        btnBack = findViewById(R.id.btnBack);
        
        listThongBao = new ArrayList<>();
        adapter = new ThongBaoAdapter(listThongBao);
        
        rvThongBao.setLayoutManager(new LinearLayoutManager(this));
        rvThongBao.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }

    private void fetchDuLieuThongBao() {
        db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();
        
        if (userId == null) return;

        db.collection("NguoiDung").document(userId).collection("ThongBaoNhacNho")
                .orderBy("ngayGui", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<ThongBao> data = queryDocumentSnapshots.toObjects(ThongBao.class);
                        listThongBao.clear();
                        listThongBao.addAll(data);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể tải thông báo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
