package com.example.gymhome.vungtapchung;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymhome.R;

public class VungTapChung_CardView extends AppCompatActivity {
    ImageButton ibQuayLai;
    CardView CV_BaiTap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cardview_vungtapchung);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //anh xa id
        ibQuayLai = findViewById(R.id.ibQuayLai);
        CV_BaiTap = findViewById(R.id.CV_BaiTapCon);

        //xu ly click
        ibQuayLai.setOnClickListener(v -> {
            finish();
        });
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VungTapChung_CardView.this, BaiTap_CardView.class);
                startActivity(intent);
            }
        };
        CV_BaiTap.setOnClickListener(listener);

    }
}