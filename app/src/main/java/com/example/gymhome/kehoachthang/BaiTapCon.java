package com.example.gymhome.kehoachthang;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymhome.R;

public class BaiTapCon extends AppCompatActivity {
    ImageButton ibQuayLai, ibPrevious, ibPause, ibNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cardview_baitapcon);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //anh xa id
        ibQuayLai = findViewById(R.id.ibQuayLai);
        ibPrevious = findViewById(R.id.ibPrevious);
        ibPause = findViewById(R.id.ibPause);
        ibNext = findViewById(R.id.ibNext);
        //xu ly click
        ibQuayLai.setOnClickListener(v -> {
            finish();
        });
    }
}