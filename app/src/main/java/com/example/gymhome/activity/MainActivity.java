package com.example.gymhome.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gymhome.R;
import com.example.gymhome.fragment.BaoCao_Fragment;
import com.example.gymhome.fragment.CaNhan_Fragment;
import com.example.gymhome.fragment.DinhDuong_Fragment;
import com.example.gymhome.fragment.TapLuyen_Fragment;
import com.example.gymhome.fragment.TrangChu_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    // Đăng ký launcher để xử lý kết quả xin quyền thông báo
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d("FCM", "Quyền thông báo đã được cấp");
                    com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("gymhome_thongbao");
                } else {
                    Log.d("FCM", "Người dùng từ chối quyền thông báo");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Kiểm tra và xin quyền thông báo mỗi khi mở app
        yeuCauQuyenThongBao();

        // Lấy và lưu FCM Token cho thông báo cá nhân
        luuTokenThongBao();

        // Đổi màu cho bottom nav menu
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };

        int[] colors = new int[]{
                ContextCompat.getColor(this, R.color.mau_icon_chon),
                ContextCompat.getColor(this, R.color.mau_icon_mac_dinh)
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavigationView.setItemIconTintList(colorStateList);
        bottomNavigationView.setItemTextColor(colorStateList);

        // Mặc định mở Trang Chủ
        loadFragment(new TrangChu_Fragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            int id = item.getItemId();
            if (id == R.id.menu_home) {
                fragment = new TrangChu_Fragment();
            } else if (id == R.id.menu_workout) {
                fragment = new TapLuyen_Fragment();
            } else if (id == R.id.menu_nutrition) {
                fragment = new DinhDuong_Fragment();
            } else if (id == R.id.menu_report) {
                fragment = new BaoCao_Fragment();
            } else if (id == R.id.menu_profile) {
                fragment = new CaNhan_Fragment();
            }

            return loadFragment(fragment);
        });
    }

    private void yeuCauQuyenThongBao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                // Nếu chưa có quyền, hiển thị Dialog tùy chỉnh (không bị Android hạn chế)
                hienThiDialogYeuCauQuyen();
            }
        }
    }

    private void hienThiDialogYeuCauQuyen() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Bật thông báo nhắc nhở 🔔")
                .setMessage("Đừng để lỡ lịch tập luyện! GymHome cần quyền thông báo để đồng hành cùng bạn mỗi ngày.")
                .setCancelable(false)
                .setPositiveButton("Cấp quyền", (dialog, which) -> {
                    // Gọi bảng xin quyền của hệ thống
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);

                    // Nếu người dùng đã từng từ chối nhiều lần, hệ thống sẽ không hiện bảng nữa.
                    // Lúc này, chúng ta cần hướng dẫn họ vào Cài đặt.
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                        openAppSettings();
                    }
                })
                .setNegativeButton("Để sau", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void luuTokenThongBao() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("FCM", "Lấy token thất bại", task.getException());
                return;
            }

            String token = task.getResult();
            // Lưu token và múi giờ vào Firestore của người dùng hiện tại
            Map<String, Object> data = new HashMap<>();
            data.put("fcmToken", token);
            data.put("timezone", java.util.TimeZone.getDefault().getID());

            FirebaseFirestore.getInstance().collection("NguoiDung")
                    .document(userId)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("FCM", "Đã cập nhật Token thành công"))
                    .addOnFailureListener(e -> Log.e("FCM", "Lỗi lưu token", e));
        });
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}
