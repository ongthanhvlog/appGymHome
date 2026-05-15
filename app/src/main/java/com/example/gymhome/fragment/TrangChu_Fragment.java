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

import com.example.gymhome.R;
import com.example.gymhome.adapter.ThuThachAdapter;
import com.example.gymhome.adapter.VungTapTrungAdapter;
import com.example.gymhome.model.ThuThach;
import com.example.gymhome.model.VungTapTrung;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import com.example.gymhome.adapter.BaiVietAdapter;
import com.example.gymhome.model.BaiViet;

import android.widget.ProgressBar;

public class TrangChu_Fragment extends Fragment {

    private RecyclerView rvVungTapTrung, rvThuThachTrangChu, rvBaiVietMoi;
    private VungTapTrungAdapter adapter;
    private ThuThachAdapter thuThachAdapter;
    private BaiVietAdapter baiVietAdapter;
    private List<VungTapTrung> danhSachVungTapTrung;
    private List<ThuThach> danhSachThuThach;
    private List<BaiViet> danhSachBaiViet;
    private FirebaseFirestore db;
    private TextView tvXemTatCaThuThach, tvXemTatCaBaiViet, tvErrorBaiViet;
    private ProgressBar pbLoadingBaiViet;
    private com.google.firebase.firestore.ListenerRegistration vungTapTrungListener, thuThachListener, baiVietListener;

    public TrangChu_Fragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.trangchu, container, false);
        db = FirebaseFirestore.getInstance();

        // Setup UI
        rvVungTapTrung = view.findViewById(R.id.rvVungTapTrung);
        rvVungTapTrung.setLayoutManager(new GridLayoutManager(getContext(), 2));
        danhSachVungTapTrung = new ArrayList<>();
        adapter = new VungTapTrungAdapter(danhSachVungTapTrung, item -> {
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
        danhSachThuThach = new ArrayList<>();
        thuThachAdapter = new ThuThachAdapter(danhSachThuThach, item -> {
            Intent intent = new Intent(getActivity(), com.example.gymhome.activity.ChiTietThuThach.class);
            intent.putExtra("ThuThachData", item);
            startActivity(intent);
        });
        rvThuThachTrangChu.setAdapter(thuThachAdapter);

        rvBaiVietMoi = view.findViewById(R.id.rvBaiVietMoi);
        pbLoadingBaiViet = view.findViewById(R.id.pbLoadingBaiViet);
        tvErrorBaiViet = view.findViewById(R.id.tvErrorBaiViet);
        tvXemTatCaBaiViet = view.findViewById(R.id.tvXemTatCaBaiViet);

        rvBaiVietMoi.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        danhSachBaiViet = new ArrayList<>();
        baiVietAdapter = new BaiVietAdapter(danhSachBaiViet, item -> {
            Intent intent = new Intent(getActivity(), com.example.gymhome.activity.ChiTietBaiVietActivity.class);
            intent.putExtra("BaiViet", item);
            startActivity(intent);
        });
        rvBaiVietMoi.setAdapter(baiVietAdapter);

        tvXemTatCaBaiViet.setOnClickListener(v -> startActivity(new Intent(getActivity(), com.example.gymhome.activity.DanhSachBaiVietActivity.class)));
        
        tvXemTatCaThuThach = view.findViewById(R.id.tvXemTatCaThuThach);
        tvXemTatCaThuThach.setOnClickListener(v -> startActivity(new Intent(getActivity(), com.example.gymhome.activity.DanhSachThuThach.class)));

        loadVungTapTrungData();
        loadThuThachData();
        loadBaiVietData();

        return view;
    }

    private void loadThuThachData() {
        thuThachListener = db.collection("ThuThach").limit(2).addSnapshotListener((value, error) -> {
            if (!isAdded()) return;
            if (error != null) return;
            if (value != null) {
                danhSachThuThach.clear();
                for (QueryDocumentSnapshot document : value) {
                    ThuThach item = document.toObject(ThuThach.class);
                    item.setId(document.getId());
                    danhSachThuThach.add(item);
                }
                thuThachAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadVungTapTrungData() {
        vungTapTrungListener = db.collection("VungTapTrung").addSnapshotListener((value, error) -> {
            if (!isAdded()) return;
            if (error != null) return;
            if (value != null) {
                danhSachVungTapTrung.clear();
                for (QueryDocumentSnapshot document : value) {
                    VungTapTrung vung = document.toObject(VungTapTrung.class);
                    vung.setId(document.getId());
                    danhSachVungTapTrung.add(vung);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadBaiVietData() {
        pbLoadingBaiViet.setVisibility(View.VISIBLE);
        tvErrorBaiViet.setVisibility(View.GONE);

        baiVietListener = db.collection("BaiViet")
                .orderBy("ngayDang", Query.Direction.DESCENDING)
                .limit(3)
                .addSnapshotListener((value, error) -> {
                    if (!isAdded()) return;

                    pbLoadingBaiViet.setVisibility(View.GONE);
                    if (error != null) {
                        Log.e("FirestoreError", "Lỗi lấy bài viết: " + error.getMessage());
                        tvErrorBaiViet.setText("Lỗi kết nối Firestore");
                        tvErrorBaiViet.setVisibility(View.VISIBLE);
                        return;
                    }

                    if (value != null) {
                        Log.d("FirestoreData", "Số lượng bài viết nhận được: " + value.size());
                        if (value.isEmpty()) {
                            tvErrorBaiViet.setText("Chưa có bài viết nào.");
                            tvErrorBaiViet.setVisibility(View.VISIBLE);
                        } else {
                            tvErrorBaiViet.setVisibility(View.GONE);
                        }

                        danhSachBaiViet.clear();
                        for (QueryDocumentSnapshot document : value) {
                            BaiViet item = document.toObject(BaiViet.class);
                            danhSachBaiViet.add(item);
                        }
                        baiVietAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vungTapTrungListener != null) vungTapTrungListener.remove();
        if (thuThachListener != null) thuThachListener.remove();
        if (baiVietListener != null) baiVietListener.remove();
    }
}
