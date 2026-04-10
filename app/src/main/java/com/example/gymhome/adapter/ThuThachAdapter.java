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
import com.example.gymhome.model.ThuThach;

import java.util.List;

public class ThuThachAdapter extends RecyclerView.Adapter<ThuThachAdapter.ViewHolder> {

    private List<ThuThach> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ThuThach item);
    }

    public ThuThachAdapter(List<ThuThach> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thuthach, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThuThach item = list.get(position);
        holder.tvTenThuThach.setText(item.getTenThuThach());

        int totalSeconds = item.getThoiGian();
        int minutes = totalSeconds / 60;
        holder.tvThoiGian.setText(minutes + " phút");

        holder.tvCapDo.setText(item.getCapDo());

        Glide.with(holder.itemView.getContext())
                .load(item.getHinhAnh())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgThuThach);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnThucHien.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThuThach;
        TextView tvTenThuThach, tvThoiGian, tvCapDo;
        android.widget.Button btnThucHien;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThuThach = itemView.findViewById(R.id.imgThuThach);
            tvTenThuThach = itemView.findViewById(R.id.tvTenThuThach);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGian);
            tvCapDo = itemView.findViewById(R.id.tvCapDo);
            btnThucHien = itemView.findViewById(R.id.btnThucHien);
        }
    }
}
