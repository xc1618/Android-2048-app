package com.xc161.Android_2048_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements my2048.OnGameListener {

    private my2048 gameview;
    private TextView scoreview;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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
    public void OnScoreChange(int score) {
        scoreview.setText("Score:" + score);
    }

    @Override
    public void OnGameOver() {
        editor.putString("max", scoreview.getText().toString());
        editor.commit();
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
