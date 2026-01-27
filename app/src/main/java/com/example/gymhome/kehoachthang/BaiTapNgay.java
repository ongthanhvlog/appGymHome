package com.example.gymhome.kehoachthang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymhome.R;

public class BaiTapNgay extends AppCompatActivity {
    ImageButton ibQuayLai;
    Button btnCaiDatTapLuyen, btnBatDauTapLuyen;
    CardView CV_BaiTapCon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cardview_baitapngay);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //anh xa id
        ibQuayLai = findViewById(R.id.ibQuayLai);
        btnCaiDatTapLuyen = findViewById(R.id.btnCaiDatTapLuyen);
        btnBatDauTapLuyen = findViewById(R.id.btnBatDauTapLuyen);
        CV_BaiTapCon = findViewById(R.id.CV_BaiTapCon);
        //xu ly click
        ibQuayLai.setOnClickListener(v -> {
            finish();
        });
        CV_BaiTapCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaiTapNgay.this, BaiTapCon.class);
                startActivity(intent);
            }
        });
        btnBatDauTapLuyen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaiTapNgay.this, BaiTapCon.class);
                startActivity(intent);
            }
        });
    }
}