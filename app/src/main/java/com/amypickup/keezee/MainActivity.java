package com.amypickup.keezee;

import android.content.Context;
import android.content.Intent;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.io.IOException;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private static final String LOG_TAG = "AudioRecord";
    private String mFileName = null;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private Button[] buttons = null;
    private boolean isClick, isLongClick = false;

    boolean mStartPlaying, mStartRecording = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttons = new Button[8];
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

        for (int i = 0; i < buttons.length; i++) {

            if (buttons[i] != null) {
                buttons[i].setOnClickListener(this);
                buttons[i].setOnLongClickListener(this);
                buttons[i].setOnTouchListener(this);

            }
        }

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
        startRecording(v.getId());
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
                isLongClick=false;
            }
        }
        return true;
    }

    private void startPlaying(int buttonNum) {
        System.out.println("+++++++++++++++++ Start Playing");
        mPlayer = new MediaPlayer();
        try {
            System.out.println("Trying to play file: " + "/audiorecordtest" + String.valueOf(buttonNum) + ".3gp");
            mPlayer.setDataSource(mFileName + "/audiorecordtest" + String.valueOf(buttonNum) + ".3gp");
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        System.out.println("+++++++++++++++++ Stop Playing");
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording(int buttonNum) {
        System.out.println("+++++++++++++++++ Start Recording");

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName + "/audiorecordtest" + String.valueOf(buttonNum) + ".3gp");
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        System.out.println("Recording to file: " + "/audiorecordtest" + String.valueOf(buttonNum) + ".3gp");
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
        mRecorder = null;
    }

    public MainActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
       // mFileName += "/audiorecordtest.3gp";
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
