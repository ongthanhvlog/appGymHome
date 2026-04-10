package com.example.gymhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymhome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText edtEmail, edtMatKhau, edtNhapLaiMatKhau;
    Button btnDangKy;
    TextView tvQuayLaiDangNhap, tvGuiLaiEmail;

    FirebaseAuth xacThucFirebase;
    FirebaseUser nguoiDung;
    CountDownTimer demNguoc;

    boolean choPhepGuiLai = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_register);

        xacThucFirebase = FirebaseAuth.getInstance();

        // Ánh xạ
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtNhapLaiMatKhau = findViewById(R.id.edtNhapLaiMatKhau);
        btnDangKy = findViewById(R.id.btnDangKy);
        tvQuayLaiDangNhap = findViewById(R.id.tvQuayLaiDangNhap);
        tvGuiLaiEmail = findViewById(R.id.tvGuiLaiEmail);

        btnDangKy.setOnClickListener(v -> xuLyDangKy());

        tvQuayLaiDangNhap.setOnClickListener(v ->
                startActivity(new Intent(Register.this, Login.class)));

        tvGuiLaiEmail.setOnClickListener(v -> {
            if (nguoiDung != null && choPhepGuiLai) {
                guiEmailXacNhan();
            }
        });
    }

    private void xuLyDangKy() {
        String email = edtEmail.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();
        String nhapLaiMatKhau = edtNhapLaiMatKhau.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập Email");
            return;
        }

        if (TextUtils.isEmpty(matKhau)) {
            edtMatKhau.setError("Vui lòng nhập mật khẩu");
            return;
        }

        if (!matKhau.equals(nhapLaiMatKhau)) {
            edtNhapLaiMatKhau.setError("Mật khẩu không khớp");
            return;
        }

        if (matKhau.length() < 6) {
            edtMatKhau.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }

        xacThucFirebase.createUserWithEmailAndPassword(email, matKhau)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        nguoiDung = xacThucFirebase.getCurrentUser();
                        String userId = nguoiDung.getUid();

                        // Tạo dữ liệu người dùng để lưu vào Firestore
                        Map<String, Object> thongTinNguoiDung = new HashMap<>();
                        thongTinNguoiDung.put("Email", email);
                        thongTinNguoiDung.put("SoLuongBaiHocDaDangKy", 0);
                        thongTinNguoiDung.put("UserId", userId);
                        thongTinNguoiDung.put("MatKhau", matKhau);

                        // Lưu vào Firestore với ID là UID của Auth
                        FirebaseFirestore.getInstance().collection("NguoiDung")
                                .document(userId)
                                .set(thongTinNguoiDung)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this,
                                            "Đăng ký thành công! Vui lòng xác nhận email.",
                                            Toast.LENGTH_LONG).show();

                                    tvGuiLaiEmail.setVisibility(TextView.VISIBLE);
                                    guiEmailXacNhan();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this,
                                            "Lỗi lưu thông tin: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(this,
                                "Lỗi: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guiEmailXacNhan() {
        if (nguoiDung == null) return;

        nguoiDung.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Đã gửi email xác nhận",
                                Toast.LENGTH_SHORT).show();
                        batDauDemNguoc();
                    } else {
                        Toast.makeText(this,
                                "Không thể gửi email",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void batDauDemNguoc() {
        choPhepGuiLai = false;
        tvGuiLaiEmail.setEnabled(false);

        demNguoc = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvGuiLaiEmail.setText(
                        "Gửi lại email (" + millisUntilFinished / 1000 + "s)"
                );
            }

            @Override
            public void onFinish() {
                choPhepGuiLai = true;
                tvGuiLaiEmail.setEnabled(true);
                tvGuiLaiEmail.setText("Gửi lại Email xác nhận");
            }
        }.start();
    }
}
