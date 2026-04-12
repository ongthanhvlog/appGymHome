package com.example.gymhome.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.gymhome.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BaoCao_Fragment extends Fragment {

    private TextView tvTongBaiTapHoanThanh, tvTongCalo, tvTongThoiGianTapLuyen;
    private TextView tvChiSoBMI, tvBMIPhanLoai, tvCanNang, tvMaxCanNang, tvMinCanNang, tvChieuCao;
    private ImageView ivChiSoBMI, ivBMIPhanLoaiColor;
    private Button btnChinhSuaBMI;
    private LineChart bieuDoCanNang;
    private FirebaseFirestore db;
    private String userId;
    private double currentCanNang = 0;
    private int currentChieuCao = 0;

    public BaoCao_Fragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.baocao, container, false);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        //anh xa view
        tvTongBaiTapHoanThanh = view.findViewById(R.id.tvTongBaiTapHoanThanh);
        tvTongCalo = view.findViewById(R.id.tvTongCalo);
        tvTongThoiGianTapLuyen = view.findViewById(R.id.tvTongThoiGianTapLuyen);
        tvChiSoBMI = view.findViewById(R.id.tvChiSoBMI);
        tvBMIPhanLoai = view.findViewById(R.id.tvBMIPhanLoai);
        tvCanNang = view.findViewById(R.id.tvCanNang);
        tvMaxCanNang = view.findViewById(R.id.tvMaxCanNang);
        tvMinCanNang = view.findViewById(R.id.tvMinCanNang);
        tvChieuCao = view.findViewById(R.id.tvChieuCao);
        ivChiSoBMI = view.findViewById(R.id.ivBMIPointer);
        ivBMIPhanLoaiColor = view.findViewById(R.id.ivBMIPhanLoaiColor);
        btnChinhSuaBMI = view.findViewById(R.id.btnChinhSuaBMI);
        bieuDoCanNang = view.findViewById(R.id.bieuDoCanNang);

        loadUserData();
        loadLichSuCanNang();
        btnChinhSuaBMI.setOnClickListener(v -> HienThiDialogChinhSuaBMI());
        setupChart();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
        loadLichSuCanNang();
    }

    private void setupChart() {
        bieuDoCanNang.getDescription().setEnabled(false);
        bieuDoCanNang.setDrawGridBackground(false);
        bieuDoCanNang.getLegend().setEnabled(false);
        bieuDoCanNang.getAxisRight().setEnabled(false);
        bieuDoCanNang.setTouchEnabled(true);
        bieuDoCanNang.setDragEnabled(true);
        bieuDoCanNang.setScaleEnabled(true);
        bieuDoCanNang.setPinchZoom(true);

        XAxis xAxis = bieuDoCanNang.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(new Date((long) value * 1000L));
            }
        });

        YAxis leftAxis = bieuDoCanNang.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setLabelCount(5);
    }

    private void loadUserData() {
        if (userId == null) return;

        db.collection("NguoiDung").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> thongTin = (Map<String, Object>) documentSnapshot.get("ThongTinNguoiDung");
                if (thongTin != null) {
                    currentCanNang = ((Number) thongTin.getOrDefault("CanNang", 0)).doubleValue();
                    currentChieuCao = ((Number) thongTin.getOrDefault("ChieuCao", 0)).intValue();
                    int soBaiTap = ((Number) thongTin.getOrDefault("SoBaiTapHoanThanh", 0)).intValue();
                    double tongCalo = ((Number) thongTin.getOrDefault("TongCalo", 0.0)).doubleValue();
                    int thoiGianGiay = ((Number) thongTin.getOrDefault("ThoiGianTapLuyen", 0)).intValue();
                    double thoiGianPhut = thoiGianGiay / 60.0;

                    tvTongBaiTapHoanThanh.setText(String.valueOf(soBaiTap));
                    tvTongCalo.setText(String.format(Locale.getDefault(), "%.1f", tongCalo));
                    tvTongThoiGianTapLuyen.setText(String.format(Locale.getDefault(), "%.1f phút", thoiGianPhut));
                    tvCanNang.setText(String.format(Locale.getDefault(), "%.1f kg", currentCanNang));
                    tvChieuCao.setText(currentChieuCao + " cm");

                    tinhToanVaHienThiBMI();
                }
            }
        });
    }

    private void tinhToanVaHienThiBMI() {
        if (currentChieuCao <= 0 || currentCanNang <= 0) return;
        double chieuCaoMet = currentChieuCao / 100.0; // convert chieucao từ cm sang m
        // BMI = can nang / (chieu cao * chieu cao)
        double chiSoBMI = currentCanNang / (chieuCaoMet * chieuCaoMet);
        tvChiSoBMI.setText(String.format(Locale.getDefault(), "%.1f", chiSoBMI));

        String phanLoaiBMI;
        int color;

        if (chiSoBMI < 18.5) {
            phanLoaiBMI = "Cân nặng thấp (gầy)";
            color = Color.parseColor("#3F51B5");
        } else if (chiSoBMI < 25) {
            phanLoaiBMI = "Bình thường";
            color = Color.parseColor("#4CAF50");
        } else if (chiSoBMI < 30) {
            phanLoaiBMI = "Tiền béo phì";
            color = Color.parseColor("#FFEB3B");
        } else if (chiSoBMI < 35) {
            phanLoaiBMI = "Béo phì độ I";
            color = Color.parseColor("#FF9800");
        } else if (chiSoBMI < 40) {
            phanLoaiBMI = "Béo phì độ II";
            color = Color.parseColor("#F44336");
        } else {
            phanLoaiBMI = "Béo phì độ III";
            color = Color.parseColor("#B71C1C");
        }

        // Tính toán và cập nhật vị trí ivBMIPoiter trên thanh (0 → 1)
        float BMIRatio = (float) ((chiSoBMI - 15) / (40 - 15));
        if (BMIRatio > 1.0f) BMIRatio = 1.0f;
        if (BMIRatio < 0.0f) BMIRatio = 0.0f;
        tvBMIPhanLoai.setText(phanLoaiBMI);
        ivBMIPhanLoaiColor.setColorFilter(color);
        float finalBMIRatio = BMIRatio;
        ivChiSoBMI.post(() -> {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) ivChiSoBMI.getLayoutParams();
            int parentWidth = ((View) ivChiSoBMI.getParent()).getWidth();
            if (parentWidth > 0) {
                int pointerWidth = ivChiSoBMI.getWidth();
                params.leftMargin = (int) (parentWidth * finalBMIRatio) - (pointerWidth / 2);
                ivChiSoBMI.setLayoutParams(params);
            }
        });
    }

    private void loadLichSuCanNang() {
        if (userId == null) return;
        db.collection("NguoiDung").document(userId).collection("LichSuCanNang")
                .orderBy("ThoiGian", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Entry> entries = new ArrayList<>();
            double maxCanNang = 0, minCanNang = Double.MAX_VALUE;

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Double giaTriCanNang = doc.getDouble("CanNang");
                Long giaTriThoiGian = doc.getLong("ThoiGian");
                if (giaTriCanNang != null && giaTriThoiGian != null) {
                    double canNang = giaTriCanNang;
                    long thoiGian = giaTriThoiGian;
                    entries.add(new Entry((float) thoiGian, (float) canNang));
                    if (canNang > maxCanNang) maxCanNang = canNang;
                    if (canNang < minCanNang) minCanNang = canNang;
                }
            }

            if (!entries.isEmpty()) {
                if (minCanNang == Double.MAX_VALUE) minCanNang = 0;
                tvMaxCanNang.setText(String.format(Locale.getDefault(), "Nặng nhất  %.1f kg", maxCanNang));
                tvMinCanNang.setText(String.format(Locale.getDefault(), "Nhẹ nhất   %.1f kg", minCanNang));
                capNhatBieuDo(entries);
            }
        });
    }

    private void capNhatBieuDo(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Cân nặng");
        dataSet.setColor(Color.parseColor("#2196F3"));
        dataSet.setCircleColor(Color.parseColor("#2196F3"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleRadius(3f);
        dataSet.setCircleHoleColor(Color.WHITE);
        
        // Hiển thị số kg trên chấm tròn biểu đồ
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.DKGRAY);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return String.format(Locale.getDefault(), "%.1f", entry.getY());
            }
        });

        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#2196F3"));
        dataSet.setFillAlpha(30);

        LineData lineData = new LineData(dataSet);
        bieuDoCanNang.setData(lineData);
        bieuDoCanNang.animateX(1000);
        bieuDoCanNang.invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void HienThiDialogChinhSuaBMI() {
        if (getContext() == null) return;
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_chieucao_cannang);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.BOTTOM);
        }

        TextView tvCanNangDialog = dialog.findViewById(R.id.tvCanNang);
        SeekBar seekBarCanNang = dialog.findViewById(R.id.seekBarCanNang);
        TextView tvChieuCaoDialog = dialog.findViewById(R.id.tvChieuCao);
        SeekBar seekBarChieuCao = dialog.findViewById(R.id.seekBarChieuCao);
        
        ImageButton btnGiamChieuCao = dialog.findViewById(R.id.btnGiamChieuCao);
        ImageButton btnTangChieuCao = dialog.findViewById(R.id.btnTangChieuCao);
        ImageButton btnGiamCanNang = dialog.findViewById(R.id.btnGiamCanNang);
        ImageButton btnTangCanNang = dialog.findViewById(R.id.btnTangCanNang);
        
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        // Khởi tạo giá trị
        final double[] tempCanNang = {currentCanNang};
        final int[] tempChieuCao = {currentChieuCao};

        tvCanNangDialog.setText(String.format(Locale.getDefault(), "%.1f", tempCanNang[0]));
        seekBarCanNang.setProgress((int) (tempCanNang[0] * 10));
        tvChieuCaoDialog.setText(String.valueOf(tempChieuCao[0]));
        seekBarChieuCao.setProgress(tempChieuCao[0]);

        // Xử lý Chiều cao
        seekBarChieuCao.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tempChieuCao[0] = progress;
                tvChieuCaoDialog.setText(String.valueOf(tempChieuCao[0]));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnGiamChieuCao.setOnTouchListener(new RepeatListener(400, 100, v -> {
            int progress = seekBarChieuCao.getProgress();
            if (progress > 0) {
                seekBarChieuCao.setProgress(progress - 1);
            }
        }));

        btnTangChieuCao.setOnTouchListener(new RepeatListener(400, 100, v -> {
            int progress = seekBarChieuCao.getProgress();
            if (progress < seekBarChieuCao.getMax()) {
                seekBarChieuCao.setProgress(progress + 1);
            }
        }));

        seekBarCanNang.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tempCanNang[0] = progress / 10.0;
                tvCanNangDialog.setText(String.format(Locale.getDefault(), "%.1f", tempCanNang[0]));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnGiamCanNang.setOnTouchListener(new RepeatListener(400, 100, v -> {
            int progress = seekBarCanNang.getProgress();
            if (progress > 0) {
                seekBarCanNang.setProgress(progress - 1);
            }
        }));

        btnTangCanNang.setOnTouchListener(new RepeatListener(400, 100, v -> {
            int progress = seekBarCanNang.getProgress();
            if (progress < seekBarCanNang.getMax()) {
                seekBarCanNang.setProgress(progress + 1);
            }
        }));

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            luuDuLieuMoi(tempCanNang[0], tempChieuCao[0]);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void HienThiDialogThayDoiCanNang(String message) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void luuDuLieuMoi(double canNang, int chieuCao) {
        if (userId == null) return;

        db.collection("NguoiDung").document(userId).collection("LichSuCanNang")
                .orderBy("ThoiGian", Query.Direction.DESCENDING).limit(1)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot lastDoc = queryDocumentSnapshots.getDocuments().get(0);
                        Double lastWeightVal = lastDoc.getDouble("CanNang");
                        Long lastTimeVal = lastDoc.getLong("ThoiGian");

                        if (lastWeightVal != null && lastTimeVal != null) {
                            double lastWeight = lastWeightVal;
                            double diff = lastWeight - canNang;

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            String timeStr = sdf.format(new Date(lastTimeVal * 1000L));

                            if (Math.abs(diff) > 0.01) {
                                String message;
                                if (diff > 0) {
                                    message = String.format(Locale.getDefault(), "Bạn đã giảm được %.1f kg so với lúc %s!", diff, timeStr);
                                } else {
                                    message = String.format(Locale.getDefault(), "Bạn đã tăng thêm %.1f kg so với lúc %s!", -diff, timeStr);
                                }
                                HienThiDialogThayDoiCanNang(message);
                            } else {
                                HienThiDialogThayDoiCanNang("Cân nặng không thay đổi so với lúc " + timeStr);
                            }
                        }
                    }

                    Map<String, Object> update = new HashMap<>();
                    update.put("ThongTinNguoiDung.CanNang", canNang);
                    update.put("ThongTinNguoiDung.ChieuCao", chieuCao);

                    db.collection("NguoiDung").document(userId).update(update).addOnSuccessListener(aVoid -> {
                        currentCanNang = canNang;
                        currentChieuCao = chieuCao;
                        tvCanNang.setText(String.format(Locale.getDefault(), "%.1f kg", canNang));
                        tvChieuCao.setText(String.format(Locale.getDefault(), "%d cm", chieuCao));
                        tinhToanVaHienThiBMI();
                        
                        Map<String, Object> history = new HashMap<>();
                        history.put("CanNang", canNang);
                        history.put("ThoiGian", System.currentTimeMillis() / 1000);
                        db.collection("NguoiDung").document(userId).collection("LichSuCanNang").add(history)
                                .addOnSuccessListener(documentReference -> loadLichSuCanNang());
                    });
                });
    }

    public static class RepeatListener implements View.OnTouchListener {
        private final Handler handler = new Handler();
        private final int initialInterval;
        private final int normalInterval;
        private final View.OnClickListener clickListener;
        private View downView;

        private final Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView != null) {
                    handler.postDelayed(this, normalInterval);
                    clickListener.onClick(downView);
                }
            }
        };

        public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener) {
            if (clickListener == null) throw new IllegalArgumentException("null clickListener");
            if (initialInterval < 0 || normalInterval < 0) throw new IllegalArgumentException("negative interval");
            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.removeCallbacks(handlerRunnable);
                    handler.postDelayed(handlerRunnable, initialInterval);
                    downView = view;
                    downView.setPressed(true);
                    clickListener.onClick(view);
                    view.performClick();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacks(handlerRunnable);
                    if (downView != null) {
                        downView.setPressed(false);
                        downView = null;
                    }
                    return true;
            }
            return false;
        }
    }
}
