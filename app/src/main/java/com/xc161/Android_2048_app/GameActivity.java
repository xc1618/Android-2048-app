package com.xc161.Android_2048_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements my2048.OnGameListener {

    private my2048 gameview;
    private TextView scoreview;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean is_first_New_high = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        scoreview = findViewById(R.id.score);
        gameview = findViewById(R.id.gameview);
        gameview.setOnGameListener(this);
        sharedPreferences = getSharedPreferences("my2048", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public void OnScoreChange(final int score) {
        scoreview.setText("Score:" + score);
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message message = handler.obtainMessage(score);
                message.sendToTarget();
            }
        }.start();


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what > sharedPreferences.getInt("max", 0) && is_first_New_high) {
                is_first_New_high = false;
                new AlertDialog.Builder(GameActivity.this).setTitle("Congratulation for new Record!").setNegativeButton("Exit", null).show();
            }
        }
    };

    @Override
    public void OnGameOver() {
        if (sharedPreferences.getInt("max", 0) < Integer.valueOf(scoreview.getText().toString())) {
            editor.putInt("max", Integer.valueOf(scoreview.getText().toString()));
            editor.commit();
        }
        new AlertDialog.Builder(this).setTitle("Game Over").setMessage("You Have Got" + scoreview.getText()).setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gameview.restart();
            }
        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();
    }
}
