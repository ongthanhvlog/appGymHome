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
import com.example.gymhome.model.VungTapTrung;

import java.util.List;

public class VungTapTrungAdapter extends RecyclerView.Adapter<VungTapTrungAdapter.ViewHolder> {

    private List<VungTapTrung> danhSachVungTapTrung;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(VungTapTrung item);
    }

    public VungTapTrungAdapter(List<VungTapTrung> danhSachVungTapTrung, OnItemClickListener listener) {
        this.danhSachVungTapTrung = danhSachVungTapTrung;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vungtaptrung, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VungTapTrung item = danhSachVungTapTrung.get(position);
        holder.tvTenVung.setText(item.getTenVung());
        
        Glide.with(holder.itemView.getContext())
                .load(item.getHinhAnh())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivHinhAnh);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return danhSachVungTapTrung.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinhAnh;
        TextView tvTenVung;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhAnh = itemView.findViewById(R.id.imgHinhAnhBaiTapLon);
            tvTenVung = itemView.findViewById(R.id.tvTenVung);
        }
    }
}
