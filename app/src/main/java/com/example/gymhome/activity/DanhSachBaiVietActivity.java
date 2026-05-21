package com.example.gymhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymhome.R;
import com.example.gymhome.adapter.BaiVietAdapter;
import com.example.gymhome.model.BaiViet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DanhSachBaiVietActivity extends AppCompatActivity {

    private RecyclerView rvDanhSachBaiViet;
    private BaiVietAdapter adapter;
    private List<BaiViet> danhSachBaiViet;
    private List<BaiViet> danhSachHienThi;
    private FirebaseFirestore db;
    private String userId;
    private ListenerRegistration listenerLuuBaiViet;
    private ProgressBar pbLoading;
    private String tagHienTai = "";
    private String tuKhoaTimKiem = "";
    private MaterialButton btnTatCa, btnSucKhoe, btnDinhDuong, btnTapLuyen;
    private SearchView searchView;
    private ImageButton ibQuayLai;
    private LinearLayout llBaiVietDaLuu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsbaiviet);

        ibQuayLai = findViewById(R.id.ibQuayLai);
        ibQuayLai.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();
        rvDanhSachBaiViet = findViewById(R.id.rvDanhSachBaiViet);
        pbLoading = findViewById(R.id.pbLoading);

        // Ánh xạ button lọc
        btnTatCa = findViewById(R.id.btnTatCaBaiViet);
        btnSucKhoe = findViewById(R.id.btnBaiVietSucKhoe);
        btnDinhDuong = findViewById(R.id.btnBaiVietDinhDuong);
        btnTapLuyen = findViewById(R.id.btnBaiVietTapLuyen);
        searchView = findViewById(R.id.searchViewBaiViet);
        llBaiVietDaLuu = findViewById(R.id.llSavedArticles);

        llBaiVietDaLuu.setOnClickListener(v -> {
            Intent intent = new Intent(this, BaiVietDaLuuActivity.class);
            startActivity(intent);
        });

        thietLapButtonLoc();
        thietLapTimKiem();

        rvDanhSachBaiViet.setLayoutManager(new LinearLayoutManager(this));
        danhSachBaiViet = new ArrayList<>();
        danhSachHienThi = new ArrayList<>();
        adapter = new BaiVietAdapter(danhSachHienThi, item -> {
            Intent intent = new Intent(this, ChiTietBaiVietActivity.class);
            intent.putExtra("BaiViet", item);
            startActivity(intent);
        });
        adapter.setOnSaveClickListener((item, isSaved) -> {
            xuLyLuuBaiViet(item, isSaved);
        });
        rvDanhSachBaiViet.setAdapter(adapter);

        loadTatCaBaiViet();
        dongBoBaiVietDaLuu();
    }

    private void dongBoBaiVietDaLuu() {
        if (userId == null) return;
        listenerLuuBaiViet = db.collection("NguoiDung").document(userId)
                .collection("BaiVietDaLuu")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        List<String> savedIds = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            savedIds.add(doc.getId());
                        }
                        adapter.setDanhSachIdDaLuu(savedIds);
                    }
                });
    }

    private void xuLyLuuBaiViet(BaiViet item, boolean isSaved) {
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để lưu bài viết", Toast.LENGTH_SHORT).show();
            return;
        }
        if (item.getId() == null) return;

        if (isSaved) {
            db.collection("NguoiDung").document(userId)
                    .collection("BaiVietDaLuu").document(item.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã bỏ lưu", Toast.LENGTH_SHORT).show());
        } else {
            db.collection("NguoiDung").document(userId)
                    .collection("BaiVietDaLuu").document(item.getId())
                    .set(item)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã lưu bài viết", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerLuuBaiViet != null) {
            listenerLuuBaiViet.remove();
        }
    }

    private void thietLapButtonLoc() {
        btnTatCa.setOnClickListener(v -> capNhatTagLoc(""));
        btnSucKhoe.setOnClickListener(v -> capNhatTagLoc("suckhoe"));
        btnDinhDuong.setOnClickListener(v -> capNhatTagLoc("dinhduong"));
        btnTapLuyen.setOnClickListener(v -> capNhatTagLoc("tapluyen"));
        
        // Mặc định chọn Tất cả
        capNhatGiaoDienButton("");
    }

    private void thietLapTimKiem() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tuKhoaTimKiem = query;
                xuLyLocBaiViet();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tuKhoaTimKiem = newText;
                xuLyLocBaiViet();
                return true;
            }
        });
    }

    private void capNhatTagLoc(String tag) {
        tagHienTai = tag;
        capNhatGiaoDienButton(tag);
        xuLyLocBaiViet();
    }

    private void capNhatGiaoDienButton(String selectedTag) {
        // Reset style cho tất cả
        setButtonStyle(btnTatCa, selectedTag.isEmpty());
        setButtonStyle(btnSucKhoe, selectedTag.equals("suckhoe"));
        setButtonStyle(btnDinhDuong, selectedTag.equals("dinhduong"));
        setButtonStyle(btnTapLuyen, selectedTag.equals("tapluyen"));
    }

    private void setButtonStyle(MaterialButton button, boolean isSelected) {
        button.setSelected(isSelected);
        if (isSelected) {
            button.setStrokeWidth(0);
        } else {
            button.setStrokeWidth(1);
        }
    }

    private void loadTatCaBaiViet() {
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
                            item.setId(doc.getId());
                            danhSachBaiViet.add(item);
                        }
                        xuLyLocBaiViet(); // Hiển thị kết hợp lọc
                    }
                });
    }

    private void xuLyLocBaiViet() {
        danhSachHienThi.clear();
        String filterPattern = tuKhoaTimKiem.toLowerCase().trim();

        for (BaiViet item : danhSachBaiViet) {
            // Kiểm tra Tag
            boolean matchesTag = tagHienTai.isEmpty() || (item.getTag() != null && item.getTag().equals(tagHienTai));
            
            // Kiểm tra Tiêu đề
            boolean matchesQuery = filterPattern.isEmpty() || (item.getTenBaiViet() != null && item.getTenBaiViet().toLowerCase().contains(filterPattern));

            if (matchesTag && matchesQuery) {
                danhSachHienThi.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
