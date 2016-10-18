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


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecord";
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;

    private Button[] buttons = null;
    private boolean isLongClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttons = new Button[8];
        // add buttons to button array
        buttons[0] = (Button) findViewById(R.id.button0);
        buttons[1] = (Button) findViewById(R.id.button1);
        buttons[2] = (Button) findViewById(R.id.button4);
        buttons[3] = (Button) findViewById(R.id.button5);

        buttons[0].setOnClickListener(onClickListener);
        buttons[1].setOnLongClickListener(onLongClickListener);
        buttons[1].setOnTouchListener(onTouchListener);

        // create onClickListeners for each button in array
/*
        for(int i = 0; i<buttons.length; i++) {

            if (buttons[i] != null) {
                buttons[i].setOnClickListener(onClickListener);
                buttons[i].setOnLongClickListener(onLongClickListener);
                buttons[i].setOnTouchListener(onTouchListener);

            }
        }
*/

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // play audio
            System.out.println("+++++++++++++++++ Click");
            startPlaying();
            System.out.println("Playing..........");
            stopPlaying();

        }
    };

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View pView) {
            // Do something when your hold starts here.
            System.out.println("+++++++++++++++++ Long Click");
            startRecording();
            isLongClick = true;
            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if(isLongClick) {
                    System.out.println("+++++++++++++++++ Click Released");
                    stopRecording();
                    isLongClick=false;
                }
            }
            return true;
        }
    };

    private void startPlaying() {
        System.out.println("+++++++++++++++++ Start Playing");
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
