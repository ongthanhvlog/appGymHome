package com.example.gymhome.activity;

import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
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
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;

import com.example.gymhome.R;
import com.example.gymhome.model.ThuThach;
import com.example.gymhome.utils.VideoCacheManager;

import java.util.Locale;

public class ChiTietThuThach extends AppCompatActivity {
    private ImageButton ibQuayLai, ibPrevious, ibPause, ibNext, ibLapLaiBaiTap;
    private TextView tvTenBaiTapNho, tvMoTaBaiTapNho, tvThoiGian, tvTienDo;
    private PlayerView playerView;
    private CardView videoCard;
    private ProgressBar videoProgressBar;
    private SeekBar seekBarAmLuong;
    private ThuThach thuThach;
    private ExoPlayer exoPlayer;
    private CountDownTimer demNguocThoiGian;
    private long thoiGianConLai;
    private boolean isPaused = false;
    private boolean isBatLapLai = false;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.baitapnho);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        thuThach = (ThuThach) getIntent().getSerializableExtra("ThuThachData");

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

        thietLapGiaoDien();
        khoiTaoVideoPlayer();
        hienThiDuLieu();
    }

    private void thietLapGiaoDien() {
        ibPrevious.setVisibility(View.INVISIBLE);
        ibNext.setVisibility(View.INVISIBLE);
        tvTienDo.setVisibility(View.GONE);

        ibQuayLai.setOnClickListener(v -> finish());
        
        ibPause.setOnClickListener(v -> {
            if (isPaused) tiepTuc();
            else tamDung();
        });

        ibLapLaiBaiTap.setOnClickListener(v -> {
            isBatLapLai = !isBatLapLai;
            ibLapLaiBaiTap.setColorFilter(isBatLapLai ? Color.parseColor("#FF5252") : Color.parseColor("#9E9E9E"));
        });
        
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        seekBarAmLuong.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBarAmLuong.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        seekBarAmLuong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void khoiTaoVideoPlayer() {
        androidx.media3.exoplayer.DefaultRenderersFactory renderersFactory = new androidx.media3.exoplayer.DefaultRenderersFactory(this)
                .setEnableDecoderFallback(true);

        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(this)
                .setDataSourceFactory(VideoCacheManager.getCacheDataSourceFactory(this));

        exoPlayer = new ExoPlayer.Builder(this, renderersFactory)
                .setMediaSourceFactory(mediaSourceFactory)
                .setHandleAudioBecomingNoisy(true)
                .build();

        playerView.setPlayer(exoPlayer);
        playerView.setResizeMode(androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    videoProgressBar.setVisibility(View.GONE);
                    playerView.setVisibility(View.VISIBLE);
                    if (demNguocThoiGian == null && !isPaused) {
                        batDauDemNguoc();
                    }
                } else if (state == Player.STATE_BUFFERING) {
                    videoProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlayerError(androidx.media3.common.PlaybackException error) {
                videoProgressBar.setVisibility(View.GONE);
                Toast.makeText(ChiTietThuThach.this, "Lỗi phát video", Toast.LENGTH_SHORT).show();
                if (demNguocThoiGian == null && !isPaused) {
                    batDauDemNguoc();
                }
            }
        });
    }

    private void hienThiDuLieu() {
        if (thuThach == null) return;

        tvTenBaiTapNho.setText(thuThach.getTenThuThach());
        tvMoTaBaiTapNho.setText(thuThach.getMoTa());
        thoiGianConLai = (thuThach.getThoiGian() > 0 ? thuThach.getThoiGian() : 30) * 1000L;
        capNhatTxtThoiGian();

        if (thuThach.getVideoHuongDan() != null && !thuThach.getVideoHuongDan().isEmpty()) {
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(thuThach.getVideoHuongDan())));
            exoPlayer.prepare();
            exoPlayer.play();
            playerView.setVisibility(View.VISIBLE);
        } else {
            batDauDemNguoc();
        }
    }

    private void batDauDemNguoc() {
        if (demNguocThoiGian != null) demNguocThoiGian.cancel();
        demNguocThoiGian = new CountDownTimer(thoiGianConLai, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                thoiGianConLai = millisUntilFinished;
                capNhatTxtThoiGian();
            }
            @Override
            public void onFinish() {
                if (isBatLapLai) hienThiDuLieu();
                else {
                    Toast.makeText(ChiTietThuThach.this, "Hoàn thành thử thách!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }.start();
    }

    private void tamDung() {
        if (demNguocThoiGian != null) demNguocThoiGian.cancel();
        isPaused = true;
        ibPause.setImageResource(R.drawable.ic_play);
        if (exoPlayer.isPlaying()) exoPlayer.pause();
    }

    private void tiepTuc() {
        batDauDemNguoc();
        isPaused = false;
        ibPause.setImageResource(R.drawable.ic_pause);
        exoPlayer.play();
    }

    private void capNhatTxtThoiGian() {
        int minutes = (int) (thoiGianConLai / 1000) / 60;
        int seconds = (int) (thoiGianConLai / 1000) % 60;
        tvThoiGian.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (demNguocThoiGian != null) demNguocThoiGian.cancel();
        if (exoPlayer != null) exoPlayer.release();
    }
}
