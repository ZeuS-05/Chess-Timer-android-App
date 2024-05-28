package com.example.chesstimerappproject;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView timer1, timer2;
    private Button player1Button, player2Button;
    private Button startButton, stopButton, resetButton;

    private Handler handler = new Handler();
    private boolean isPlayer1Turn = true;
    private long player1Time = 10 * 60 * 1000; // 10 minutes in milliseconds
    private long player2Time = 10 * 60 * 1000; // 10 minutes in milliseconds
    private boolean isTimerRunning = false;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlayer1Turn) {
                player1Time -= 1000;
                if (player1Time <= 0) {
                    timer1.setText("00:00");
                    handler.removeCallbacks(this);
                    return;
                }
                int minutes = (int) (player1Time / 1000) / 60;
                int seconds = (int) (player1Time / 1000) % 60;
                timer1.setText(String.format("%02d:%02d", minutes, seconds));
            } else {
                player2Time -= 1000;
                if (player2Time <= 0) {
                    timer2.setText("00:00");
                    handler.removeCallbacks(this);
                    return;
                }
                int minutes = (int) (player2Time / 1000) / 60;
                int seconds = (int) (player2Time / 1000) % 60;
                timer2.setText(String.format("%02d:%02d", minutes, seconds));
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer1 = findViewById(R.id.timer1);
        timer2 = findViewById(R.id.timer2);
        player1Button = findViewById(R.id.player1Button);
        player2Button = findViewById(R.id.player2Button);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        resetButton = findViewById(R.id.resetButton);

        // Initialize isTimerRunning to false
        isTimerRunning = false;

        player1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlayer1Turn) {
                    isPlayer1Turn = true;
                    handler.removeCallbacks(timerRunnable);
                    if (isTimerRunning) {
                        handler.post(timerRunnable);
                    }
                }
            }
        });

        player2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayer1Turn) {
                    isPlayer1Turn = false;
                    handler.removeCallbacks(timerRunnable);
                    if (isTimerRunning) {
                        handler.post(timerRunnable);
                    }
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning) {
                    isTimerRunning = true;
                    handler.post(timerRunnable);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    isTimerRunning = false;
                    handler.removeCallbacks(timerRunnable);
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset timer values
                player1Time = 10 * 60 * 1000;
                player2Time = 10 * 60 * 1000;

                // Update timer text views
                updateTimerText();

                // Stop the timer if running
                if (isTimerRunning) {
                    isTimerRunning = false;
                    handler.removeCallbacks(timerRunnable);
                }
            }
        });
    }


    // Helper method to update timer text views
    private void updateTimerText() {
        int minutes1 = (int) (player1Time / 1000) / 60;
        int seconds1 = (int) (player1Time / 1000) % 60;
        timer1.setText(String.format("%02d:%02d", minutes1, seconds1));

        int minutes2 = (int) (player2Time / 1000) / 60;
        int seconds2 = (int) (player2Time / 1000) % 60;
        timer2.setText(String.format("%02d:%02d", minutes2, seconds2));
    }
}
