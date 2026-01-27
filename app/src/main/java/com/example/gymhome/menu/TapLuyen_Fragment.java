package com.example.gymhome.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gymhome.R;
import com.example.gymhome.kehoachthang.BaiTapNgay;

public class TapLuyen_Fragment extends Fragment {

    TextView tabBeginner, tabAdvanced, tabRegistered;
    ScrollView monthlyPlanContent, registeredListContent;
    CardView CV_BaiTapNgay;

    public TapLuyen_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tap_luyen, container, false);

        //anh xa id
        tabBeginner = view.findViewById(R.id.tabBeginner);
        tabAdvanced = view.findViewById(R.id.tabAdvanced);
        tabRegistered = view.findViewById(R.id.tabRegistered);
        monthlyPlanContent = view.findViewById(R.id.kehoach1thang);
        registeredListContent = view.findViewById(R.id.baitapdadangky);
        CV_BaiTapNgay = view.findViewById(R.id.CV_BaiTapCon);

        // Set default tab
        selectTab(tabBeginner);

        tabBeginner.setOnClickListener(v -> selectTab(tabBeginner));
        tabAdvanced.setOnClickListener(v -> selectTab(tabAdvanced));
        tabRegistered.setOnClickListener(v -> selectTab(tabRegistered));

        CV_BaiTapNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BaiTapNgay.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void selectTab(TextView selectedTab) {
        // Reset all tabs to default state
        tabBeginner.setSelected(false);
        tabBeginner.setBackground(null);
        tabAdvanced.setSelected(false);
        tabAdvanced.setBackground(null);
        tabRegistered.setSelected(false);
        tabRegistered.setBackground(null);

        // Set the selected tab to active state
        selectedTab.setSelected(true);
        selectedTab.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.tab_selected_bg));

        // Show/hide content based on the selected tab
        if (selectedTab.getId() == R.id.tabRegistered) {
            monthlyPlanContent.setVisibility(View.GONE);
            registeredListContent.setVisibility(View.VISIBLE);
        } else {
            monthlyPlanContent.setVisibility(View.VISIBLE);
            registeredListContent.setVisibility(View.GONE);
            // Here you can add logic to load different content for "Beginner" and "Advanced"
            // if needed, but for now they share the same layout.
        }
    }
}
