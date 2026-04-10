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
import com.example.gymhome.model.BaiTapLon;

import java.util.List;

public class BaiTapLonAdapter extends RecyclerView.Adapter<BaiTapLonAdapter.ViewHolder> {
    private List<BaiTapLon> danhSachBaiTapLon;
    private OnItemClickListener listener;
    private OnCancelClickListener cancelListener;
    private boolean isShowCancelButton = false;

    public interface OnItemClickListener {
        void onItemClick(BaiTapLon item);
    }

    public interface OnCancelClickListener {
        void onCancelClick(BaiTapLon item);
    }

    // Constructor
    public BaiTapLonAdapter(List<BaiTapLon> danhSachBaiTapLon, OnItemClickListener listener) {
        this.danhSachBaiTapLon = danhSachBaiTapLon;
        this.listener = listener;
    }

    public void setOnCancelClickListener(OnCancelClickListener cancelListener) {
        this.cancelListener = cancelListener;
        this.isShowCancelButton = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ánh xạ layout item_baitaplon vào Adapter
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_baitaplon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaiTapLon item = danhSachBaiTapLon.get(position);
        holder.tvTen.setText(item.getTenBaiTapLon());
        holder.tvCapDo.setText(item.getCapDo());
        holder.tvThoiGian.setText(item.getThoiGian());

        // Sử dụng thư viện Glide để load hình ảnh từ link
        Glide.with(holder.itemView.getContext())
                .load(item.getHinhAnh())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivHinhAnh);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));

        if (isShowCancelButton) {
            holder.btnHuy.setVisibility(View.VISIBLE);
            holder.btnHuy.setOnClickListener(v -> {
                if (cancelListener != null) {
                    cancelListener.onCancelClick(item);
                }
            });
        } else {
            holder.btnHuy.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return danhSachBaiTapLon != null ? danhSachBaiTapLon.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinhAnh, btnHuy;
        TextView tvTen, tvCapDo, tvThoiGian;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhAnh = itemView.findViewById(R.id.imgHinhAnhBaiTapLon);
            btnHuy = itemView.findViewById(R.id.btnHuyDangKyBaiTapLon);
            tvTen = itemView.findViewById(R.id.tvTenBaiTapLon);
            tvCapDo = itemView.findViewById(R.id.tvCapDo);
            tvThoiGian = itemView.findViewById(R.id.tvTongThoiGian);
        }
    }
}
