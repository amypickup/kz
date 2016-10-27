package com.amypickup.keezee;

import android.content.Context;
import android.content.Intent;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.io.IOException;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private static final String LOG_TAG = "AudioRecord";
    private static final int MAX_STREAMS = 8;

    // Stream type.
    private static final int streamType = AudioManager.STREAM_MUSIC;

    private String mFileName = null;

    private SoundPool soundPool;
    private AudioManager audioManager;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private Button[] buttons = null;
    private int[] soundIds = null;

    HashMap buttonMap = null;

    private boolean isClick, isLongClick = false;
    boolean plays = false, loaded = false;


    private float actVolume, maxVolume, volume;
    private int currentButtonId, counter;

    boolean mStartPlaying, mStartRecording = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        //Hardware buttons setting to adjust the media sound
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        counter = 0;

        // For Android SDK >= 21
        if (Build.VERSION.SDK_INT >= 21 ) {

            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder= new SoundPool.Builder()
                    .setAudioAttributes(audioAttrib)
                    .setMaxStreams(MAX_STREAMS);

            this.soundPool = builder.build();
        }
        // for Android SDK < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });





        buttons = new Button[MAX_STREAMS];
        // add buttons to button array
        buttons[0] = (Button) findViewById(R.id.button0);
        buttons[1] = (Button) findViewById(R.id.button1);
        buttons[2] = (Button) findViewById(R.id.button2);
        buttons[3] = (Button) findViewById(R.id.button3);
        buttons[4] = (Button) findViewById(R.id.button4);
        buttons[5] = (Button) findViewById(R.id.button5);
        buttons[6] = (Button) findViewById(R.id.button6);
        buttons[7] = (Button) findViewById(R.id.button7);

        // create onClickListeners for each button in array

        for (int i = 0; i < MAX_STREAMS; i++) {

            if (buttons[i] != null) {
                buttons[i].setOnClickListener(this);
                buttons[i].setOnLongClickListener(this);
                buttons[i].setOnTouchListener(this);

            }
        }

        soundIds = new int[MAX_STREAMS];
        buttonMap = new HashMap();

    }

    @Override
    public void onClick(View v) {
        // on short click, play recorded sound
        if(mPlayer != null) {
            stopPlaying();
        }
        System.out.println("+++++++++++++++++ Click");
        startPlaying(v.getId());
        mStartPlaying = !mStartPlaying;
        isClick = true;
    }

    @Override
    public boolean onLongClick(View v) {
        // On long click, start recording, pass in the filename
        System.out.println("+++++++++++++++++ Long Click");

        if(mPlayer != null) {
            stopPlaying();
        }
        currentButtonId = v.getId();
        startRecording();
        isLongClick = true;
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // On touch release, stop play or record
        v.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            System.out.println("+++++++++++++++++ Click Released " + isLongClick);
            if(isLongClick) {
                stopRecording();
             //   this.soundId = this.soundPool.load(this, mFileName, 1);
                isLongClick=false;
            }
        }
        return true;
    }

    private void startPlaying(int buttonNum) {
        System.out.println("+++++++++++++++++ Start Playing");

// Is the sound loaded does it already play?
   /*     if (loaded && !plays) {
            soundPool.play(soundID, volume, volume, 1, 0, 1f);
            counter = counter++;
            Toast.makeText(this, "Played sound", Toast.LENGTH_SHORT).show();
            plays = true;
        }
*/
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        System.out.println("+++++++++++++++++ Stop Playing");
/*
        if (plays) {
            soundPool.stop(soundID);
            soundID = soundPool.load(this, R.raw.beep, counter);
            Toast.makeText(this, "Stop sound", Toast.LENGTH_SHORT).show();
            plays = false;
        }
        */

        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        System.out.println("+++++++++++++++++ Start Recording");

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();

    }

    private void stopRecording() {
        System.out.println("+++++++++++++++++ Stop Recording");
        mRecorder.stop();
        mRecorder.release();

        //int soundID = soundPool.load(mFileName, 1);
        //buttonMap.put(currentButtonId, soundID);

        System.out.println("Attempting to save then play " + mFileName);


        soundPool.play(soundPool.load(mFileName, 1), volume, volume, 1, 0, 1f);

        mRecorder = null;
    }

    public MainActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void makeToast(String toastText) {
        Context context = getApplicationContext();
        CharSequence text = toastText;
        int duration = Toast.LENGTH_SHORT;

        Toast.makeText(context, text, duration).show();
    }

}
