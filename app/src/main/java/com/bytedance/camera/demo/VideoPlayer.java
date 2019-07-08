package com.bytedance.camera.demo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;

import java.io.File;

public class VideoPlayer extends AppCompatActivity {
    private Button button;
    private VideoView videoView;
    private SeekBar seekBar;
    private boolean type = false;
    private boolean stop = false;
    private static int progress=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        setTitle("Play");

        seekBar=findViewById(R.id.seekBar);
        button = findViewById(R.id.btn_play);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoView.isPlaying()){
                    videoView.pause();
                }
                else{
                    videoView.start();
                }
            }
        });
        videoView = findViewById(R.id.videoView);
        //videoView.setVideoPath(getVideoPath(R.raw.yuminhong));
        /*Todo:添加video的uri*/
        Intent intent = getIntent();
        String url = intent.getExtras().get("data").toString();
        Uri uri =  Uri.parse(url);
        videoView.setVideoURI(uri);
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                //videoView.requestFocus();
                seekBar.setMax(videoView.getDuration());
                System.out.println("During:"+videoView.getDuration()+"MAX:"+seekBar.getMax());
            }
        });

        new Thread() {
            @Override
            public void run() {
                System.out.println("asdasfdasd");
                try {
                    System.out.println(videoView.isPlaying());
                    while (true) {
                        int current = videoView.getCurrentPosition();
                        System.out.println(current);
                        progress = current;
                        seekBar.setProgress(current);
                        type = true;
                        sleep(500);
                        if (stop == true) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.resume();
                videoView.start();
            }
        });
        //seekBar = findViewById(R.id.seekBar);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                System.out.println("seek click");
                int process = seekBar.getProgress();
                progress = process;
                if (videoView != null && type == false) {
                    videoView.seekTo(process);
                }
                if (type) {
                    type = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private String getVideoPath(int resID){
        return "android.resource://" + this.getPackageName() + "/" + resID;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (progress != 0) {
            videoView.seekTo(progress);
        }
        super.onStart();
    }

}
