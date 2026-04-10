package com.example.gymhome.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymhome.R;
import com.example.gymhome.adapter.ThuThachAdapter;
import com.example.gymhome.model.ThuThach;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DanhSachThuThach extends AppCompatActivity {

    private RecyclerView rvTatCaThuThach;
    private ThuThachAdapter adapter;
    private List<ThuThach> list;
    private FirebaseFirestore db;
    private ImageButton ibQuayLai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dsthuthach);
        
        // Xử lý Edge-to-Edge để giao diện không bị đè bởi thanh hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Anh xạ ID
        ibQuayLai = findViewById(R.id.ibQuayLai);
        rvTatCaThuThach = findViewById(R.id.rvTatCaThuThach);

        // Nút quay lại hoạt động tương tự dsbaitaplon
        ibQuayLai.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        rvTatCaThuThach.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new ThuThachAdapter(list, item -> {
            // Mở màn hình chi tiết thử thách
            android.content.Intent intent = new android.content.Intent(DanhSachThuThach.this, ChiTietThuThach.class);
            intent.putExtra("ThuThachData", item);
            startActivity(intent);
        });

        rvTatCaThuThach.setAdapter(adapter);

        loadAllThuThach();
    }

    private void loadAllThuThach() {
        db.collection("ThuThach")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ThuThach item = document.toObject(ThuThach.class);
                            item.setId(document.getId());
                            list.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Lỗi tải dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
