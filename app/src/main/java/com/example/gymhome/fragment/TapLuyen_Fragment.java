package com.example.gymhome.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymhome.R;
import com.example.gymhome.activity.DanhSachBaiTapLon;
import com.example.gymhome.activity.DanhSachBaiTapNho;
import com.example.gymhome.adapter.BaiTapLonAdapter;
import com.example.gymhome.adapter.NgayAdapter;
import com.example.gymhome.model.BaiTapLon;
import com.example.gymhome.model.Ngay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TapLuyen_Fragment extends Fragment {

    private TextView tabNguoiMoiBatDau, tabNangCao, tabBaiTapDaDangKy;
    private View viewBaiTapDaDangKy;
    private RecyclerView rvNgay, rvBaiTapDaDangKy;
    private NgayAdapter adapter;
    private BaiTapLonAdapter daDangKyAdapter;
    private List<Ngay> danhSachNgay;
    private List<BaiTapLon> danhSachBaiTapDaDangKy;
    private FirebaseFirestore db;
    private String phanLoai = "NguoiMoiBatDau";

    public TapLuyen_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tapluyen, container, false);

        db = FirebaseFirestore.getInstance();

        // Ánh xạ View
        tabNguoiMoiBatDau = view.findViewById(R.id.tabNguoiMoiBatDau);
        tabNangCao = view.findViewById(R.id.tabNangCao);
        tabBaiTapDaDangKy = view.findViewById(R.id.tabBaiTapDaDangKy);
        viewBaiTapDaDangKy = view.findViewById(R.id.baitapdadangky);
        rvNgay = view.findViewById(R.id.rvNgay);

        // Ánh xạ RecyclerView cho bài tập đã đăng ký
        rvBaiTapDaDangKy = view.findViewById(R.id.rvBaiTapDaDangKy);
        if (rvBaiTapDaDangKy == null) {
            // Nếu chưa có trong layout thì chúng ta sẽ xử lý sau hoặc giả định nó tồn tại trong baitapdadangky
            // Ở đây tôi sẽ tìm trong viewBaiTapDaDangKy nếu nó là một layout chứa RV
            rvBaiTapDaDangKy = viewBaiTapDaDangKy.findViewById(R.id.rvBaiTapDaDangKy);
        }

        // Cấu hình RecyclerView Ngay
        danhSachNgay = new ArrayList<>();
        rvNgay.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NgayAdapter(danhSachNgay, item -> {
            // Chuyển sang danh sách bài tập của ngày đó
            Intent intent = new Intent(getActivity(), DanhSachBaiTapLon.class);
            intent.putExtra("NgayId", item.getId());
            intent.putExtra("CapDo", phanLoai);
            intent.putExtra("HinhAnhNgay", item.getHinhAnh());
            startActivity(intent);
        });
        rvNgay.setAdapter(adapter);

        // Cấu hình RecyclerView Bài tập đã đăng ký
        danhSachBaiTapDaDangKy = new ArrayList<>();
        if (rvBaiTapDaDangKy != null) {
            rvBaiTapDaDangKy.setLayoutManager(new LinearLayoutManager(getContext()));
            daDangKyAdapter = new BaiTapLonAdapter(danhSachBaiTapDaDangKy, item -> {
                Intent intent = new Intent(getActivity(), DanhSachBaiTapNho.class);
                intent.putExtra("BaiTapLonId", item.getId());
                intent.putExtra("TenBaiTapLon", item.getTenBaiTapLon());
                intent.putExtra("MoTa", item.getMoTa());
                intent.putExtra("HinhAnh", item.getHinhAnh());
                intent.putExtra("isRegistered", true); // Quan trọng để load đúng collection
                startActivity(intent);
            });
            daDangKyAdapter.setOnCancelClickListener(item -> {
                showConfirmHuyDangKyDialog(item);
            });
            rvBaiTapDaDangKy.setAdapter(daDangKyAdapter);
        }

        // Mặc định chọn tab Người mới
        selectTab(tabNguoiMoiBatDau);

        // Xử lý click
        tabNguoiMoiBatDau.setOnClickListener(v -> {
            phanLoai = "NguoiMoiBatDau";
            selectTab(tabNguoiMoiBatDau);
        });
        tabNangCao.setOnClickListener(v -> {
            phanLoai = "NangCao";
            selectTab(tabNangCao);
        });
        tabBaiTapDaDangKy.setOnClickListener(v -> selectTab(tabBaiTapDaDangKy));

        return view;
    }

    private void selectTab(TextView selectedTab) {
        // Reset giao diện các tab
        tabNguoiMoiBatDau.setSelected(false);
        tabNguoiMoiBatDau.setBackground(null);
        tabNangCao.setSelected(false);
        tabNangCao.setBackground(null);
        tabBaiTapDaDangKy.setSelected(false);
        tabBaiTapDaDangKy.setBackground(null);

        // Active tab được chọn
        selectedTab.setSelected(true);
        selectedTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_selected_bg));

        if (selectedTab.getId() == R.id.tabBaiTapDaDangKy) {
            rvNgay.setVisibility(View.GONE);
            viewBaiTapDaDangKy.setVisibility(View.VISIBLE);
            loadBaiTapDaDangKy();
        } else {
            rvNgay.setVisibility(View.VISIBLE);
            viewBaiTapDaDangKy.setVisibility(View.GONE);
            loadKeHoachData();
        }
    }

    private void showConfirmHuyDangKyDialog(BaiTapLon item) {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Xác nhận hủy đăng ký")
                .setMessage("Bạn có muốn hủy đăng ký bài tập lớn \"" + item.getTenBaiTapLon() + "\" không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    huyDangKyBaiTapLon(item);
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void huyDangKyBaiTapLon(BaiTapLon item) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        db.collection("NguoiDung")
                .document(userId)
                .collection("DanhSachBaiTapLonDaDangKy")
                .document(item.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã hủy đăng ký: " + item.getTenBaiTapLon(), Toast.LENGTH_SHORT).show();
                    loadBaiTapDaDangKy(); // Load lại danh sách sau khi xóa
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi hủy đăng ký: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadBaiTapDaDangKy() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        db.collection("NguoiDung")
                .document(userId)
                .collection("DanhSachBaiTapLonDaDangKy")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        danhSachBaiTapDaDangKy.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BaiTapLon item = document.toObject(BaiTapLon.class);
                            item.setId(document.getId());
                            danhSachBaiTapDaDangKy.add(item);
                        }
                        if (daDangKyAdapter != null) {
                            daDangKyAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void loadKeHoachData() {
        // Khởi tạo 30 ngày mặc định
        List<Ngay> danhSachNgayKhoiTao = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            Ngay dummy = new Ngay();
            dummy.setId("ngay_" + i);
            dummy.setTongThoiGian(0);
            dummy.setSoLuongBaiTapLon(0);
            danhSachNgayKhoiTao.add(dummy);
        }

        // Lấy dữ liệu từ Firebase
        db.collection("KeHoach")
                .document(phanLoai)
                .collection("Ngay")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Ngay> duLieuFirebase = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Ngay ngay = document.toObject(Ngay.class);
                            ngay.setId(document.getId());
                            duLieuFirebase.put(document.getId(), ngay);
                        }

                        danhSachNgay.clear();
                        for (Ngay dummy : danhSachNgayKhoiTao) {
                            if (duLieuFirebase.containsKey(dummy.getId())) {
                                danhSachNgay.add(duLieuFirebase.get(dummy.getId()));
                            } else {
                                danhSachNgay.add(dummy);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        danhSachNgay.clear();
                        danhSachNgay.addAll(danhSachNgayKhoiTao);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
