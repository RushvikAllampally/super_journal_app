package com.example.superjournalapp.applock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.superjournalapp.R;
import com.example.superjournalapp.constants.ApplicationConstants;

import java.util.ArrayList;

public class AppLock extends AppCompatActivity implements View.OnClickListener {

    private View view_01, view_02, view_03, view_04;
    private Button btn_01, btn_02, btn_03, btn_04, btn_05, btn_06, btn_07, btn_08, btn_09, btn_00, btn_clear;

    private ArrayList<String> numbers_list = new ArrayList<>();

    private String passCode = "";
    private String num_01, num_02, num_03, num_04;

    private TextView forgotPassword;

    private Boolean isNewPasscode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        forgotPassword = findViewById(R.id.forgot_password);

        Intent intent = getIntent();
        isNewPasscode = intent.getBooleanExtra(ApplicationConstants.IS_NEW_PASSCODE, false);

//        if(!isNewPasscode){
//            forgotPassword.setVisibility(View.VISIBLE);
//        }
//
//        forgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        initializeComponents();



    }

    private void initializeComponents() {

        view_01 = findViewById(R.id.view_01);
        view_02 = findViewById(R.id.view_02);
        view_03 = findViewById(R.id.view_03);
        view_04 = findViewById(R.id.view_04);

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

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_01) {
            numbers_list.add("1");
        } else if (view.getId() == R.id.btn_02) {
            numbers_list.add("2");
        } else if (view.getId() == R.id.btn_03) {
            numbers_list.add("3");
        } else if (view.getId() == R.id.btn_04) {
            numbers_list.add("4");
        } else if (view.getId() == R.id.btn_05) {
            numbers_list.add("5");
        } else if (view.getId() == R.id.btn_06) {
            numbers_list.add("6");
        } else if (view.getId() == R.id.btn_07) {
            numbers_list.add("7");
        } else if (view.getId() == R.id.btn_08) {
            numbers_list.add("8");
        } else if (view.getId() == R.id.btn_09) {
            numbers_list.add("9");
        } else if (view.getId() == R.id.btn_0) {
            numbers_list.add("0");
        } else if (view.getId() == R.id.btn_clear) {
            numbers_list.clear();
        }
        passNumber(numbers_list);
    }

    private void passNumber(ArrayList<String> numbersList) {

        if (numbers_list.size() == 0) {
            view_01.setBackgroundResource(R.drawable.grey_circle_24);
            view_02.setBackgroundResource(R.drawable.grey_circle_24);
            view_03.setBackgroundResource(R.drawable.grey_circle_24);
            view_04.setBackgroundResource(R.drawable.grey_circle_24);
        } else {
            switch (numbers_list.size()) {
                case 1:
                    num_01 = numbers_list.get(0);
                    view_01.setBackgroundResource(R.drawable.blue_circle_24);
                    break;
                case 2:
                    num_02 = numbers_list.get(1);
                    view_02.setBackgroundResource(R.drawable.blue_circle_24);
                    break;
                case 3:
                    num_03 = numbers_list.get(2);
                    view_03.setBackgroundResource(R.drawable.blue_circle_24);
                    break;
                case 4:
                    num_04 = numbers_list.get(3);
                    view_04.setBackgroundResource(R.drawable.blue_circle_24);
                    passCode = num_01 + num_02 + num_03 + num_04;

                    if (getPassCode().length() == 0 || isNewPasscode) {
                        savePassCode(passCode);
                        isNewPasscode = false;
                        Toast.makeText(AppLock.this, "Re-Enter New Passcode", Toast.LENGTH_LONG).show();
                        numbers_list.clear();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                passNumber(numbers_list);
                            }
                        }, 1000);

                    } else {
                        matchPassCode();
                    }
                    break;
            }
        }

    }

    private void matchPassCode() {
        if (getPassCode().equals(passCode)) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("isPasscodeOpened", true);
            setResult(RESULT_OK, returnIntent);
            finish();
            finish();
        } else {
            Toast.makeText(this, "PassCode doesn't match please retry again !", Toast.LENGTH_LONG).show();
        }
    }

    private SharedPreferences.Editor savePassCode(String passCode) {
        SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("passcode", passCode);
        editor.putBoolean(ApplicationConstants.IS_PASSCODE_ENABLED, true);

        editor.commit();

        return editor;
    }

    private String getPassCode() {
        SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        return preferences.getString("passcode", "");
    }


}