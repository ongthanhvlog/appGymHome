package com.example.gymhome.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gymhome.R;
import com.example.gymhome.model.Ngay;

import java.util.List;

public class NgayAdapter extends RecyclerView.Adapter<NgayAdapter.ViewHolder> {

    private List<Ngay>  danhSachNgay;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Ngay item);
    }

    public NgayAdapter(List<Ngay> danhSachNgay, OnItemClickListener listener) {
        this.danhSachNgay = danhSachNgay;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ngay, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ngay item = danhSachNgay.get(position);
        
        int ngayNum = item.getNgayNumber();
        holder.tvSoNgay.setText(String.valueOf(ngayNum));
        holder.tvTenNgay.setText("Ngày " + ngayNum);
        
        holder.tvThoiGian.setText(item.getTongThoiGianDisplay() + " phút");
        holder.tvSoBaiTap.setText(item.getSoLuongBaiTapLonDisplay() + " bài tập");

        Glide.with(holder.itemView.getContext())
                .load(item.getHinhAnh())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.ivHinhAnhNgay);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return danhSachNgay.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenNgay, tvThoiGian, tvSoBaiTap, tvSoNgay;
        ImageView ivHinhAnhNgay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhAnhNgay = itemView.findViewById(R.id.imgHinhAnhBaiTapLon);
            tvSoNgay = itemView.findViewById(R.id.tvSoNgay);
            tvTenNgay = itemView.findViewById(R.id.tvTenNgay);
            tvThoiGian = itemView.findViewById(R.id.tvTongThoiGian);
            tvSoBaiTap = itemView.findViewById(R.id.tvSoLuongBaiTapLon);
        }
    }
}
