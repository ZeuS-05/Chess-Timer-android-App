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
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int TIMER_OPTION_ONE = 10 * 60 * 1000; // 10 minutes in milliseconds
    private static final int TIMER_OPTION_TWO = 5 * 60 * 1000;  // 5 minutes in milliseconds
    private static final int TIMER_OPTION_CUSTOM = 0;           // Custom timer

    private TextView timer1, timer2;
    private Button stopButton;
    private Spinner changeModeSpinner;
    private EditText customTimeInput;
    private final Handler handler = new Handler();
    private boolean isPlayer1Turn = true;
    private long player1Time = TIMER_OPTION_ONE; // Default timer option 1
    private long player2Time = TIMER_OPTION_ONE; // Default timer option 1
    private boolean isTimerRunning = false;

    private final Runnable timerRunnable = new Runnable() {
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
        Button resetButton = findViewById(R.id.resetButton);
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
                this, android.R.layout.simple_spinner_item, modeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        changeModeSpinner.setAdapter(adapter);

        changeModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);

                customTimeInput.setVisibility(View.GONE);
                resetButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.VISIBLE);

                switch (selectedOption) {
                    case "Blitz Mode":
                        updateTimerValues(TIMER_OPTION_ONE);
                        break;
                    case "Rapid Mode":
                        updateTimerValues(TIMER_OPTION_TWO);
                        break;
                    case "Custom Mode":
                        resetButton.setVisibility(View.GONE);
                        startButton.setVisibility(View.GONE);
                        stopButton.setVisibility(View.GONE);
                        customTimeInput.setVisibility(View.VISIBLE); // Show custom time input

                        break;
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

        customTimeInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateCustomTime();
                    resetButton.setVisibility(View.VISIBLE);
                    startButton.setVisibility(View.VISIBLE);
                    stopButton.setVisibility(View.VISIBLE);
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
        switch (selectedOption) {
            case "Custom Mode":
                customTimeInput.setVisibility(View.VISIBLE);
                break;
            case "Blitz Mode":
                player1Time = TIMER_OPTION_ONE;
                player2Time = TIMER_OPTION_ONE;
                break;
            case "Rapid Mode":
                player1Time = TIMER_OPTION_TWO;
                player2Time = TIMER_OPTION_TWO;
                break;
        }

        // Update timer text views
        updateTimerText();

        // Hide custom time input if it's currently visible
        stopButton.setVisibility(View.VISIBLE);

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
        }
        updateTimerText();
    }

    private void updateTimerText() {
        timer1.setText(formatTime(player1Time));
        timer2.setText(formatTime(player2Time));
    }

    private String formatTime(long milliseconds) {
        int minutes = (int) (milliseconds / 1000) / 60;
        int seconds = (int) (milliseconds / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
