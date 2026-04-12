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

    private List<ThuThach> danhSachThuThach;
    private OnItemClickListener listener;
    private double canNang;

    public interface OnItemClickListener {
        void onItemClick(ThuThach item);
    }

    public ThuThachAdapter(List<ThuThach> danhSachThuThach, double canNang, OnItemClickListener listener) {
        this.danhSachThuThach = danhSachThuThach;
        this.canNang = canNang;
        this.listener = listener;
    }

    public ThuThachAdapter(List<ThuThach> danhSachThuThach, OnItemClickListener listener) {
        this(danhSachThuThach, 0.0, listener);
    }

    public void setCanNang(double canNang) {
        this.canNang = canNang;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thuthach, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThuThach item = danhSachThuThach.get(position);
        holder.tvTenThuThach.setText(item.getTenThuThach());

        int totalSeconds = item.getThoiGian();
        int minutes = totalSeconds / 60;
        holder.tvThoiGian.setText(minutes + " phút");

        holder.tvCapDo.setText(item.getCapDo());

        // Tính Calo = MET * Cân nặng * Thời gian (giờ)
        double metValue = item.getMET();
        double calo = metValue * canNang * (totalSeconds / 3600.0);
        holder.tvCalo.setText(String.format("%.1f kcal", calo));

        Glide.with(holder.itemView.getContext())
                .load(item.getHinhAnh())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgThuThach);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnThucHien.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return danhSachThuThach.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThuThach;
        TextView tvTenThuThach, tvThoiGian, tvCapDo, tvCalo;
        android.widget.Button btnThucHien;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThuThach = itemView.findViewById(R.id.imgThuThach);
            tvTenThuThach = itemView.findViewById(R.id.tvTenThuThach);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGian);
            tvCapDo = itemView.findViewById(R.id.tvCapDo);
            tvCalo = itemView.findViewById(R.id.tvCalo);
            btnThucHien = itemView.findViewById(R.id.btnThucHien);
        }
    }
}
