package com.diary.superjournalapp.applock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.constants.ApplicationConstants;

import java.util.ArrayList;

public class AppLock extends AppCompatActivity implements View.OnClickListener {

    private View view_01, view_02, view_03, view_04;
    private Button btn_01, btn_02, btn_03, btn_04, btn_05, btn_06, btn_07, btn_08, btn_09, btn_00, btn_forgot;
    private ImageButton btn_clear;
    private TextView passcodeTitle, passcodeInstruction, changePassword;

    private ArrayList<String> numbers_list = new ArrayList<>();

    private String passCode = "";
    private String num_01, num_02, num_03, num_04;
    private String tempPasscode = ""; // For storing temporary passcode during change operation

    // Mode flags
    private Boolean isNewPasscode = false;
    private Boolean isChangingPasscode = false;
    private Boolean isConfirmingNewPasscode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_improved);

        Intent intent = getIntent();
        isNewPasscode = intent.getBooleanExtra(ApplicationConstants.IS_NEW_PASSCODE, false);

        initializeComponents();
        updateUIForMode();



    }

    private void initializeComponents() {
        // Initialize views
        view_01 = findViewById(R.id.view_01);
        view_02 = findViewById(R.id.view_02);
        view_03 = findViewById(R.id.view_03);
        view_04 = findViewById(R.id.view_04);

        // Initialize text views
        passcodeTitle = findViewById(R.id.passcode_title);
        passcodeInstruction = findViewById(R.id.passcode_instruction);
        changePassword = findViewById(R.id.change_password);

        // Initialize buttons
        btn_01 = findViewById(R.id.btn_01);
        btn_02 = findViewById(R.id.btn_02);
        btn_03 = findViewById(R.id.btn_03);
        btn_04 = findViewById(R.id.btn_04);
        btn_05 = findViewById(R.id.btn_05);
        btn_06 = findViewById(R.id.btn_06);
        btn_07 = findViewById(R.id.btn_07);
        btn_08 = findViewById(R.id.btn_08);
        btn_09 = findViewById(R.id.btn_09);
        btn_00 = findViewById(R.id.btn_0);
        btn_clear = findViewById(R.id.btn_clear);
        btn_forgot = findViewById(R.id.btn_forgot);

        // Set click listeners
        btn_00.setOnClickListener(this);
        btn_01.setOnClickListener(this);
        btn_02.setOnClickListener(this);
        btn_03.setOnClickListener(this);
        btn_04.setOnClickListener(this);
        btn_05.setOnClickListener(this);
        btn_06.setOnClickListener(this);
        btn_07.setOnClickListener(this);
        btn_08.setOnClickListener(this);
        btn_09.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_forgot.setOnClickListener(this);

        // Show change password button only if passcode exists
        if (!isNewPasscode && getPassCode().length() > 0) {
            changePassword.setVisibility(View.VISIBLE);
            changePassword.setOnClickListener(v -> startChangePasscodeProcess());
        }
    }

    /**
     * Updates UI elements based on the current mode (new passcode, change passcode, etc)
     */
    private void updateUIForMode() {
        if (isNewPasscode) {
            passcodeTitle.setText("Set New Passcode");
            passcodeInstruction.setText("Create a 4-digit passcode");
        } else if (isChangingPasscode) {
            if (isConfirmingNewPasscode) {
                passcodeTitle.setText("Confirm New Passcode");
                passcodeInstruction.setText("Re-enter your new passcode");
            } else {
                passcodeTitle.setText("Change Passcode");
                passcodeInstruction.setText("Enter your current passcode");
            }
        } else {
            passcodeTitle.setText("Enter Passcode");
            passcodeInstruction.setText("Please enter your 4-digit passcode");
        }
    }

    /**
     * Start the passcode change process
     */
    private void startChangePasscodeProcess() {
        isChangingPasscode = true;
        updateUIForMode();
        resetPasscodeInput();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        // Number buttons
        if (viewId == R.id.btn_01) {
            numbers_list.add("1");
            animateButton(btn_01);
        } else if (viewId == R.id.btn_02) {
            numbers_list.add("2");
            animateButton(btn_02);
        } else if (viewId == R.id.btn_03) {
            numbers_list.add("3");
            animateButton(btn_03);
        } else if (viewId == R.id.btn_04) {
            numbers_list.add("4");
            animateButton(btn_04);
        } else if (viewId == R.id.btn_05) {
            numbers_list.add("5");
            animateButton(btn_05);
        } else if (viewId == R.id.btn_06) {
            numbers_list.add("6");
            animateButton(btn_06);
        } else if (viewId == R.id.btn_07) {
            numbers_list.add("7");
            animateButton(btn_07);
        } else if (viewId == R.id.btn_08) {
            numbers_list.add("8");
            animateButton(btn_08);
        } else if (viewId == R.id.btn_09) {
            numbers_list.add("9");
            animateButton(btn_09);
        } else if (viewId == R.id.btn_0) {
            numbers_list.add("0");
            animateButton(btn_00);
        } 
        // Clear button
        else if (viewId == R.id.btn_clear) {
            if (numbers_list.size() > 0) {
                // Remove only the last digit if there's input
                numbers_list.remove(numbers_list.size() - 1);
            } else {
                numbers_list.clear();
            }
            animateButton(btn_clear);
        }
        // Forgot password button
        else if (viewId == R.id.btn_forgot) {
            showForgotPasscodeDialog();
            return; // Skip passNumber call
        }
        
        // Update the dots UI
        passNumber(numbers_list);
    }
    
    /**
     * Add simple button press animation
     */
    private void animateButton(View button) {
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animation.setDuration(200);
        button.startAnimation(animation);
    }
    
    /**
     * Show dialog for forgotten passcode
     */
    private void showForgotPasscodeDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Forgot Passcode")
            .setMessage("If you've forgotten your passcode, you'll need to reset the app. This will clear all app settings but won't delete your journal entries.")
            .setPositiveButton("Reset", (dialog, which) -> {
                // Reset passcode but keep journal data
                clearPasscode();
                Toast.makeText(this, "Passcode reset successfully", Toast.LENGTH_LONG).show();
                finish();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    /**
     * Clear saved passcode
     */
    private void clearPasscode() {
        SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("passcode");
        editor.putBoolean(ApplicationConstants.IS_PASSCODE_ENABLED, false);
        editor.apply();
    }

    private void passNumber(ArrayList<String> numbersList) {
        // Update the passcode dot indicators
        resetDots();
        
        if (numbersList.size() > 0) {
            for (int i = 0; i < numbersList.size(); i++) {
                switch (i) {
                    case 0:
                        num_01 = numbersList.get(0);
                        view_01.setBackgroundResource(R.drawable.passcode_dot_filled);
                        break;
                    case 1:
                        num_02 = numbersList.get(1);
                        view_02.setBackgroundResource(R.drawable.passcode_dot_filled);
                        break;
                    case 2:
                        num_03 = numbersList.get(2);
                        view_03.setBackgroundResource(R.drawable.passcode_dot_filled);
                        break;
                    case 3:
                        num_04 = numbersList.get(3);
                        view_04.setBackgroundResource(R.drawable.passcode_dot_filled);
                        
                        // Construct the passcode
                        passCode = num_01 + num_02 + num_03 + num_04;
                        
                        // Process the complete passcode
                        processCompletePasscode();
                        break;
                }
            }
        }
    }
    
    /**
     * Process the passcode once all 4 digits are entered
     */
    private void processCompletePasscode() {
        // Handle different modes
        if (isNewPasscode) {
            // First-time passcode setup
            handleNewPasscodeSetup();
        } else if (isChangingPasscode) {
            // Changing existing passcode
            handlePasscodeChange();
        } else {
            // Normal passcode verification
            matchPassCode();
        }
    }
    
    /**
     * Handle the process for setting up a new passcode
     */
    private void handleNewPasscodeSetup() {
        if (tempPasscode.isEmpty()) {
            // First entry - store it temporarily
            tempPasscode = passCode;
            Toast.makeText(AppLock.this, "Re-enter passcode to confirm", Toast.LENGTH_SHORT).show();
            isConfirmingNewPasscode = true;
            updateUIForMode();
            resetPasscodeInput();
        } else {
            // Confirm the passcode matches
            if (tempPasscode.equals(passCode)) {
                // Save the new passcode
                savePassCode(passCode);
                Toast.makeText(AppLock.this, "Passcode set successfully", Toast.LENGTH_SHORT).show();
                isNewPasscode = false;
                finish();
            } else {
                // Passcodes don't match
                showErrorAnimation();
                Toast.makeText(AppLock.this, "Passcodes don't match. Try again.", Toast.LENGTH_SHORT).show();
                tempPasscode = "";
                isConfirmingNewPasscode = false;
                updateUIForMode();
                resetPasscodeInput();
            }
        }
    }
    
    /**
     * Handle the process for changing an existing passcode
     */
    private void handlePasscodeChange() {
        if (!isConfirmingNewPasscode) {
            // Verify current passcode
            if (getPassCode().equals(passCode)) {
                isConfirmingNewPasscode = true;
                tempPasscode = "";
                updateUIForMode();
                resetPasscodeInput();
            } else {
                showErrorAnimation();
                Toast.makeText(AppLock.this, "Incorrect passcode", Toast.LENGTH_SHORT).show();
                resetPasscodeInput();
            }
        } else {
            // Process new passcode
            if (tempPasscode.isEmpty()) {
                // Store first entry of new passcode
                tempPasscode = passCode;
                passcodeTitle.setText("Confirm New Passcode");
                passcodeInstruction.setText("Re-enter your new passcode");
                resetPasscodeInput();
            } else {
                // Confirm new passcode
                if (tempPasscode.equals(passCode)) {
                    savePassCode(passCode);
                    Toast.makeText(AppLock.this, "Passcode changed successfully", Toast.LENGTH_SHORT).show();
                    isChangingPasscode = false;
                    isConfirmingNewPasscode = false;
                    tempPasscode = "";
                    finish();
                } else {
                    showErrorAnimation();
                    Toast.makeText(AppLock.this, "Passcodes don't match. Try again.", Toast.LENGTH_SHORT).show();
                    tempPasscode = "";
                    passcodeTitle.setText("Enter New Passcode");
                    passcodeInstruction.setText("Create a new 4-digit passcode");
                    resetPasscodeInput();
                }
            }
        }
    }
    
    /**
     * Reset the passcode input UI
     */
    private void resetPasscodeInput() {
        numbers_list.clear();
        resetDots();
    }
    
    /**
     * Reset all passcode dots to empty state
     */
    private void resetDots() {
        view_01.setBackgroundResource(R.drawable.passcode_dot_empty);
        view_02.setBackgroundResource(R.drawable.passcode_dot_empty);
        view_03.setBackgroundResource(R.drawable.passcode_dot_empty);
        view_04.setBackgroundResource(R.drawable.passcode_dot_empty);
    }
    
    /**
     * Show error animation for incorrect passcode
     */
    private void showErrorAnimation() {
        Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        View dotContainer = findViewById(R.id.passcode_title);
        dotContainer.startAnimation(shakeAnimation);
    }

    private void matchPassCode() {
        if (getPassCode().equals(passCode)) {
            // Successful passcode entry
            Intent returnIntent = new Intent();
            returnIntent.putExtra("isPasscodeOpened", true);
            setResult(RESULT_OK, returnIntent);
            finish();
        } else {
            // Failed passcode entry
            showErrorAnimation();
            Toast.makeText(this, "Incorrect passcode. Please try again.", Toast.LENGTH_SHORT).show();
            // Clear the entered passcode and reset UI
            new Handler(Looper.getMainLooper()).postDelayed(() -> resetPasscodeInput(), 300);
        }
    }

    /**
     * Save passcode to SharedPreferences
     */
    private SharedPreferences.Editor savePassCode(String passCode) {
        SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("passcode", passCode);
        editor.putBoolean(ApplicationConstants.IS_PASSCODE_ENABLED, true);
        editor.apply(); // Using apply() instead of commit() for better performance

        return editor;
    }

    /**
     * Get stored passcode from SharedPreferences
     */
    private String getPassCode() {
        SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        return preferences.getString("passcode", "");
    }


}