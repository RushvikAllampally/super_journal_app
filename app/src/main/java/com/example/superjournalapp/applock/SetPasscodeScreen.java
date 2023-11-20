package com.example.superjournalapp.applock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.superjournalapp.R;
import com.example.superjournalapp.constants.ApplicationConstants;

public class SetPasscodeScreen extends AppCompatActivity {

    private SwitchCompat enablePasscodeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_passcode_screen);

        enablePasscodeLock = findViewById(R.id.switch_enable_passcode_lock);

        SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        enablePasscodeLock.setChecked(preferences.getBoolean(ApplicationConstants.IS_PASSCODE_ENABLED, false));

        enablePasscodeLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {

                    Intent intent = new Intent(SetPasscodeScreen.this, AppLock.class);
                    intent.putExtra(ApplicationConstants.IS_NEW_PASSCODE, true);
                    startActivity(intent);

                } else {

                    SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(ApplicationConstants.IS_PASSCODE_ENABLED, false);
                    editor.commit();

                }
            }
        });

    }

}