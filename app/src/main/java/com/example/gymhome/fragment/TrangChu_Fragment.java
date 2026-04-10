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
import android.widget.Toast;

import com.example.gymhome.R;
import com.example.gymhome.adapter.VungTapTrungAdapter;
import com.example.gymhome.model.VungTapTrung;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TrangChu_Fragment extends Fragment {

    private RecyclerView rvVungTapTrung;
    private VungTapTrungAdapter adapter;
    private List<VungTapTrung> vungTapTrungList;
    private FirebaseFirestore db;

    public TrangChu_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.trangchu, container, false);

        db = FirebaseFirestore.getInstance();

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

        loadVungTapTrungData();

        return view;
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
