package cheerfulpeach.sortinghat;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Player extends AppCompatActivity {

    MediaPlayer mPlayer;
    MediaPlayer mPlayer2;

    Button startButton;
    Button pauseButton;
    Button stopButton;

    Button startButton2;
    Button pauseButton2;
    Button stopButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mPlayer = MediaPlayer.create(this, R.raw.mp);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });
        startButton = (Button) findViewById(R.id.start);
        pauseButton = (Button) findViewById(R.id.pause);
        stopButton = (Button) findViewById(R.id.stop);

        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAudio(view);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseAudio(view);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAudio(view);
            }
        });


        mPlayer2 = MediaPlayer.create(this, R.raw.mp2);
        mPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp2) {
                stop();
            }
        });
        startButton2 = (Button) findViewById(R.id.start2);
        pauseButton2 = (Button) findViewById(R.id.pause2);
        stopButton2 = (Button) findViewById(R.id.stop2);

        pauseButton2.setEnabled(false);
        stopButton2.setEnabled(false);

        startButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAudio2(view);
            }
        });

        pauseButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseAudio2(view);
            }
        });

        stopButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAudio2(view);
            }
        });
    }

    private void stop(){
        mPlayer.stop();
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        try {
            mPlayer.prepare();
            mPlayer.seekTo(0);
            startButton.setEnabled(true);
        }
        catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void startAudio(View view){

        mPlayer.start();
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    public void pauseAudio(View view){

        mPlayer.pause();
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    public void stopAudio(View view){

        stop();
    }

    public void startAudio2(View view){

        mPlayer2.start();
        startButton2.setEnabled(false);
        pauseButton2.setEnabled(true);
        stopButton2.setEnabled(true);
    }

    public void pauseAudio2(View view){

        mPlayer2.pause();
        startButton2.setEnabled(true);
        pauseButton2.setEnabled(false);
        stopButton.setEnabled(true);
    }

    public void stopAudio2(View view){

        stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (stopButton.isEnabled()) {
            stop();
        }
    }
}
