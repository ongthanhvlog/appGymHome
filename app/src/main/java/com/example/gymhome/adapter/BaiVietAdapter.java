package com.example.gymhome.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.gymhome.R;
import com.example.gymhome.model.BaiViet;

import java.util.List;

public class BaiVietAdapter extends RecyclerView.Adapter<BaiVietAdapter.ViewHolder> {

    private List<BaiViet> danhSachBaiViet;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BaiViet item);
    }

    public BaiVietAdapter(List<BaiViet> danhSachBaiViet, OnItemClickListener listener) {
        this.danhSachBaiViet = danhSachBaiViet;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bai_viet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaiViet item = danhSachBaiViet.get(position);
        holder.tvTenBaiViet.setText(item.getTenBaiViet());

        if (item.getMoTa() != null && !item.getMoTa().equals(item.getTenBaiViet())) {
            holder.tvMoTa.setText(item.getMoTa());
            holder.tvMoTa.setVisibility(View.VISIBLE);
        } else {
            holder.tvMoTa.setVisibility(View.GONE);
        }

        holder.tvNgayDang.setText(String.format("Ngày đăng: %s", item.getNgayDang()));

        // Load logo nguồn nếu có
        if (item.getLinkLogo() != null && !item.getLinkLogo().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getLinkLogo())
                    .into(holder.imgLinkLogo);
            holder.imgLinkLogo.setVisibility(View.VISIBLE);
            holder.tvSource.setVisibility(View.GONE);
        } else {
            holder.imgLinkLogo.setVisibility(View.GONE);
            holder.tvSource.setVisibility(View.VISIBLE);
            holder.tvSource.setText(item.getSourceName());
        }

        // Sử dụng Glide với hiệu ứng bo góc cho đồng bộ với Web
        Glide.with(holder.itemView.getContext())
                .load(item.getHinhAnhDaiDien())
                .transform(new CenterCrop(), new RoundedCorners(16))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imgHinhAnhDaiDien);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return danhSachBaiViet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHinhAnhDaiDien, imgLinkLogo;
        TextView tvTenBaiViet, tvMoTa, tvNgayDang, tvSource;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHinhAnhDaiDien = itemView.findViewById(R.id.imgHinhAnhDaiDien);
            imgLinkLogo = itemView.findViewById(R.id.imgLinkLogo);
            tvTenBaiViet = itemView.findViewById(R.id.tvTenBaiViet);
            tvMoTa = itemView.findViewById(R.id.tvMoTa);
            tvNgayDang = itemView.findViewById(R.id.tvNgayDang);
            tvSource = itemView.findViewById(R.id.tvSource);
        }
    }
}
