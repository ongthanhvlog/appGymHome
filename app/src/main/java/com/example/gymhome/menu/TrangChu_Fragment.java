package com.example.gymhome.menu;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gymhome.R;
import com.example.gymhome.vungtapchung.VungTapChung_CardView;

public class TrangChu_Fragment extends Fragment {

    CardView CV_vtcNguc, CV_vtcCanhTayVaVai, CV_vtcChan, CV_vtcLung, CV_vtcBung, CV_vtcTioanThan;

    public TrangChu_Fragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trang_chu, container, false);

        CV_vtcNguc = view.findViewById(R.id.CV_vtcNguc);
        CV_vtcCanhTayVaVai = view.findViewById(R.id.CV_vtcCanhTayVaVai);
        CV_vtcChan = view.findViewById(R.id.CV_vtcChan);
        CV_vtcLung = view.findViewById(R.id.CV_vtcLung);
        CV_vtcBung = view.findViewById(R.id.CV_vtcBung);
        CV_vtcTioanThan = view.findViewById(R.id.CV_vtcTioanThan);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VungTapChung_CardView.class);
                startActivity(intent);
            }
        };

        CV_vtcNguc.setOnClickListener(listener);
        CV_vtcCanhTayVaVai.setOnClickListener(listener);
        CV_vtcChan.setOnClickListener(listener);
        CV_vtcLung.setOnClickListener(listener);
        CV_vtcBung.setOnClickListener(listener);
        CV_vtcTioanThan.setOnClickListener(listener);

        return view;
    }
}
