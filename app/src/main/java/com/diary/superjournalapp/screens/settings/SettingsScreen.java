package com.diary.superjournalapp.screens.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.applock.SetPasscodeScreen;
import com.diary.superjournalapp.base.ThemedActivity;
import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.constants.ThemeConstants;
import com.diary.superjournalapp.screens.fragments.HomeFragment;
import com.diary.superjournalapp.utils.ThemeUtils;
import com.diary.superjournalapp.utils.comingSoonActivity;

public class SettingsScreen extends ThemedActivity {

    private LinearLayout reminderBlock;
    private LinearLayout passocodeBlock;
    private LinearLayout exportDatalock;
    private LinearLayout inviteAFriendBlock;
    private LinearLayout shareFeedBackBlock;
    private LinearLayout rateOurAppBlock;
    private LinearLayout darkModeBlock;
    private Button editProfileBtn;
    private EditText editNameInput;
    private TextView displayProfileName;
    private SwitchCompat darkModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        reminderBlock = findViewById(R.id.reminder_block);
        passocodeBlock = findViewById(R.id.passcode_block);
        exportDatalock = findViewById(R.id.export_data_block);
        inviteAFriendBlock = findViewById(R.id.invite_a_block);
        shareFeedBackBlock = findViewById(R.id.feed_back_block);
        rateOurAppBlock = findViewById(R.id.rate_app_block);
        darkModeBlock = findViewById(R.id.dark_mode_block);

        displayProfileName = findViewById(R.id.display_profile_name);
        editProfileBtn = findViewById(R.id.edit_profile_btn);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        
        // Initialize dark mode switch based on current theme
        setupDarkModeToggle();

        SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        String appUserName = preferences.getString(ApplicationConstants.APP_USER_NAME, "");

        displayProfileName.setText(appUserName);

        reminderBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsScreen.this, ReminderScreen.class);
                startActivity(intent);
            }
        });

        View.OnClickListener comingSoononClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsScreen.this, comingSoonActivity.class);
                startActivity(intent);
            }
        };

        passocodeBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsScreen.this, SetPasscodeScreen.class);
                startActivity(intent);
            }
        });
        exportDatalock.setOnClickListener(comingSoononClickListener);
        inviteAFriendBlock.setOnClickListener(comingSoononClickListener);
        rateOurAppBlock.setOnClickListener(comingSoononClickListener);

        shareFeedBackBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                String UriText = "mailto:" + Uri.encode(ApplicationConstants.CONTACT_EMAIL) + "?subject=" +
                        Uri.encode("Feedback of the App");

                Uri uri = Uri.parse(UriText);
                intent.setData(uri);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });


        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(SettingsScreen.this);
                dialog.setContentView(R.layout.edit_profile_details);

                editNameInput = dialog.findViewById(R.id.profile_name);

                SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
                String appUserName = preferences.getString(ApplicationConstants.APP_USER_NAME, "");

                editNameInput.setText(appUserName);

                dialog.show();

                TextView saveBtn = dialog.findViewById(R.id.save_profile_name);
                TextView cancelBtn = dialog.findViewById(R.id.cancel_profile_name);

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String profileName = editNameInput.getText().toString();

                        // Get the SharedPreferences editor
                        SharedPreferences.Editor editor = preferences.edit();
                        // Set the value of FIRST_TIME_USER to "yes"
                        editor.putString(ApplicationConstants.APP_USER_NAME, profileName);
                        // Apply the changes
                        editor.apply();

                        displayProfileName.setText(profileName);
                        HomeFragment.updateUserNameInHome(profileName);
                        dialog.dismiss();

                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });

    }
    
    /**
     * Sets up the dark mode toggle switch and its listener
     */
    private void setupDarkModeToggle() {
        // Set the initial switch state based on the current theme mode
        int currentThemeMode = ThemeUtils.getThemeMode(this);
        darkModeSwitch.setChecked(currentThemeMode == ThemeUtils.MODE_DARK);
        
        // Set click listener for the entire dark mode block
        darkModeBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle switch state
                darkModeSwitch.setChecked(!darkModeSwitch.isChecked());
                // Apply theme change based on new switch state
                applyThemeChange(darkModeSwitch.isChecked());
            }
        });
        
        // Set change listener for the switch itself
        darkModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Apply theme change based on switch state
                applyThemeChange(darkModeSwitch.isChecked());
            }
        });
    }
    
    /**
     * Apply theme change based on the switch state
     * @param isDarkMode true for dark mode, false for light mode
     */
    private void applyThemeChange(boolean isDarkMode) {
        // Set appropriate theme mode
        int themeMode = isDarkMode ? ThemeUtils.MODE_DARK : ThemeUtils.MODE_LIGHT;
        ThemeUtils.setThemeMode(this, themeMode);
        
        // Recreate the activity to apply theme changes immediately
        recreate();
    }
}