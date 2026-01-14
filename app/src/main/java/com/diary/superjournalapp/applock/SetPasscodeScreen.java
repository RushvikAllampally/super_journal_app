package com.diary.superjournalapp.applock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.constants.ApplicationConstants;

public class SetPasscodeScreen extends AppCompatActivity {

    private SwitchCompat enablePasscodeLock;
    private CardView resetPasscodeCard;
    private LinearLayout passcodeOptionsLayout;
    private TextView passcodeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_passcode_screen_improved);

        // Initialize views
        enablePasscodeLock = findViewById(R.id.switch_enable_passcode_lock);
        resetPasscodeCard = findViewById(R.id.change_passcode_card); // We'll reuse the change_passcode_card ID
        passcodeOptionsLayout = findViewById(R.id.passcode_options);
        passcodeStatus = findViewById(R.id.passcode_status);

        // Load current passcode state
        updatePasscodeState();

        // Set up listeners
        enablePasscodeLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // Start the passcode setup flow
                    Intent intent = new Intent(SetPasscodeScreen.this, AppLock.class);
                    intent.putExtra(ApplicationConstants.IS_NEW_PASSCODE, true);
                    startActivity(intent);
                } else {
                    // Show confirmation dialog before disabling
                    showDisablePasscodeDialog();
                }
            }
        });

        // Reset passcode button
        resetPasscodeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetPasscodeConfirmation();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update UI when returning to this screen
        updatePasscodeState();
    }

    /**
     * Update UI based on current passcode state
     */
    private void updatePasscodeState() {
        SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        boolean isPasscodeEnabled = preferences.getBoolean(ApplicationConstants.IS_PASSCODE_ENABLED, false);
        
        // Update switch without triggering listener
        enablePasscodeLock.setOnCheckedChangeListener(null);
        enablePasscodeLock.setChecked(isPasscodeEnabled);
        enablePasscodeLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(SetPasscodeScreen.this, AppLock.class);
                    intent.putExtra(ApplicationConstants.IS_NEW_PASSCODE, true);
                    startActivity(intent);
                } else {
                    showDisablePasscodeDialog();
                }
            }
        });

        // Show/hide additional options
        passcodeOptionsLayout.setVisibility(isPasscodeEnabled ? View.VISIBLE : View.GONE);
        
        // Update status text
        if (isPasscodeEnabled) {
            passcodeStatus.setText("App is password protected");
        } else {
            passcodeStatus.setText("No passcode protection");
        }
    }

    /**
     * Show confirmation dialog before disabling passcode
     */
    private void showDisablePasscodeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Disable Passcode Protection")
                .setMessage("Are you sure you want to disable passcode protection? Anyone with access to your device will be able to open the app.")
                .setPositiveButton("Disable", (dialog, which) -> {
                    // Disable passcode
                    SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(ApplicationConstants.IS_PASSCODE_ENABLED, false);
                    editor.apply();
                    updatePasscodeState();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Keep passcode enabled
                    enablePasscodeLock.setChecked(true);
                })
                .show();
    }

    
    /**
     * Show confirmation dialog before resetting passcode
     */
    private void showResetPasscodeConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Passcode")
                .setMessage("Are you sure you want to reset your passcode? You'll need to create a new one.")
                .setPositiveButton("Reset", (dialog, which) -> {
                    // Clear existing passcode
                    SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("passcode");
                    editor.apply();
                    
                    // Start new passcode setup
                    Intent intent = new Intent(SetPasscodeScreen.this, AppLock.class);
                    intent.putExtra(ApplicationConstants.IS_NEW_PASSCODE, true);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}