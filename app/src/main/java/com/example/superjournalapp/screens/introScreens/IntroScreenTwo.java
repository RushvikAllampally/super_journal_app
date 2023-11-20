package com.example.superjournalapp.screens.introScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.superjournalapp.R;

public class IntroScreenTwo extends AppCompatActivity {

    Button nxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen_two);

        nxt = findViewById(R.id.intro_screen_two_nxt);

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroScreenTwo.this, JournalTypesScreenIntro.class);
                startActivity(intent);
            }
        });
    }
}