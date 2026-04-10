package com.example.gymhome.activity;

import android.app.AlertDialog;
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

import com.bumptech.glide.Glide;
import com.example.gymhome.R;
import com.example.gymhome.model.BaiTapLon;
import com.example.gymhome.model.BaiTapNho;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DangKyBaiTapLon extends AppCompatActivity {

    private ImageButton ibQuayLai;
    private ImageView imgHinhAnhBaiTapLon;
    private TextView tvTenBaiTapLon, tvCapDo, tvTongThoiGian, tvSoLuongBaiTapNho, tvMoTaBaiTapLon;
    private Button btnDangKyBaiTapLon;
    private boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dangkybaitaplon);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // anh xa id
        ibQuayLai = findViewById(R.id.ibQuayLai);
        imgHinhAnhBaiTapLon = findViewById(R.id.imgHinhAnhBaiTapLon);
        tvTenBaiTapLon = findViewById(R.id.textView14);
        tvCapDo = findViewById(R.id.tvCapDo);
        tvTongThoiGian = findViewById(R.id.tvTongThoiGian);
        tvSoLuongBaiTapNho = findViewById(R.id.tvSoLuongBaiTapCon);
        tvMoTaBaiTapLon = findViewById(R.id.tvMoTaBaiTapLon);
        btnDangKyBaiTapLon = findViewById(R.id.btnDangKyBaiTapLon);

        // nhan du lieu tu intent
        BaiTapLon item = (BaiTapLon) getIntent().getSerializableExtra("BaiTapLon");

        if (item != null) {
            tvTenBaiTapLon.setText(item.getTenBaiTapLon());
            tvCapDo.setText(item.getCapDo());
            tvTongThoiGian.setText(item.getThoiGian());
            tvSoLuongBaiTapNho.setText(String.valueOf(item.getSoLuongBaiTapNho()));
            tvMoTaBaiTapLon.setText(item.getMoTa());
            
            Glide.with(this)
                    .load(item.getHinhAnh())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgHinhAnhBaiTapLon);

            kiemTraTrangThaiDangKy(item);
        }

        ibQuayLai.setOnClickListener(v -> finish());
        
        btnDangKyBaiTapLon.setOnClickListener(v -> {
            if (item != null) {
                if (isRegistered) {
                    hienThiDialogXacNhanHuy(item);
                } else {
                    hienThiDialogXacNhanDangKy(item);
                }
            }
        });
    }

    private void kiemTraTrangThaiDangKy(BaiTapLon item) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        FirebaseFirestore.getInstance().collection("NguoiDung")
                .document(userId)
                .collection("DanhSachBaiTapLonDaDangKy")
                .document(item.getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        isRegistered = true;
                        btnDangKyBaiTapLon.setText("Hủy Đăng Ký Bài Tập");
                        btnDangKyBaiTapLon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY));
                    } else {
                        isRegistered = false;
                        btnDangKyBaiTapLon.setText("Đăng Ký Bài Tập");
                        btnDangKyBaiTapLon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF5252")));
                    }
                });
    }

    private void hienThiDialogXacNhanHuy(BaiTapLon item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận hủy đăng ký");
        builder.setMessage("Bạn có muốn hủy đăng ký bài tập \"" + item.getTenBaiTapLon() + "\" không?");

        builder.setPositiveButton("Có", (dialog, which) -> {
            thucHienHuyDangKy(item);
        });

        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void thucHienHuyDangKy(BaiTapLon item) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        FirebaseFirestore.getInstance().collection("NguoiDung")
                .document(userId)
                .collection("DanhSachBaiTapLonDaDangKy")
                .document(item.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã hủy đăng ký thành công", Toast.LENGTH_SHORT).show();
                    kiemTraTrangThaiDangKy(item); // Cập nhật lại trạng thái nút
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi hủy đăng ký: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void hienThiDialogXacNhanDangKy(BaiTapLon item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận đăng ký");
        builder.setMessage("Bạn có muốn đăng ký bài tập \"" + item.getTenBaiTapLon() + "\" không?");
        
        builder.setPositiveButton("Có", (dialog, which) -> {
            thucHienDangKy(item);
        });

        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void thucHienDangKy(BaiTapLon item) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đăng ký", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String vungId = getIntent().getStringExtra("VungId");

        if (vungId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin vùng tập", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("VungTapTrung")
                .document(vungId)
                .collection("DanhSachBaiTapLon")
                .document(item.getId())
                .collection("DanhSachBaiTapNho")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<BaiTapNho> danhSachBaiTapNho = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            BaiTapNho nho = doc.toObject(BaiTapNho.class);
                            nho.setId(doc.getId());
                            danhSachBaiTapNho.add(nho);
                        }
                        luuVaoDanhSachDaDangKy(userId, item, danhSachBaiTapNho);
                    } else {
                        Toast.makeText(this, "Lỗi lấy danh sách bài tập nhỏ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void luuVaoDanhSachDaDangKy(String userId, BaiTapLon item, List<BaiTapNho> danhSachBaiTapNho) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("NguoiDung")
                .document(userId)
                .collection("DanhSachBaiTapLonDaDangKy")
                .document(item.getId())
                .set(item)
                .addOnSuccessListener(aVoid -> {
                    if (danhSachBaiTapNho.isEmpty()) {
                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    AtomicInteger count = new AtomicInteger(0);
                    for (BaiTapNho nho : danhSachBaiTapNho) {
                        db.collection("NguoiDung")
                                .document(userId)
                                .collection("DanhSachBaiTapLonDaDangKy")
                                .document(item.getId())
                                .collection("DanhSachBaiTapNho")
                                .document(nho.getId())
                                .set(nho)
                                .addOnCompleteListener(t -> {
                                    if (count.incrementAndGet() == danhSachBaiTapNho.size()) {
                                        Toast.makeText(this, "Đăng ký thành công bài tập: " + item.getTenBaiTapLon(), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi đăng ký: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
