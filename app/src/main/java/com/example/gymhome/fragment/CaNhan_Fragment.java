package com.example.gymhome.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymhome.R;
import com.example.gymhome.activity.Login;
import com.example.gymhome.adapter.ChamSocKhachHangAdapter;
import com.example.gymhome.model.ChamSocKhachHang;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CaNhan_Fragment extends Fragment {

    // View
    TextView tvEmail;
    Button btnDangXuat, btnDoiMatKhau;
    LinearLayout layoutDanhGia;

    // Firebase
    FirebaseAuth xacThucFirebase;
    FirebaseUser nguoiDungHienTai;
    FirebaseFirestore db;

    // Listener & Adapter cho dialog
    ListenerRegistration yeuCauListener;
    ChamSocKhachHangAdapter adapter;
    List<ChamSocKhachHang> dsYeuCau;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View giaoDien = inflater.inflate(R.layout.canhan, container, false);

        // Khởi tạo Firebase
        xacThucFirebase = FirebaseAuth.getInstance();
        nguoiDungHienTai = xacThucFirebase.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ view
        anhXaView(giaoDien);

        // Hiển thị email đăng nhập
        hienThiEmail();

        // Sự kiện đăng xuất
        btnDangXuat.setOnClickListener(v -> hienThiHopThoaiDangXuat());

        // Sự kiện đổi mật khẩu
        btnDoiMatKhau.setOnClickListener(v -> hienThiHopThoaiDoiMatKhau());

        // Sự kiện Đánh giá/Yêu cầu
        layoutDanhGia.setOnClickListener(v -> hienThiDialogDanhSachYeuCau());

        return giaoDien;
    }

    // ÁNH XẠ VIEW
    private void anhXaView(View giaoDien) {
        tvEmail = giaoDien.findViewById(R.id.tvTenDangNhap);
        btnDangXuat = giaoDien.findViewById(R.id.btnDangXuat);
        btnDoiMatKhau = giaoDien.findViewById(R.id.btnDoiMatKhau);
        layoutDanhGia = giaoDien.findViewById(R.id.layoutDanhGia);
    }

    // HIỂN THỊ EMAIL NGƯỜI DÙNG
    private void hienThiEmail() {
        if (nguoiDungHienTai != null) {
            tvEmail.setText(nguoiDungHienTai.getEmail());
        } else {
            tvEmail.setText("Chưa đăng nhập");
        }
    }

    // HỘP THOẠI ĐĂNG XUẤT
    private void hienThiHopThoaiDangXuat() {
        new AlertDialog.Builder(getContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Có", (dialog, which) -> thucHienDangXuat())
                .setNegativeButton("Không", null)
                .show();
    }

    // THỰC HIỆN ĐĂNG XUẤT
    private void thucHienDangXuat() {
        // Xóa trạng thái lưu đăng nhập
        SharedPreferences luuDangNhap = getActivity().getSharedPreferences(
                "LUU_DANG_NHAP",
                android.content.Context.MODE_PRIVATE
        );
        luuDangNhap.edit().putBoolean("DA_NHO", false).apply();

        // Đăng xuất khỏi Firebase
        xacThucFirebase.signOut();

        Toast.makeText(getContext(),
                "Đã đăng xuất",
                Toast.LENGTH_SHORT).show();

        Intent manHinhDangNhap = new Intent(getActivity(), Login.class);
        manHinhDangNhap.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        startActivity(manHinhDangNhap);
        getActivity().finish();
    }

    // HỘP THOẠI ĐỔI MẬT KHẨU
    private void hienThiHopThoaiDoiMatKhau() {
        EditText edtMatKhauMoi = new EditText(getContext());
        edtMatKhauMoi.setHint("Nhập mật khẩu mới");
        edtMatKhauMoi.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        new AlertDialog.Builder(getContext())
                .setTitle("Đổi mật khẩu")
                .setView(edtMatKhauMoi)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String matKhauMoi = edtMatKhauMoi.getText().toString().trim();
                    capNhatMatKhauMoi(matKhauMoi);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // CẬP NHẬT MẬT KHẨU FIREBASE
    private void capNhatMatKhauMoi(String matKhauMoi) {
        if (matKhauMoi.isEmpty()) {
            Toast.makeText(getContext(),
                    "Mật khẩu không được để trống",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (matKhauMoi.length() < 6) {
            Toast.makeText(getContext(),
                    "Mật khẩu phải từ 6 ký tự",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (nguoiDungHienTai != null) {
            nguoiDungHienTai.updatePassword(matKhauMoi)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Đổi mật khẩu thành công",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(),
                                    "Đổi mật khẩu thất bại, vui lòng đăng nhập lại",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // DIALOG DANH SÁCH YÊU CẦU
    private void hienThiDialogDanhSachYeuCau() {
        android.app.Dialog dialog = new android.app.Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_danh_sach_yeu_cau);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        ImageView imgClose = dialog.findViewById(R.id.imgClose);
        RecyclerView rvDanhSachYeuCau = dialog.findViewById(R.id.rvDanhSachYeuCau);
        Button btnThemYeuCau = dialog.findViewById(R.id.btnThemYeuCau);

        dsYeuCau = new ArrayList<>();
        adapter = new ChamSocKhachHangAdapter(getContext(), dsYeuCau);
        rvDanhSachYeuCau.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDanhSachYeuCau.setAdapter(adapter);

        layDanhSachYeuCau();

        imgClose.setOnClickListener(v -> dialog.dismiss());
        btnThemYeuCau.setOnClickListener(v -> hienThiDialogThemYeuCau());

        dialog.setOnDismissListener(d -> {
            if (yeuCauListener != null) {
                yeuCauListener.remove();
                yeuCauListener = null;
            }
        });

        dialog.show();
    }

    // LẤY DANH SÁCH YÊU CẦU TỪ FIRESTORE
    private void layDanhSachYeuCau() {
        if (nguoiDungHienTai == null) return;

        if (yeuCauListener != null) yeuCauListener.remove();

        // Bỏ .orderBy() để không cần tạo Index trên Firebase Console
        yeuCauListener = db.collection("ChamSocKhachHang")
                .whereEqualTo("Email", nguoiDungHienTai.getEmail())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        android.util.Log.e("FIRESTORE_ERROR", "Lỗi: " + error.getMessage());
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        return;
                    }

                    if (value != null) {
                        dsYeuCau.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            ChamSocKhachHang item = doc.toObject(ChamSocKhachHang.class);
                            item.setId(doc.getId());
                            dsYeuCau.add(item);
                        }

                        // Sắp xếp danh sách cục bộ (Mới nhất lên đầu)
                        Collections.sort(dsYeuCau, (o1, o2) -> {
                            if (o1.getThoiGianTao() == null || o2.getThoiGianTao() == null) return 0;
                            return o2.getThoiGianTao().compareTo(o1.getThoiGianTao());
                        });

                        adapter.notifyDataSetChanged();
                        android.util.Log.d("FIRESTORE_SYNC", "Đã cập nhật " + dsYeuCau.size() + " yêu cầu");
                    }
                });
    }

    // DIALOG THÊM YÊU CẦU MỚI
    private void hienThiDialogThemYeuCau() {
        android.app.Dialog dialog = new android.app.Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_them_yeu_cau);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER);
        }

        EditText edtNoiDung = dialog.findViewById(R.id.edtNoiDung);
        Button btnHuy = dialog.findViewById(R.id.btnHuy);
        Button btnGui = dialog.findViewById(R.id.btnGui);

        btnHuy.setOnClickListener(v -> dialog.dismiss());

        btnGui.setOnClickListener(v -> {
            String noiDung = edtNoiDung.getText().toString().trim();

            if (noiDung.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tự động tạo tiêu đề từ 3 chữ đầu của nội dung
            String[] words = noiDung.split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(3, words.length); i++) {
                sb.append(words[i]).append(" ");
            }
            String tieuDe = sb.toString().trim();
            if (words.length > 3) tieuDe += "...";

            guiYeuCau(tieuDe, noiDung, dialog);
        });

        dialog.show();
    }

    // GỬI YÊU CẦU LÊN FIRESTORE
    private void guiYeuCau(String tieuDe, String noiDung, android.app.Dialog dialog) {
        if (nguoiDungHienTai == null) return;

        ChamSocKhachHang item = new ChamSocKhachHang();
        item.setEmail(nguoiDungHienTai.getEmail());
        item.setTieuDe(tieuDe);
        item.setNoiDung(noiDung);
        item.setThoiGianTao(Timestamp.now());
        item.setTrangThai(1); // 1: Đang chờ xử lý

        db.collection("ChamSocKhachHang")
                .add(item)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Gửi yêu cầu thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    // Listener (SnapshotListener) sẽ tự động cập nhật danh sách ngay lập tức
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
