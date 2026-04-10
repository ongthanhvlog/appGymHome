package com.example.gymhome.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymhome.R;
import com.example.gymhome.activity.DanhSachThuThach;
import com.example.gymhome.adapter.ThuThachAdapter;
import com.example.gymhome.adapter.VungTapTrungAdapter;
import com.example.gymhome.model.ThuThach;
import com.example.gymhome.model.VungTapTrung;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TrangChu_Fragment extends Fragment {

    private RecyclerView rvVungTapTrung, rvThuThachTrangChu;
    private VungTapTrungAdapter adapter;
    private ThuThachAdapter thuThachAdapter;
    private List<VungTapTrung> vungTapTrungList;
    private List<ThuThach> thuThachList;
    private FirebaseFirestore db;
    private TextView tvXemTatCaThuThach;

    public TrangChu_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.trangchu, container, false);

        db = FirebaseFirestore.getInstance();

        // Vùng tập trung
        rvVungTapTrung = view.findViewById(R.id.rvVungTapTrung);
        rvVungTapTrung.setLayoutManager(new GridLayoutManager(getContext(), 2));

        vungTapTrungList = new ArrayList<>();
        adapter = new VungTapTrungAdapter(vungTapTrungList, item -> {
            Intent intent = new Intent(getActivity(), com.example.gymhome.activity.VungTapTrung.class);
            intent.putExtra("VungId", item.getId());
            intent.putExtra("TenVung", item.getTenVung());
            intent.putExtra("HinhAnh", item.getHinhAnh());
            intent.putExtra("MoTa", item.getMoTa());
            startActivity(intent);
        });
        rvVungTapTrung.setAdapter(adapter);
        rvThuThachTrangChu = view.findViewById(R.id.rvThuThach);
        rvThuThachTrangChu.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        rvThuThachTrangChu.setNestedScrollingEnabled(false);
        
        thuThachList = new ArrayList<>();
        thuThachAdapter = new ThuThachAdapter(thuThachList, item -> {
            Intent intent = new Intent(getActivity(), com.example.gymhome.activity.ChiTietThuThach.class);
            intent.putExtra("ThuThachData", item);
            startActivity(intent);
        });
        rvThuThachTrangChu.setAdapter(thuThachAdapter);
        tvXemTatCaThuThach = view.findViewById(R.id.tvXemTatCaThuThach);
        tvXemTatCaThuThach.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DanhSachThuThach.class);
            startActivity(intent);
        });

        loadVungTapTrungData();
        loadThuThachData();

        return view;
    }

    private void loadThuThachData() {
        db.collection("ThuThach")
                .limit(2)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        thuThachList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ThuThach item = document.toObject(ThuThach.class);
                            item.setId(document.getId());
                            thuThachList.add(item);
                        }
                        thuThachAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadVungTapTrungData() {
        db.collection("VungTapTrung")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        vungTapTrungList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            VungTapTrung vung = document.toObject(VungTapTrung.class);
                            vung.setId(document.getId());
                            vungTapTrungList.add(vung);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Lỗi lấy dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
