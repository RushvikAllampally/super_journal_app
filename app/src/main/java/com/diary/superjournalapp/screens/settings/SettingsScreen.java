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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.applock.SetPasscodeScreen;
import com.diary.superjournalapp.base.ThemedActivity;
import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.constants.ThemeConstants;
import com.diary.superjournalapp.screens.fragments.HomeFragment;
import com.diary.superjournalapp.utils.FontSizeUtils;
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
    private LinearLayout dailyPromptsBlock;
    private LinearLayout fontSizeBlock;
    private Button editProfileBtn;
    private EditText editNameInput;
    private TextView displayProfileName;
    private TextView currentFontSizeText;
    private SwitchCompat darkModeSwitch;
    private SwitchCompat promptsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        reminderBlock = findViewById(R.id.reminder_block);
        passocodeBlock = findViewById(R.id.passcode_block);
//        exportDatalock = findViewById(R.id.export_data_block);
        inviteAFriendBlock = findViewById(R.id.invite_a_block);
        shareFeedBackBlock = findViewById(R.id.feed_back_block);
        rateOurAppBlock = findViewById(R.id.rate_app_block);
        darkModeBlock = findViewById(R.id.dark_mode_block);
        dailyPromptsBlock = findViewById(R.id.daily_prompts_block);
//        fontSizeBlock = findViewById(R.id.font_size_block);

        displayProfileName = findViewById(R.id.display_profile_name);
        editProfileBtn = findViewById(R.id.edit_profile_btn);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        promptsSwitch = findViewById(R.id.prompts_switch);
//        currentFontSizeText = findViewById(R.id.current_font_size_text);
        
        // Initialize dark mode switch based on current theme
        setupDarkModeToggle();
        
        // Initialize prompts switch
        setupPromptsToggle();
        
        // Initialize font size settings
//        setupFontSizeSettings();

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
//        exportDatalock.setOnClickListener(comingSoononClickListener);
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
    
    /**
     * Sets up the prompts toggle switch and its listener
     */
    private void setupPromptsToggle() {
        // Set the initial switch state based on saved preference
        boolean promptsEnabled = com.diary.superjournalapp.utils.PromptUtils.arePromptsEnabled(this);
        promptsSwitch.setChecked(promptsEnabled);
        
        // Set click listener for the entire prompts block
        dailyPromptsBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle switch state
                promptsSwitch.setChecked(!promptsSwitch.isChecked());
                // Save the new setting
                com.diary.superjournalapp.utils.PromptUtils.setPromptsEnabled(
                    SettingsScreen.this, promptsSwitch.isChecked());
            }
        });
        
        // Set change listener for the switch itself
        promptsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the new setting
                com.diary.superjournalapp.utils.PromptUtils.setPromptsEnabled(
                    SettingsScreen.this, promptsSwitch.isChecked());
            }
        });
    }
    
    /**
     * Sets up the font size settings
     */
    private void setupFontSizeSettings() {
        // Display current font size preference
        updateFontSizeText();
        
        // Setup click listener for font size block
        fontSizeBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFontSizeDialog();
            }
        });
    }
    
    /**
     * Updates the font size text based on the current preference
     */
    private void updateFontSizeText() {
        String currentPreference = FontSizeUtils.getFontSizePreference(this);
        String displayText;
        
        switch (currentPreference) {
            case FontSizeUtils.FONT_SIZE_SMALL:
                displayText = "Small";
                break;
            case FontSizeUtils.FONT_SIZE_LARGE:
                displayText = "Large";
                break;
            case FontSizeUtils.FONT_SIZE_EXTRA_LARGE:
                displayText = "Extra Large";
                break;
            case FontSizeUtils.FONT_SIZE_MEDIUM:
            default:
                displayText = "Medium";
                break;
        }
        
        currentFontSizeText.setText(displayText);
    }
    
    /**
     * Shows the font size selection dialog
     */
    private void showFontSizeDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.font_size_dialog);
        
        RadioButton smallButton = dialog.findViewById(R.id.font_size_small);
        RadioButton mediumButton = dialog.findViewById(R.id.font_size_medium);
        RadioButton largeButton = dialog.findViewById(R.id.font_size_large);
        RadioButton extraLargeButton = dialog.findViewById(R.id.font_size_extra_large);
        TextView previewText = dialog.findViewById(R.id.font_size_preview);
        Button cancelButton = dialog.findViewById(R.id.font_size_cancel_button);
        Button applyButton = dialog.findViewById(R.id.font_size_apply_button);
        
        // Set initial selection based on current preference
        String currentPreference = FontSizeUtils.getFontSizePreference(this);
        switch (currentPreference) {
            case FontSizeUtils.FONT_SIZE_SMALL:
                smallButton.setChecked(true);
                previewText.setTextSize(15);
                break;
            case FontSizeUtils.FONT_SIZE_LARGE:
                largeButton.setChecked(true);
                previewText.setTextSize(22);
                break;
            case FontSizeUtils.FONT_SIZE_EXTRA_LARGE:
                extraLargeButton.setChecked(true);
                previewText.setTextSize(26);
                break;
            case FontSizeUtils.FONT_SIZE_MEDIUM:
            default:
                mediumButton.setChecked(true);
                previewText.setTextSize(18);
                break;
        }
        
        // Setup radio button listeners to update preview
        smallButton.setOnClickListener(v -> previewText.setTextSize(15));
        mediumButton.setOnClickListener(v -> previewText.setTextSize(18));
        largeButton.setOnClickListener(v -> previewText.setTextSize(22));
        extraLargeButton.setOnClickListener(v -> previewText.setTextSize(26));
        
        // Setup button listeners
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        applyButton.setOnClickListener(v -> {
            String newFontSize = FontSizeUtils.FONT_SIZE_MEDIUM; // default
            
            if (smallButton.isChecked()) {
                newFontSize = FontSizeUtils.FONT_SIZE_SMALL;
            } else if (largeButton.isChecked()) {
                newFontSize = FontSizeUtils.FONT_SIZE_LARGE;
            } else if (extraLargeButton.isChecked()) {
                newFontSize = FontSizeUtils.FONT_SIZE_EXTRA_LARGE;
            } else if (mediumButton.isChecked()) {
                newFontSize = FontSizeUtils.FONT_SIZE_MEDIUM;
            }
            
            // Save the preference
            FontSizeUtils.saveFontSizePreference(SettingsScreen.this, newFontSize);
            
            // Update the displayed font size text
            updateFontSizeText();
            
            // Close the dialog
            dialog.dismiss();
            
            // Show a toast to notify the user
            Toast.makeText(SettingsScreen.this, "Font size updated", Toast.LENGTH_SHORT).show();
        });
        
        dialog.show();
    }
}