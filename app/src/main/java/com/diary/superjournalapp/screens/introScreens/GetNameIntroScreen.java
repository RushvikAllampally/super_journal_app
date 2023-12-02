package com.diary.superjournalapp.screens.introScreens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.diary.superjournalapp.MainActivity;
import com.diary.superjournalapp.R;
import com.diary.superjournalapp.constants.ApplicationConstants;

public class GetNameIntroScreen extends AppCompatActivity {

    private Button button;
    private EditText appUserNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_name_intro_screen);

        button = findViewById(R.id.final_intro_next_btn);
        appUserNameText=findViewById(R.id.app_user_name_input);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String appUserName = appUserNameText.getText().toString();

                SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);

                // Get the SharedPreferences editor
                SharedPreferences.Editor editor = preferences.edit();

                // Set the value of FIRST_TIME_USER to "yes"
                editor.putString(ApplicationConstants.FIRST_TIME_USER, "no");
                editor.putString(ApplicationConstants.APP_USER_NAME, appUserName);

                // Apply the changes
                editor.apply();

                // Create an intent to navigate to the home screen (fragment)
                Intent intent = new Intent(GetNameIntroScreen.this, MainActivity.class);

                // Set flags to clear the back stack and start the home screen as a new task
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Start the home activity
                startActivity(intent);

                // Finish the current activity (last intro screen)
                finish();
            }
        });

    }
}