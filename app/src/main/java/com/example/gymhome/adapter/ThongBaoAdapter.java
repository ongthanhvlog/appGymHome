package com.example.gymhome.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymhome.R;
import com.example.gymhome.model.ThongBao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ThongBaoAdapter extends RecyclerView.Adapter<ThongBaoAdapter.ThongBaoViewHolder> {

    private List<ThongBao> listThongBao;

    public ThongBaoAdapter(List<ThongBao> listThongBao) {
        this.listThongBao = listThongBao;
    }

    @NonNull
    @Override
    public ThongBaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thong_bao, parent, false);
        return new ThongBaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThongBaoViewHolder holder, int position) {
        ThongBao thongBao = listThongBao.get(position);
        if (thongBao == null) return;

        holder.tvTieuDe.setText(thongBao.getTieuDe());
        holder.tvNoiDung.setText(thongBao.getNoiDung());
        
        Object ngayRaw = thongBao.getNgayGui();
        String ngayHienThi = "";

        if (ngayRaw instanceof com.google.firebase.Timestamp) {
            Date date = ((com.google.firebase.Timestamp) ngayRaw).toDate();
            SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            sdfOutput.setTimeZone(TimeZone.getDefault());
            ngayHienThi = sdfOutput.format(date);
        } else if (ngayRaw instanceof String) {
            String strNgay = (String) ngayRaw;
            try {
                SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                sdfInput.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = sdfInput.parse(strNgay);
                SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                sdfOutput.setTimeZone(TimeZone.getDefault());
                if (date != null) ngayHienThi = sdfOutput.format(date);
            } catch (Exception e) {
                if (strNgay.length() > 16) ngayHienThi = strNgay.substring(0, 16).replace("T", " ");
                else ngayHienThi = strNgay;
            }
        }
        
        holder.tvNgayGui.setText(ngayHienThi);
    }

    @Override
    public int getItemCount() {
        if (listThongBao != null) {
            return listThongBao.size();
        }
        return 0;
    }

    public static class ThongBaoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTieuDe, tvNoiDung, tvNgayGui;

        public ThongBaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTieuDe = itemView.findViewById(R.id.tvTieuDe);
            tvNoiDung = itemView.findViewById(R.id.tvNoiDung);
            tvNgayGui = itemView.findViewById(R.id.tvNgayGui);
        }
    }
}
