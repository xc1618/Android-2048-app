package com.xc161.Android_2048_app;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class play_music extends Handler {
    play_music() {
    }

    private SoundPool soundPool;
    private int moveid, mergeid;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 0x001:      //初始化音频资源
                soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 100);
                mergeid = soundPool.load((Context) msg.obj, R.raw.merge, 100);
                moveid = soundPool.load((Context) msg.obj, R.raw.move, 100);
                break;
            case 0x002:
                soundPool.play(moveid, 1, 1, 5, 0, 1);
                break;
            case 0x003:
                soundPool.play(mergeid, 1, 1, 5, 0, 1);
                break;
        }
    }
}
