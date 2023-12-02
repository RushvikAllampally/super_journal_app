package com.diary.superjournalapp.screens.introScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.diary.superjournalapp.R;

public class JournalTypesIntroTwo extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_types_intro_two);

        button=  findViewById(R.id.nxt_btn_intro_scrn_journaltypes_two);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JournalTypesIntroTwo.this, GetNameIntroScreen.class);
                startActivity(intent);
            }
        });
    }
}