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
import com.example.gymhome.model.BaiTapNho;

import java.util.List;

public class BaiTapNhoAdapter extends RecyclerView.Adapter<BaiTapNhoAdapter.ViewHolder> {

    private List<BaiTapNho> danhSachBaiTapNho;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BaiTapNho item);
    }

    public BaiTapNhoAdapter(List<BaiTapNho> danhSachBaiTapNho, OnItemClickListener listener) {
        this.danhSachBaiTapNho = danhSachBaiTapNho;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_baitapnho, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaiTapNho item = danhSachBaiTapNho.get(position);
        holder.tvTen.setText(item.getTenBaiTapNho());
        
        int thoiGian = item.getThoiGian();
        holder.tvThoiGian.setText(thoiGian + " giây");

        Glide.with(holder.itemView.getContext())
                .load(item.getHinhAnh())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivHinhAnh);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return danhSachBaiTapNho.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinhAnh;
        TextView tvTen, tvThoiGian;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhAnh = itemView.findViewById(R.id.imgHinhAnhBaiTapNho);
            tvTen = itemView.findViewById(R.id.tvTenBaiTapNho);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGian);
        }
    }
}
