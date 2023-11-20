package com.example.superjournalapp.screens.introScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.superjournalapp.R;

public class JournalTypesScreenIntro extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_types_screen);

        button = findViewById(R.id.nxt_btn_intro_scrn_journaltypes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JournalTypesScreenIntro.this, JournalTypesIntroTwo.class);
                startActivity(intent);
            }
        });
    }
}