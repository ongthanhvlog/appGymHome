package com.example.gymhome.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymhome.R;
import com.example.gymhome.activity.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CaNhan_Fragment extends Fragment {

    // View
    TextView tvTenDangNhap;
    Button btnDangXuat, btnDoiMatKhau;

    // Firebase
    FirebaseAuth xacThucFirebase;
    FirebaseUser nguoiDungHienTai;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View giaoDien = inflater.inflate(R.layout.canhan, container, false);

        // Ánh xạ view
        anhXaView(giaoDien);

        // Khởi tạo Firebase
        xacThucFirebase = FirebaseAuth.getInstance();
        nguoiDungHienTai = xacThucFirebase.getCurrentUser();

        // Hiển thị email đăng nhập
        hienThiTenDangNhap();

        // Sự kiện đăng xuất
        btnDangXuat.setOnClickListener(v -> hienThiHopThoaiDangXuat());

        // Sự kiện đổi mật khẩu
        btnDoiMatKhau.setOnClickListener(v -> hienThiHopThoaiDoiMatKhau());

        return giaoDien;
    }

    // ÁNH XẠ VIEW
    private void anhXaView(View giaoDien) {
        tvTenDangNhap = giaoDien.findViewById(R.id.tvTenDangNhap);
        btnDangXuat = giaoDien.findViewById(R.id.btnDangXuat);
        btnDoiMatKhau = giaoDien.findViewById(R.id.btnDoiMatKhau);
    }

    // HIỂN THỊ EMAIL NGƯỜI DÙNG
    private void hienThiTenDangNhap() {
        if (nguoiDungHienTai != null) {
            tvTenDangNhap.setText(nguoiDungHienTai.getEmail());
        } else {
            tvTenDangNhap.setText("Chưa đăng nhập");
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
}
