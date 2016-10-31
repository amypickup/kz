package com.amypickup.keezee;

import android.widget.Button;

/**
 * Created by amypickup on 10/28/16.
 */
public class Tile {

    private Button buttonId;
    private int soundId;

    public Tile() {
        buttonId = null;
        soundId = 0;
    }

    public Tile(Button button) {
        buttonId = button;
        soundId = 0;
    }

    public Button getButtonId() {
        return buttonId;
    }

    public void setButtonId(Button buttonId) {
        this.buttonId = buttonId;
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }
}
