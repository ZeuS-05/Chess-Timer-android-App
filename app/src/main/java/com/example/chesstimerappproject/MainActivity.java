package com.example.chesstimerappproject;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int TIMER_OPTION_ONE = 10 * 60 * 1000; // 10 minutes in milliseconds
    private static final int TIMER_OPTION_TWO = 5 * 60 * 1000;  // 5 minutes in milliseconds
    private static final int TIMER_OPTION_CUSTOM = 0;           // Custom timer
    private AlertDialog dialog; // Declare dialog at the class level
    private RadioButton radioButtonCustom, radioButtonBlitz, radioButtonRapid;
    private TextView timer1, timer2;
    private Spinner changeModeSpinner;
    private EditText customTimeInput;
    private final Handler handler = new Handler();
    private boolean isPlayer1Turn = true;
    private long player1Time = TIMER_OPTION_ONE; // Default timer option 1
    private long player2Time = TIMER_OPTION_ONE; // Default timer option 1
    private boolean isTimerRunning = false;
    private MediaPlayer mediaPlayer1;
    private MediaPlayer mediaPlayer2;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlayer1Turn) {
                player1Time -= 1000;
                updateTimerText();

                if (player1Time <= 0) {
                    timer1.setText("00:00");
                    handler.removeCallbacks(this);
                    showWinDialog("Player 2 Wins!");
                    return;
                }

                if (player1Time <= 20000) {
                    if (mediaPlayer1 != null && !mediaPlayer1.isPlaying()) {
                        mediaPlayer1.start(); // Start Player 1's timer sound
                    }
                } else {
                    if (mediaPlayer1 != null && mediaPlayer1.isPlaying()) {
                        mediaPlayer1.pause(); // Pause Player 1's timer sound when it's not their turn
                    }
                }
            } else {
                player2Time -= 1000;
                updateTimerText();

                if (player2Time <= 0) {
                    timer2.setText("00:00");
                    handler.removeCallbacks(this);
                    showWinDialog("Player 1 Wins!");
                    return;
                }

                if (player2Time <= 20000) {
                    if (mediaPlayer2 != null && !mediaPlayer2.isPlaying()) {
                        mediaPlayer2.start(); // Start Player 2's timer sound
                    }
                } else {
                    if (mediaPlayer2 != null && mediaPlayer2.isPlaying()) {
                        mediaPlayer2.pause(); // Pause Player 2's timer sound when it's not their turn
                    }
                }
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
        Button stopButton = findViewById(R.id.stopButton);
        Button resetButton = findViewById(R.id.resetButton);
        changeModeSpinner = findViewById(R.id.changeModeSpinner);
        customTimeInput = findViewById(R.id.customTimeInput);

        // Initialize Media Players for timer sounds
        mediaPlayer1 = MediaPlayer.create(this, R.raw.timer_p1); // Assuming timer_p1.mp3 is stored in res/raw/
        mediaPlayer2 = MediaPlayer.create(this, R.raw.timer_p2); // Assuming timer_p2.mp3 is stored in res/raw/

        // Set audio attributes for media players
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mediaPlayer1.setAudioAttributes(audioAttributes);
        mediaPlayer2.setAudioAttributes(audioAttributes);

        // Set initial timer values and update text
        updateTimerText();

        player1Button.setOnClickListener(v -> switchPlayerTurn(true));
        player2Button.setOnClickListener(v -> switchPlayerTurn(false));

        startButton.setOnClickListener(v -> startTimer());
        stopButton.setOnClickListener(v -> stopTimer());
        resetButton.setOnClickListener(v -> resetTimers());

        setupModeSpinner();

        customTimeInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateCustomTime();
                return true;
            }
            return false;
        });
    }

    private void switchPlayerTurn(boolean player1Turn) {
        isPlayer1Turn = player1Turn;
        handler.removeCallbacks(timerRunnable);
        if (isTimerRunning) {
            handler.post(timerRunnable);
        }

        // Pause or stop current player's media player
        if (isPlayer1Turn) {
            if (mediaPlayer2.isPlaying()) {
                mediaPlayer2.pause();
            }
            if (mediaPlayer1 != null && !mediaPlayer1.isPlaying()) {
                mediaPlayer1.start(); // Resume Player 1's timer sound
            }
        } else {
            if (mediaPlayer1.isPlaying()) {
                mediaPlayer1.pause();
            }
            if (mediaPlayer2 != null && !mediaPlayer2.isPlaying()) {
                mediaPlayer2.start(); // Resume Player 2's timer sound
            }
        }
    }

    private void startTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true;
            handler.post(timerRunnable);
        }
    }

    private void stopTimer() {
        if (isTimerRunning) {
            isTimerRunning = false;
            handler.removeCallbacks(timerRunnable);

            // Pause current player's media player
            if (isPlayer1Turn && mediaPlayer1.isPlaying()) {
                mediaPlayer1.pause();
            } else if (!isPlayer1Turn && mediaPlayer2.isPlaying()) {
                mediaPlayer2.pause();
            }
        }
    }

    private void resetTimers() {
        String selectedOption = (String) changeModeSpinner.getSelectedItem();
        switch (selectedOption) {
            case "Custom Mode":
                CustomMode();
                break;
            case "Blitz Mode":
                updateTimerValues(TIMER_OPTION_ONE);
                break;
            case "Rapid Mode":
                updateTimerValues(TIMER_OPTION_TWO);
                break;
        }
        updateTimerText();
        stopTimer();
    }

    private void updateCustomTime() {
        String customTimeString = customTimeInput.getText().toString().trim();

        if (!customTimeString.isEmpty()) {
            try {
                int customMinutes = Integer.parseInt(customTimeString);
                if (customMinutes > 0) {
                    player1Time = (long) customMinutes * 60 * 1000;
                    player2Time = (long) customMinutes * 60 * 1000;
                    updateTimerText();
                    customTimeInput.setVisibility(View.GONE);
                    hideKeyboard(customTimeInput);
                    setButtonVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "Please select a time greater than 0", Toast.LENGTH_SHORT).show();
                    CustomMode(); // Ensure CustomMode handles visibility correctly
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid input. Please enter a valid number", Toast.LENGTH_SHORT).show();
                CustomMode(); // Ensure CustomMode handles visibility correctly
            }
        } else {
            Toast.makeText(this, "Please enter a time", Toast.LENGTH_SHORT).show();
            CustomMode(); // Ensure CustomMode handles visibility correctly
        }
    }

    private void CustomMode() {
        setButtonVisibility(View.GONE);
        customTimeInput.setVisibility(View.VISIBLE);
    }

    private void setButtonVisibility(int visibility) {
        Button resetButton = findViewById(R.id.resetButton);
        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        resetButton.setVisibility(visibility);
        startButton.setVisibility(visibility);
        stopButton.setVisibility(visibility);
    }

    private void setupModeSpinner() {
        List<String> modeOptions = new ArrayList<>();
        modeOptions.add("Blitz Mode");
        modeOptions.add("Rapid Mode");
        modeOptions.add("Custom Mode");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, modeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        changeModeSpinner.setAdapter(adapter);

        changeModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);

                customTimeInput.setVisibility(View.GONE);
                setButtonVisibility(View.VISIBLE);

                switch (selectedOption) {
                    case "Blitz Mode":
                        updateTimerValues(TIMER_OPTION_ONE);
                        break;
                    case "Rapid Mode":
                        updateTimerValues(TIMER_OPTION_TWO);
                        break;
                    case "Custom Mode":
                        CustomMode();
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
        }
        updateTimerText();
    }

    private void updateTimerText() {
        timer1.setText(formatTime(player1Time));
        timer2.setText(formatTime(player2Time));
    }

    String formatTime(long milliseconds) {
        int minutes = (int) (milliseconds / 1000) / 60;
        int seconds = (int) (milliseconds / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showWinDialog(String winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(winner)
                .setMessage("Choose an option:");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_mode_selection, null);
        builder.setView(dialogView);

        radioButtonCustom = dialogView.findViewById(R.id.radioButtonCustom);
        radioButtonBlitz = dialogView.findViewById(R.id.radioButtonBlitz);
        radioButtonRapid = dialogView.findViewById(R.id.radioButtonRapid);
        Button enterButton = dialogView.findViewById(R.id.enterButton);

        enterButton.setOnClickListener(v -> {
            if (radioButtonCustom.isChecked()) {
                changeModeSpinner.setSelection(2); // Select "Custom Mode" in spinner
                CustomMode(); // Ensure CustomMode handles visibility correctly
            } else if (radioButtonBlitz.isChecked()) {
                changeModeSpinner.setSelection(0); // Select "Blitz Mode" in spinner
                updateTimerValues(TIMER_OPTION_ONE);
            } else if (radioButtonRapid.isChecked()) {
                changeModeSpinner.setSelection(1); // Select "Rapid Mode" in spinner
                updateTimerValues(TIMER_OPTION_TWO);
            } else {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            updateTimerText();

            if (isTimerRunning) {
                isTimerRunning = false;
                handler.removeCallbacks(timerRunnable);
            }

            dialog.dismiss();
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

        dialog = builder.create(); // Assign the created dialog to the class-level variable
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release media players when activity is destroyed
        if (mediaPlayer1 != null) {
            mediaPlayer1.release();
            mediaPlayer1 = null;
        }
        if (mediaPlayer2 != null) {
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
    }
}
