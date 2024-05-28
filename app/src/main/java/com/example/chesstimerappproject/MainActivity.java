package com.example.chesstimerappproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    package com.example.chesstimer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

    public class MainActivity extends AppCompatActivity {

        private TextView player1TimerTextView;
        private TextView player2TimerTextView;
        private Button startStopPlayer1Button;
        private Button startStopPlayer2Button;

        private CountDownTimer player1CountDownTimer;
        private CountDownTimer player2CountDownTimer;

        private long player1TimeLeftInMillis = 600000; // 10 minutes in milliseconds
        private long player2TimeLeftInMillis = 600000; // 10 minutes in milliseconds

        private boolean player1TimerRunning = false;
        private boolean player2TimerRunning = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            player1TimerTextView = findViewById(R.id.player1_timer);
            player2TimerTextView = findViewById(R.id.player2_timer);
            startStopPlayer1Button = findViewById(R.id.start_stop_player1);
            startStopPlayer2Button = findViewById(R.id.start_stop_player2);

            startStopPlayer1Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (player1TimerRunning) {
                        stopPlayer1Timer();
                    } else {
                        startPlayer1Timer();
                    }
                }
            });

            startStopPlayer2Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (player2TimerRunning) {
                        stopPlayer2Timer();
                    } else {
                        startPlayer2Timer();
                    }
                }
            });

            updateTimerText();
        }

        private void startPlayer1Timer() {
            player1CountDownTimer = new CountDownTimer(player1TimeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    player1TimeLeftInMillis = millisUntilFinished;
                    updateTimerText();
                }

                @Override
                public void onFinish() {
                    player1TimerRunning = false;
                    startStopPlayer1Button.setText("Start Player 1");
                }
            }.start();

            player1TimerRunning = true;
            startStopPlayer1Button.setText("Pause Player 1");
        }

        private void stopPlayer1Timer() {
            player1CountDownTimer.cancel();
            player1TimerRunning = false;
            startStopPlayer1Button.setText("Start Player 1");
        }

        private void startPlayer2Timer() {
            player2CountDownTimer = new CountDownTimer(player2TimeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    player2TimeLeftInMillis = millisUntilFinished;
                    updateTimerText();
                }

                @Override
                public void onFinish() {
                    player2TimerRunning = false;
                    startStopPlayer2Button.setText("Start Player 2");
                }
            }.start();

            player2TimerRunning = true;
            startStopPlayer2Button.setText("Pause Player 2");
        }

        private void stopPlayer2Timer() {
            player2CountDownTimer.cancel();
            player2TimerRunning = false;
            startStopPlayer2Button.setText("Start Player 2");
        }

        private void updateTimerText() {
            int minutes1 = (int) (player1TimeLeftInMillis / 1000) / 60;
            int seconds1 = (int) (player1TimeLeftInMillis / 1000) % 60;

            int minutes2 = (int) (player2TimeLeftInMillis / 1000) / 60;
            int seconds2 = (int) (player2TimeLeftInMillis / 1000) % 60;

            String timeLeftFormatted1 = String.format("%02d:%02d", minutes1, seconds1);
            String timeLeftFormatted2 = String.format("%02d:%02d", minutes2, seconds2);

            player1TimerTextView.setText(timeLeftFormatted1);
            player2TimerTextView.setText(timeLeftFormatted2);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}