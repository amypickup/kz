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

    private HashMap<Integer, Tile> tiles;

    private boolean isClick, isLongClick = false;
    boolean plays = false, loaded = false;


    private float actVolume, maxVolume, volume;
    private int counter;

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


        tiles = new HashMap<Integer, Tile>();

        // add button references to tiles list
        tiles.put(new Integer(R.id.button0), new Tile((Button) findViewById(R.id.button0)));
        tiles.put(new Integer(R.id.button1), new Tile((Button) findViewById(R.id.button1)));
        tiles.put(new Integer(R.id.button2), new Tile((Button) findViewById(R.id.button2)));
        tiles.put(new Integer(R.id.button3), new Tile((Button) findViewById(R.id.button3)));
        tiles.put(new Integer(R.id.button4), new Tile((Button) findViewById(R.id.button4)));
        tiles.put(new Integer(R.id.button5), new Tile((Button) findViewById(R.id.button5)));
        tiles.put(new Integer(R.id.button6), new Tile((Button) findViewById(R.id.button6)));
        tiles.put(new Integer(R.id.button7), new Tile((Button) findViewById(R.id.button7)));

        // create onClickListeners for each button in tiles array
        for (Tile t : tiles.values()) {
            if (t != null) {
                t.getButtonId().setOnClickListener(this);
                t.getButtonId().setOnLongClickListener(this);
                t.getButtonId().setOnTouchListener(this);

            }
        }

    }

    @Override
    public void onClick(View v) {
        // on short click, play recorded sound
        if(mPlayer != null) {
            stopPlaying();
        }
        System.out.println("KEEZEEE:  Click");
        startPlaying(v.getId());
        mStartPlaying = !mStartPlaying;
        isClick = true;


    }

    @Override
    public boolean onLongClick(View v) {
        // On long click, start recording, pass in the filename
        // System.out.println("KEEZEE: Long Click on " + v.getId());

        if(mPlayer != null) {
            stopPlaying();
        }
        startRecording(v.getId());
        isLongClick = true;
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // On touch release, stop play or record
        v.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // System.out.println("KEEZEE: Click Released " + isLongClick);
            if(isLongClick) {
                stopRecording(v.getId());
                isLongClick=false;
            }
        }
        return true;
    }

    private void startPlaying(int viewId) {

// Is the sound loaded does it already play?
   /*     if (loaded && !plays) {
            soundPool.play(soundIds[0], volume, volume, 1, 0, 1f);
            counter = counter++;
            Toast.makeText(this, "Played sound", Toast.LENGTH_SHORT).show();
            plays = true;
        }
*/
        System.out.println("KEEZEE: Trying to play button " + viewId + " with sound " + tiles.get(viewId).getSoundId());

        soundPool.play(tiles.get(viewId).getSoundId(), volume, volume, 1, 0, 1f);

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
        System.out.println("KEEZEE: Stop Playing");
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

    private void startRecording(int buttonKey) {
        System.out.println("KEEZEE: Start Recording on button " + buttonKey);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);


        mRecorder.setOutputFile(mFileName + "/keezee" + buttonKey + ".3gp");

        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();

    }

    private void stopRecording(int buttonKey) {
        mRecorder.stop();
        mRecorder.release();

        System.out.println("KEEZEE: Attempting to save " + mFileName + "/keezee" + buttonKey + ".3gp");

        tiles.get(buttonKey).setSoundId(soundPool.load(mFileName + "/keezee" + buttonKey + ".3gp", 1));

        mRecorder = null;

        System.out.println("KEEZEE: Stopping Recording on button " + tiles.get(buttonKey).getButtonId().getId() +
                ", sound " + tiles.get(buttonKey).getSoundId());

    }

    public MainActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
      //  mFileName += "/audiorecordtest.3gp";
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
