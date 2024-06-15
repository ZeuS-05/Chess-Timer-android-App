package com.example.chesstimerappproject;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int TIMER_OPTION_ONE = 10 * 60 * 1000; // 10 minutes in milliseconds
    private static final int TIMER_OPTION_TWO = 5 * 60 * 1000;  // 5 minutes in milliseconds

    private TextView timer1, timer2;
    private Button player1Button, player2Button;
    private Button startButton, stopButton, resetButton, changeModeButton;

    private Handler handler = new Handler();
    private boolean isPlayer1Turn = true;
    private long player1Time = TIMER_OPTION_ONE; // Default timer option 1
    private long player2Time = TIMER_OPTION_ONE; // Default timer option 1
    private boolean isTimerRunning = false;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlayer1Turn) {
                player1Time -= 1000;
                if (player1Time <= 0) {
                    timer1.setText("00:00");
                    handler.removeCallbacks(this);
                    showWinDialog("Player 2 Wins!");
                    return;
                }
                updateTimerText();
            } else {
                player2Time -= 1000;
                if (player2Time <= 0) {
                    timer2.setText("00:00");
                    handler.removeCallbacks(this);
                    showWinDialog("Player 1 Wins!");
                    return;
                }
                updateTimerText();
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
        changeModeButton = findViewById(R.id.changeModeButton);

        // Initialize isTimerRunning to false
        isTimerRunning = false;

        // Set initial timer values and update text
        updateTimerText();

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
                resetTimers();
            }
        });

        changeModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle between timer options
                if (player1Time == TIMER_OPTION_ONE) {
                    updateTimerValues(TIMER_OPTION_TWO);
                } else {
                    updateTimerValues(TIMER_OPTION_ONE);
                }
                if (isTimerRunning) {
                    isTimerRunning = false;
                    handler.removeCallbacks(timerRunnable);
                    handler.postDelayed(timerRunnable, 100); // Start the timer with updated values
                }
            }
        });
    }

    private void showWinDialog(String winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(winner)
                .setMessage("Choose an option:")
                .setPositiveButton("Restart", (dialog, which) -> resetTimers())
                .setNegativeButton("Change Mode", (dialog, which) -> {
                    if (player1Time == TIMER_OPTION_ONE) {
                        updateTimerValues(TIMER_OPTION_TWO);
                    } else {
                        updateTimerValues(TIMER_OPTION_ONE);
                    }
                    resetTimers();
                })
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void resetTimers() {
        // Reset timer values to the default for both players
        if (player1Time == TIMER_OPTION_ONE || player2Time == TIMER_OPTION_ONE) {
            player1Time = TIMER_OPTION_ONE;
            player2Time = TIMER_OPTION_ONE;
        } else {
            player1Time = TIMER_OPTION_TWO;
            player2Time = TIMER_OPTION_TWO;
        }
        // Update timer text views
        updateTimerText();

        // Stop the timer if running
        if (isTimerRunning) {
            isTimerRunning = false;
            handler.removeCallbacks(timerRunnable);
        }
    }

    private void updateTimerValues(int selectedOption) {
        switch (selectedOption) {
            case TIMER_OPTION_ONE:
                player1Time = TIMER_OPTION_ONE;
                player2Time = TIMER_OPTION_ONE;
                break;
            case TIMER_OPTION_TWO:
                player1Time = TIMER_OPTION_TWO;
                player2Time = TIMER_OPTION_TWO;
                break;
            default:
                player1Time = TIMER_OPTION_ONE;
                player2Time = TIMER_OPTION_ONE;
                break;
        }
        updateTimerText(); // Update the displayed timer values
    }

    private void updateTimerText() {
        int minutes1 = (int) (player1Time / 1000) / 60;
        int seconds1 = (int) (player1Time / 1000) % 60;
        timer1.setText(String.format("%02d:%02d", minutes1, seconds1));

        int minutes2 = (int) (player2Time / 1000) / 60;
        int seconds2 = (int) (player2Time / 1000) % 60;
        timer2.setText(String.format("%02d:%02d", minutes2, seconds2));
    }
}
