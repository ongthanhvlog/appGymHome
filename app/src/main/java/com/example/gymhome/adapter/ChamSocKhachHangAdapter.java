package com.example.gymhome.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymhome.R;
import com.example.gymhome.model.ChamSocKhachHang;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChamSocKhachHangAdapter extends RecyclerView.Adapter<ChamSocKhachHangAdapter.ViewHolder> {

    private Context context;
    private List<ChamSocKhachHang> danhSachYeuCau;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ChamSocKhachHangAdapter(Context context, List<ChamSocKhachHang> danhSachYeuCau) {
        this.context = context;
        this.danhSachYeuCau = danhSachYeuCau;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_yeu_cau, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChamSocKhachHang item = danhSachYeuCau.get(position);

        holder.tvTieuDe.setText(item.getTieuDe());
        holder.tvNoiDung.setText(item.getNoiDung());

        if (item.getThoiGianTao() != null) {
            holder.tvThoiGianTao.setText(sdf.format(item.getThoiGianTao().toDate()));
        }

        // Trạng thái & Hiển thị nút Hủy
        switch (item.getTrangThai()) {
            case 1: // Đang chờ xử lý
                holder.tvTrangThai.setText("Đang chờ xử lý");
                holder.tvTrangThai.setTextColor(Color.parseColor("#AD6800"));
                holder.tvTrangThai.setBackgroundColor(Color.parseColor("#FFFBE6"));
                holder.btnHuyYeuCau.setVisibility(View.VISIBLE);
                break;
            case 2: // Đã xử lý xong
                holder.tvTrangThai.setText("Đã xử lý xong");
                holder.tvTrangThai.setTextColor(Color.parseColor("#237804"));
                holder.tvTrangThai.setBackgroundColor(Color.parseColor("#F6FFED"));
                holder.btnHuyYeuCau.setVisibility(View.GONE);
                break;
            case 0: // Đã hủy
                holder.tvTrangThai.setText("Đã hủy");
                holder.tvTrangThai.setTextColor(Color.parseColor("#CF1322"));
                holder.tvTrangThai.setBackgroundColor(Color.parseColor("#FFF1F0"));
                holder.btnHuyYeuCau.setVisibility(View.GONE);
                break;
            default:
                holder.btnHuyYeuCau.setVisibility(View.GONE);
                break;
        }

        // Phản hồi
        if (item.getPhanHoi() != null && !item.getPhanHoi().isEmpty()) {
            holder.divider.setVisibility(View.VISIBLE);
            holder.layoutPhanHoi.setVisibility(View.VISIBLE);
            holder.tvPhanHoi.setText(item.getPhanHoi());
            if (item.getThoiGianPhanHoi() != null) {
                holder.tvThoiGianPhanHoi.setText(sdf.format(item.getThoiGianPhanHoi().toDate()));
            } else {
                holder.tvThoiGianPhanHoi.setText("");
            }
        } else {
            holder.divider.setVisibility(View.GONE);
            holder.layoutPhanHoi.setVisibility(View.GONE);
        }

        // Sự kiện nút Hủy
        holder.btnHuyYeuCau.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận hủy")
                    .setMessage("Bạn có chắc chắn muốn hủy yêu cầu này không?")
                    .setPositiveButton("Hủy yêu cầu", (dialog, which) -> {
                        huyYeuCau(item.getId());
                    })
                    .setNegativeButton("Đóng", null)
                    .show();
        });
    }

    private void huyYeuCau(String id) {
        if (id == null) return;

        db.collection("ChamSocKhachHang")
                .document(id)
                .update("trangThai", 0)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Đã hủy yêu cầu thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi khi hủy: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return danhSachYeuCau.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvThoiGianTao, tvTrangThai, tvTieuDe, tvNoiDung, tvPhanHoi, tvThoiGianPhanHoi, btnHuyYeuCau;
        View divider, layoutPhanHoi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvThoiGianTao = itemView.findViewById(R.id.tvThoiGianTao);
            tvTrangThai = itemView.findViewById(R.id.tvTrangThai);
            tvTieuDe = itemView.findViewById(R.id.tvTieuDe);
            tvNoiDung = itemView.findViewById(R.id.tvNoiDung);
            tvPhanHoi = itemView.findViewById(R.id.tvPhanHoi);
            tvThoiGianPhanHoi = itemView.findViewById(R.id.tvThoiGianPhanHoi);
            btnHuyYeuCau = itemView.findViewById(R.id.btnHuyYeuCau);
            divider = itemView.findViewById(R.id.divider);
            layoutPhanHoi = itemView.findViewById(R.id.layoutPhanHoi);
        }
    }
}
