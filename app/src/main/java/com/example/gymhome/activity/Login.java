package com.example.gymhome.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymhome.R;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText edtEmail, edtMatKhau;
    Button btnDangNhap;
    TextView tvDangKy, tvQuenMatKhau;
    CheckBox cbLuuDangNhap;

    FirebaseAuth xacThucFirebase;
    SharedPreferences luuDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra đã lưu đăng nhập chưa
        luuDangNhap = getSharedPreferences("LUU_DANG_NHAP", MODE_PRIVATE);
        if (luuDangNhap.getBoolean("DA_NHO", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        xacThucFirebase = FirebaseAuth.getInstance();

        // Ánh xạ view
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        tvDangKy = findViewById(R.id.tvDangKy);
        tvQuenMatKhau = findViewById(R.id.tvQuenMatKhau);
        cbLuuDangNhap = findViewById(R.id.cbLuuDangNhap);

        btnDangNhap.setOnClickListener(v -> xuLyDangNhap());

        tvDangKy.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        tvQuenMatKhau.setOnClickListener(v -> xuLyQuenMatKhau());
    }

    private void xuLyDangNhap() {
        String email = edtEmail.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(matKhau)) {
            Toast.makeText(this,
                    "Vui lòng nhập đầy đủ thông tin",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        xacThucFirebase.signInWithEmailAndPassword(email, matKhau)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        if (cbLuuDangNhap.isChecked()) {
                            luuDangNhap.edit()
                                    .putBoolean("DA_NHO", true)
                                    .apply();
                        }

                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this,
                                "Email hoặc mật khẩu không đúng",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void xuLyQuenMatKhau() {
        String email = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this,
                    "Vui lòng nhập email",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        xacThucFirebase.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,
                                "Đã gửi email khôi phục mật khẩu",
                                Toast.LENGTH_LONG).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }
}
