package com.example.gymhome.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gymhome.R;
import com.example.gymhome.fragment.BaoCao_Fragment;
import com.example.gymhome.fragment.CaNhan_Fragment;
import com.example.gymhome.fragment.DinhDuong_Fragment;
import com.example.gymhome.fragment.TapLuyen_Fragment;
import com.example.gymhome.fragment.TrangChu_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

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
