package com.example.chesstimerappproject;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int TIMER_OPTION_ONE = 10 * 60 * 1000; // 10 minutes in milliseconds
    private static final int TIMER_OPTION_TWO = 5 * 60 * 1000;  // 5 minutes in milliseconds
    private static final int TIMER_OPTION_CUSTOM = 0;           // Custom timer

    private TextView timer1, timer2;
    private Button stopButton;
    private Button resetButton;
    private Spinner changeModeSpinner;
    private EditText customTimeInput;

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
        Button player1Button = findViewById(R.id.player1Button);
        Button player2Button = findViewById(R.id.player2Button);
        Button startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        resetButton = findViewById(R.id.resetButton);
        changeModeSpinner = findViewById(R.id.changeModeSpinner);
        customTimeInput = findViewById(R.id.customTimeInput);

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

        // Set up the spinner for change mode
        List<String> modeOptions = new ArrayList<>();
        modeOptions.add("Blitz Mode"); // TIMER_OPTION_ONE
        modeOptions.add("Rapid Mode"); // TIMER_OPTION_TWO
        modeOptions.add("Custom Mode"); // TIMER_OPTION_CUSTOM

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, modeOptions);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        changeModeSpinner.setAdapter(adapter);

        changeModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);

                customTimeInput.setVisibility(View.GONE); // Hide custom time input by default

                if (selectedOption.equals("Blitz Mode")) {
                    updateTimerValues(TIMER_OPTION_ONE);
                } else if (selectedOption.equals("Rapid Mode")) {
                    updateTimerValues(TIMER_OPTION_TWO);
                } else if (selectedOption.equals("Custom Mode")) {
                    customTimeInput.setVisibility(View.VISIBLE); // Show custom time input
                }

                if (isTimerRunning) {
                    isTimerRunning = false;
                    handler.removeCallbacks(timerRunnable);
                    handler.postDelayed(timerRunnable, 100); // Start the timer with updated values
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Handle Enter key press in customTimeInput EditText
        customTimeInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Update timer values and hide keyboard
                    updateCustomTime();
                    return true;
                }
                return false;
            }
        });
    }

    private void showWinDialog(String winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(winner)
                .setMessage("Choose an option:")
                .setPositiveButton("Restart", (dialog, which) -> resetTimers())
                .setNegativeButton("Change Mode", (dialog, which) -> {
                    String selectedOption = (String) changeModeSpinner.getSelectedItem();
                    if (selectedOption.equals("Blitz Mode")) {
                        updateTimerValues(TIMER_OPTION_TWO);
                    } else if (selectedOption.equals("Rapid Mode")) {
                        updateTimerValues(TIMER_OPTION_ONE);
                    }
                    resetTimers();
                })
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void resetTimers() {
        // Reset timer values to the default for both players
        String selectedOption = (String) changeModeSpinner.getSelectedItem();
        if (selectedOption.equals("Custom Mode")) {
            updateCustomTime();
        } else if (selectedOption.equals("Blitz Mode")) {
            player1Time = TIMER_OPTION_ONE;
            player2Time = TIMER_OPTION_ONE;
        } else if (selectedOption.equals("Rapid Mode")) {
            player1Time = TIMER_OPTION_TWO;
            player2Time = TIMER_OPTION_TWO;
        }

        // Update timer text views
        updateTimerText();

        // Hide custom time input if it's currently visible
        customTimeInput.setVisibility(View.GONE);

        // Stop the timer if running
        if (isTimerRunning) {
            isTimerRunning = false;
            handler.removeCallbacks(timerRunnable);
        }
    }

    private void updateCustomTime() {
        String customTimeString = customTimeInput.getText().toString().trim();
        if (!customTimeString.isEmpty()) {
            try {
                int customMinutes = Integer.parseInt(customTimeString);
                player1Time = (long) customMinutes * 60 * 1000;
                player2Time = (long) customMinutes * 60 * 1000;
            } catch (NumberFormatException e) {
                // Handle invalid input (non-numeric)
                player1Time = TIMER_OPTION_ONE;
                player2Time = TIMER_OPTION_ONE;
            }
        } else {
            // Handle empty input
            player1Time = TIMER_OPTION_ONE;
            player2Time = TIMER_OPTION_ONE;
        }

        // Update timer text views
        updateTimerText();

        // Hide custom time input and close keyboard
        customTimeInput.setVisibility(View.GONE);
        hideKeyboard(customTimeInput);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            case TIMER_OPTION_CUSTOM:
                updateCustomTime();
                break;
            default:
                player1Time = TIMER_OPTION_ONE;
                player2Time = TIMER_OPTION_ONE;
                break;
        }
        updateTimerText(); // Update the displayed timer values
    }

    // Method to update the timer text views based on the remaining time
    private void updateTimerText() {
        timer1.setText(formatTime(player1Time));
        timer2.setText(formatTime(player2Time));
    }

    // Helper method to format time from milliseconds to mm:ss
    private String formatTime(long timeInMillis) {
        int minutes = (int) (timeInMillis / 1000) / 60;
        int seconds = (int) (timeInMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable); // Remove callbacks to prevent memory leaks
    }
}
