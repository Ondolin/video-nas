package tech.erichier.videonas;

import android.os.Bundle;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VideoActivity extends AppCompatActivity {

    static SimpleExoPlayer player;
    static StyledPlayerView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        final TextView mediaName = findViewById(R.id.mediaName);
        mediaName.setText(getIntent().getStringExtra("videoname"));

        final AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(1000);

        new android.os.Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mediaName.setAnimation(anim);
                                mediaName.setVisibility(View.GONE);
                            }
                        });
                    }
                },
                10000);

        player = new SimpleExoPlayer.Builder(this).build();

        view = findViewById(R.id.video);
        view.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(getIntent().getStringExtra("videourl"));

        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
        player = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (player.isPlaying())
            player.pause();
        return super.onKeyDown(keyCode, event);
    }
}
