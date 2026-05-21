package com.example.gymhome.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.gymhome.R;
import com.example.gymhome.model.BaiViet;

import java.util.ArrayList;
import java.util.List;

public class BaiVietAdapter extends RecyclerView.Adapter<BaiVietAdapter.ViewHolder> {

    private List<BaiViet> danhSachBaiViet;
    private List<String> danhSachIdDaLuu = new ArrayList<>();
    private OnItemClickListener listener;
    private OnSaveClickListener saveListener;

    public interface OnItemClickListener {
        void onItemClick(BaiViet item);
    }

    public interface OnSaveClickListener {
        void onSaveClick(BaiViet item, boolean isSaved);
    }

    public BaiVietAdapter(List<BaiViet> danhSachBaiViet, OnItemClickListener listener) {
        this.danhSachBaiViet = danhSachBaiViet;
        this.listener = listener;
    }

    public void setOnSaveClickListener(OnSaveClickListener saveListener) {
        this.saveListener = saveListener;
    }

    public void setDanhSachIdDaLuu(List<String> savedIds) {
        this.danhSachIdDaLuu = savedIds;
        notifyDataSetChanged();
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

        holder.tvNgayDang.setText(String.format("Ngày đăng: %s", item.getNgayDang()));

        // Xử lý Tag và Icon
        if (item.getTag() != null && !item.getTag().isEmpty()) {
            holder.llTag.setVisibility(View.VISIBLE);
            String tag = item.getTag();
            String label = tag;
            int colorRes = R.color.tag_default;
            int iconRes = R.drawable.ic_health; // Default icon

            switch (tag) {
                case "dinhduong":
                    label = "Dinh dưỡng";
                    colorRes = R.color.tag_dinhduong;
                    iconRes = R.drawable.ic_dinhduong;
                    break;
                case "suckhoe":
                    label = "Sức khỏe";
                    colorRes = R.color.tag_suckhoe;
                    iconRes = R.drawable.ic_health;
                    break;
                case "tapluyen":
                    label = "Tập luyện";
                    colorRes = R.color.tag_tapluyen;
                    iconRes = R.drawable.ic_tapluyen;
                    break;
            }
            holder.tvTag.setText(label);
            holder.imgTagIcon.setImageResource(iconRes);
            holder.llTag.getBackground().mutate().setTint(ContextCompat.getColor(holder.itemView.getContext(), colorRes));
        } else {
            holder.llTag.setVisibility(View.GONE);
        }

        // Hiển thị nguồn (Ưu tiên Logo ảnh nếu có)
        if (item.getLinkLogo() != null && !item.getLinkLogo().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getLinkLogo())
                    .into(holder.imgLinkLogo);
            holder.imgLinkLogo.setVisibility(View.VISIBLE);
            holder.tvSource.setVisibility(View.GONE);
        } else {
            holder.imgLinkLogo.setVisibility(View.GONE);
            holder.tvSource.setVisibility(View.VISIBLE);
            String source = item.getSourceName();
            if (source == null || source.isEmpty()) {
                source = "GymHome";
            }
            holder.tvSource.setText(source.toUpperCase());
        }

        // Load hình ảnh bài viết
        Glide.with(holder.itemView.getContext())
                .load(item.getHinhAnhDaiDien())
                .transform(new CenterCrop())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imgHinhAnhDaiDien);

        // Click listeners
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.llReadMore.setOnClickListener(v -> listener.onItemClick(item));
        
        // Cập nhật trạng thái icon lưu
        boolean isSaved = item.getId() != null && danhSachIdDaLuu.contains(item.getId());
        if (isSaved) {
            holder.imgSaveIcon.setImageResource(R.drawable.ic_bookmark_filled);
            holder.imgSaveIcon.setImageTintList(android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.colorSaved)));
        } else {
            holder.imgSaveIcon.setImageResource(R.drawable.ic_bookmark_border);
            holder.imgSaveIcon.setImageTintList(android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.mau_icon_mac_dinh)));
        }

        holder.llSave.setOnClickListener(v -> {
            if (saveListener != null) {
                saveListener.onSaveClick(item, isSaved);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhSachBaiViet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHinhAnhDaiDien, imgTagIcon, imgLinkLogo, imgSaveIcon;
        TextView tvTenBaiViet, tvNgayDang, tvSource, tvTag;
        LinearLayout llTag, llSave, llReadMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHinhAnhDaiDien = itemView.findViewById(R.id.imgHinhAnhDaiDien);
            imgTagIcon = itemView.findViewById(R.id.imgTagIcon);
            imgLinkLogo = itemView.findViewById(R.id.imgLinkLogo);
            tvTenBaiViet = itemView.findViewById(R.id.tvTenBaiViet);
            tvNgayDang = itemView.findViewById(R.id.tvNgayDang);
            tvSource = itemView.findViewById(R.id.tvSource);
            tvTag = itemView.findViewById(R.id.tvTag);
            llTag = itemView.findViewById(R.id.llTag);
            llSave = itemView.findViewById(R.id.llSave);
            imgSaveIcon = itemView.findViewById(R.id.imgSaveIcon);
            llReadMore = itemView.findViewById(R.id.llReadMore);
        }
    }
}
