package com.example.superjournalapp.screens.settings;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.superjournalapp.R;
import com.example.superjournalapp.applock.SetPasscodeScreen;
import com.example.superjournalapp.constants.ApplicationConstants;
import com.example.superjournalapp.screens.fragments.HomeFragment;
import com.example.superjournalapp.utils.comingSoonActivity;

public class SettingsScreen extends AppCompatActivity {

    private LinearLayout reminderBlock;
    private LinearLayout passocodeBlock;
    private LinearLayout exportDatalock;
    private LinearLayout inviteAFriendBlock;
    private LinearLayout shareFeedBackBlock;
    private LinearLayout rateOurAppBlock;
    private Button editProfileBtn;
    private EditText editNameInput;
    private TextView displayProfileName;

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

        displayProfileName = findViewById(R.id.display_profile_name);
        editProfileBtn = findViewById(R.id.edit_profile_btn);

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
                String UriText = "mailto:" + Uri.encode("rushvik249@gmail.com") + "?subject=" +
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
}