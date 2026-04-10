package com.example.gymhome.activity;

import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.VideoSize;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;

import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import com.example.gymhome.utils.VideoCacheManager;
import com.example.gymhome.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BaiTapNho extends AppCompatActivity {
    private ImageButton ibQuayLai, ibPrevious, ibPause, ibNext, ibLapLaiBaiTap;
    private TextView tvTenBaiTapNho, tvMoTaBaiTapNho, tvThoiGian, tvTienDo;
    private PlayerView playerView;
    private CardView videoCard;
    private ProgressBar videoProgressBar;
    private SeekBar seekBarAmLuong;
    private List<com.example.gymhome.model.BaiTapNho> danhSachBaiTapNho;
    private int viTriHienTai = 0;
    private boolean isPaused = false;
    private boolean isBatLapLai = false;
    private long thoiGianConLai;
    private ExoPlayer exoPlayer;
    private CountDownTimer demNguocThoiGianBaiTap;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle trangThaiLuuTru) {
        super.onCreate(trangThaiLuuTru);
        EdgeToEdge.enable(this);
        setContentView(R.layout.baitapnho);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets thanhHeThong = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(thanhHeThong.left, thanhHeThong.top, thanhHeThong.right, thanhHeThong.bottom);
            return insets;
        });

        //anh xa id
        ibQuayLai = findViewById(R.id.ibQuayLai);
        ibPrevious = findViewById(R.id.ibPrevious);
        ibPause = findViewById(R.id.ibPause);
        ibNext = findViewById(R.id.ibNext);
        ibLapLaiBaiTap = findViewById(R.id.ibLapLaiBaiTap);
        tvTenBaiTapNho = findViewById(R.id.tvTenBaiTapNho);
        tvMoTaBaiTapNho = findViewById(R.id.tvMoTaBaiTapNho);
        tvThoiGian = findViewById(R.id.tvThoiGian);
        tvTienDo = findViewById(R.id.tvTienDo);
        playerView = findViewById(R.id.playerView);
        videoCard = findViewById(R.id.videoCard);
        videoProgressBar = findViewById(R.id.videoProgressBar);
        seekBarAmLuong = findViewById(R.id.seekBarAmLuong);
        
        danhSachBaiTapNho = (ArrayList<com.example.gymhome.model.BaiTapNho>) getIntent().getSerializableExtra("DanhSachBaiTap");
        viTriHienTai = getIntent().getIntExtra("ViTriHienTai", 0);
        thietLapAmLuong();
        khoiTaoTrinhPhatVideo();
        if (danhSachBaiTapNho != null && !danhSachBaiTapNho.isEmpty()) {
            hienThiThongTinBaiTap(viTriHienTai);
        }

        ibQuayLai.setOnClickListener(v -> finish());
        ibPrevious.setOnClickListener(v -> nextBaiTap(viTriHienTai - 1));
        ibNext.setOnClickListener(v -> nextBaiTap(viTriHienTai + 1));

        ibLapLaiBaiTap.setOnClickListener(v -> {
            isBatLapLai = !isBatLapLai;
            if (isBatLapLai) {
                ibLapLaiBaiTap.setColorFilter(Color.parseColor("#FF5252"));
                Toast.makeText(this, "Đã bật lặp lại bài tập", Toast.LENGTH_SHORT).show();
            } else {
                ibLapLaiBaiTap.setColorFilter(Color.parseColor("#9E9E9E"));
                Toast.makeText(this, "Đã tắt lặp lại bài tập", Toast.LENGTH_SHORT).show();
            }
        });

        ibPause.setOnClickListener(v -> {
            if (isPaused) tiepTucDemNguoc();
            else tamDungDemNguoc();
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void khoiTaoTrinhPhatVideo() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build();

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this)
                .setEnableDecoderFallback(true);

        // Sử dụng CacheDataSource.Factory để load video từ cache
        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(this)
                .setDataSourceFactory(VideoCacheManager.getCacheDataSourceFactory(this));

        exoPlayer = new ExoPlayer.Builder(this, renderersFactory)
                .setMediaSourceFactory(mediaSourceFactory)
                .setAudioAttributes(audioAttributes, true)
                .setHandleAudioBecomingNoisy(true)
                .build();

        playerView.setPlayer(exoPlayer);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        playerView.setKeepContentOnPlayerReset(true);

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onVideoSizeChanged(VideoSize videoSize) {
                if (videoSize.width > 0 && videoSize.height > 0) {
                    videoCard.post(() -> applyVideoSize(videoSize));
                }
            }

            @Override
            public void onPlaybackStateChanged(int trangThaiPhat) {
                if (trangThaiPhat == Player.STATE_READY) {
                    videoProgressBar.setVisibility(View.GONE);
                    playerView.setVisibility(View.VISIBLE);
                    if (demNguocThoiGianBaiTap == null && !isPaused) {
                        batDauDemNguoc();
                    }
                } else if (trangThaiPhat == Player.STATE_BUFFERING) {
                    videoProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlayerError(androidx.media3.common.PlaybackException loi) {
                videoProgressBar.setVisibility(View.GONE);
                playerView.setVisibility(View.GONE);
                Toast.makeText(BaiTapNho.this, "Không thể phát video hướng dẫn", Toast.LENGTH_SHORT).show();
                if (demNguocThoiGianBaiTap == null && !isPaused) {
                    batDauDemNguoc();
                }
            }
        });

        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
    }
    private void applyVideoSize(VideoSize videoSize) {
        videoCard.requestLayout();
    }
    private void thietLapAmLuong() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int amLuongToiDa = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int amLuongHienTai = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        
        seekBarAmLuong.setMax(amLuongToiDa);
        seekBarAmLuong.setProgress(amLuongHienTai);
        seekBarAmLuong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar thanhTruoc, int tienDo, boolean tuNguoiDung) {
                if (tuNguoiDung) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, tienDo, 0);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar thanhTruoc) {}
            @Override public void onStopTrackingTouch(SeekBar thanhTruoc) {}
        });
    }

    private void hienThiThongTinBaiTap(int viTri) {
        if (demNguocThoiGianBaiTap != null) {
            demNguocThoiGianBaiTap.cancel();
            demNguocThoiGianBaiTap = null;
        }

        com.example.gymhome.model.BaiTapNho baiTap = danhSachBaiTapNho.get(viTri);
        tvTenBaiTapNho.setText(baiTap.getTenBaiTapNho());
        tvMoTaBaiTapNho.setText(baiTap.getMoTa());
        tvTienDo.setText(String.format(Locale.getDefault(), "Bài tập: %d/%d", viTri + 1, danhSachBaiTapNho.size()));

        thoiGianConLai = (baiTap.getThoiGian() > 0 ? baiTap.getThoiGian() : 30) * 1000L;
        capNhatThoiGianHienThi();

        ibPause.setImageResource(R.drawable.ic_pause);
        isPaused = false;

        exoPlayer.stop();
        exoPlayer.clearMediaItems();
        videoProgressBar.setVisibility(View.VISIBLE);

        String duongDanVideo = baiTap.getVideoHuongDan();
        if (duongDanVideo != null && !duongDanVideo.isEmpty() && duongDanVideo.startsWith("http")) {
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(duongDanVideo)));
            if (viTri + 1 < danhSachBaiTapNho.size()) {
                String nextVideo = danhSachBaiTapNho.get(viTri + 1).getVideoHuongDan();
                if (nextVideo != null && nextVideo.startsWith("http")) {
                    exoPlayer.addMediaItem(MediaItem.fromUri(Uri.parse(nextVideo)));
                }
            }
            exoPlayer.prepare();
            exoPlayer.play();
        } else {
            videoProgressBar.setVisibility(View.GONE);
            batDauDemNguoc();
        }
    }

    private void batDauDemNguoc() {
        if (demNguocThoiGianBaiTap != null) demNguocThoiGianBaiTap.cancel();
        demNguocThoiGianBaiTap = new CountDownTimer(thoiGianConLai, 1000) {
            @Override
            public void onTick(long soGiayConLai) {
                thoiGianConLai = soGiayConLai;
                capNhatThoiGianHienThi();
            }
            @Override public void onFinish() {
                demNguocThoiGianBaiTap = null;
                if (isBatLapLai) hienThiThongTinBaiTap(viTriHienTai);
                else nextBaiTap(viTriHienTai + 1);
            }
        }.start();
    }

    private void tamDungDemNguoc() {
        if (demNguocThoiGianBaiTap != null) demNguocThoiGianBaiTap.cancel();
        isPaused = true;
        ibPause.setImageResource(R.drawable.ic_play);
        if (exoPlayer.isPlaying()) exoPlayer.pause();
    }

    private void tiepTucDemNguoc() {
        batDauDemNguoc();
        isPaused = false;
        ibPause.setImageResource(R.drawable.ic_pause);
        exoPlayer.play();
    }

    private void nextBaiTap(int viTriMoi) {
        if (viTriMoi >= 0 && viTriMoi < danhSachBaiTapNho.size()) {
            viTriHienTai = viTriMoi;
            hienThiThongTinBaiTap(viTriHienTai);
        } else if (viTriMoi >= danhSachBaiTapNho.size()) {
            Toast.makeText(this, "Chúc mừng! Bạn đã hoàn thành bài tập!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void capNhatThoiGianHienThi() {
        int soPhut = (int) (thoiGianConLai / 1000) / 60;
        int soGiay = (int) (thoiGianConLai / 1000) % 60;
        tvThoiGian.setText(String.format(Locale.getDefault(), "%02d:%02d", soPhut, soGiay));
    }

    @Override
    public boolean onKeyDown(int maPhim, KeyEvent suKien) {
        if (maPhim == KeyEvent.KEYCODE_VOLUME_UP || maPhim == KeyEvent.KEYCODE_VOLUME_DOWN) {
            int huongThayDoi = (maPhim == KeyEvent.KEYCODE_VOLUME_UP) ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER;
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, huongThayDoi, AudioManager.FLAG_SHOW_UI);
            seekBarAmLuong.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            return true;
        }
        return super.onKeyDown(maPhim, suKien);
    }

    @Override protected void onPause() { super.onPause(); tamDungDemNguoc(); }
    @Override protected void onDestroy() {
        super.onDestroy();
        if (demNguocThoiGianBaiTap != null) demNguocThoiGianBaiTap.cancel();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
