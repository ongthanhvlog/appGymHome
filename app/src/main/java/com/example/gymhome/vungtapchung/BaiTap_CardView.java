package com.example.gymhome.vungtapchung;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.AlertDialog;
import com.example.gymhome.R;

public class BaiTap_CardView extends AppCompatActivity {
    ImageButton ibQuayLai2;
    Button btnDangKyBaiTap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cardview_baitap);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //anh xa id
        ibQuayLai2 = findViewById(R.id.ibQuayLai);
        btnDangKyBaiTap = findViewById(R.id.btnDangKyBaiTap);
        //xu ly click
        ibQuayLai2.setOnClickListener(v->{
            finish();
        });
        btnDangKyBaiTap.setOnClickListener(v -> {
            new AlertDialog.Builder(BaiTap_CardView.this)
                    .setTitle("Xác nhận đăng ký")
                    .setMessage("Bạn có muốn đăng ký bài tập này?")
                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setNegativeButton("Không đồng ý", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });

    }
}