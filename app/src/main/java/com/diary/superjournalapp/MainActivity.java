package com.diary.superjournalapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.diary.superjournalapp.applock.AppLock;
import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.screens.fragments.CalenderViewFragment;
import com.diary.superjournalapp.screens.fragments.HomeFragment;
import com.diary.superjournalapp.screens.fragments.JournalListFragment;
import com.diary.superjournalapp.screens.fragments.JournalsDataFragment;
import com.diary.superjournalapp.screens.introScreens.WelcomeScreen;
import com.diary.superjournalapp.screens.journals.BulletJournal;
import com.diary.superjournalapp.screens.journals.DreamJournal;
import com.diary.superjournalapp.screens.journals.GratitudeJournal;
import com.diary.superjournalapp.screens.journals.ReflectiveJournal;
import com.diary.superjournalapp.utils.JournalUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_POST_NOTIFICATIONS = 1;
    private BottomNavigationView bottomNavigationItemView;
    private PopupWindow popupWindow;
    private Boolean isPasscodeOpened = false;
    private ActivityResultLauncher<Intent> passCodeScreenLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        isPasscodeOpened = data.getBooleanExtra("isPasscodeOpened", false);
                    }
                }
            }
    );

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            // Check if the permission has been granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with your logic
            } else {
                // Permission denied, handle accordingly or inform the user
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        Boolean isPassCodeEnabled = preferences.getBoolean(ApplicationConstants.IS_PASSCODE_ENABLED, false);

        String isFirstTimeUser = preferences.getString(ApplicationConstants.FIRST_TIME_USER, "yes");

        if (isFirstTimeUser.isEmpty() || isFirstTimeUser.equals("yes")) {
            Intent intent = new Intent(MainActivity.this, WelcomeScreen.class);
            startActivity(intent);
        } else {
            if (!isPasscodeOpened && isPassCodeEnabled) {
                Intent intent = new Intent(this, AppLock.class);
                passCodeScreenLauncher.launch(intent);
            }
            loadFrag(new HomeFragment(), true);
        }

        System.out.println("build versoin : "+Build.VERSION.SDK_INT);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
//            }
//        }

        JournalUtils.askForNotificationPermission(MainActivity.this,MainActivity.this);

        bottomNavigationItemView = findViewById(R.id.bottom_navigation);

        //here we are disabling the selection of middle bottom as its not needed
        Menu menu = bottomNavigationItemView.getMenu();
        MenuItem unSelectedItem = menu.findItem(R.id.nav_add_journal);

        if (unSelectedItem != null) {
            unSelectedItem.setEnabled(false);
        }

        // Initialize the popup window
        View popupView = LayoutInflater.from(this).inflate(R.layout.journal_list_popup, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        // Find your ListView
        ListView journalsListView = popupView.findViewById(R.id.journal_listView);

        // Set up your ListView and its adapter here
        String[] journalListViewData = {ApplicationConstants.REFLECTIVE_JOURNAL,ApplicationConstants.GRATITUDE_JOURNAL, ApplicationConstants.BULLET_JOURNAL, ApplicationConstants.DREAM_JOURNAL};

        // Create an ArrayAdapter to populate the ListView with data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.textView, journalListViewData);

        // Set the adapter for the ListView
        journalsListView.setAdapter(adapter);

        // Show the popup when needed, e.g., on a button click
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAtLocation(findViewById(R.id.main_frame_layout), Gravity.CENTER, 0, 0);
            }
        });

        // Dismiss the popup when clicking outside of it
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        journalsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click here
                String selectedItem = ((TextView) view.findViewById(R.id.textView)).getText().toString();

                Intent intent;
                switch (selectedItem) {
                    case "Gratitude Journal":
                        intent = new Intent(MainActivity.this, GratitudeJournal.class);
                        startActivity(intent);
                        break;
                    case "My Diary":
                        intent = new Intent(MainActivity.this, ReflectiveJournal.class);
                        startActivity(intent);
                        break;
                    case "Bullet Journal":
                        intent = new Intent(MainActivity.this, BulletJournal.class);
                        startActivity(intent);
                        break;
                    case "Dream Journal":
                        intent = new Intent(MainActivity.this, DreamJournal.class);
                        startActivity(intent);
                        break;
                }
                Toast.makeText(MainActivity.this, "Opening " + selectedItem, Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });

        bottomNavigationItemView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int selectedId = item.getItemId();

                if (selectedId == R.id.nav_home) {
                    loadFrag(new HomeFragment(), false);
                    return true;
                } else if (selectedId == R.id.nav_calender) {
                    loadFrag(new CalenderViewFragment(), false);
                    return true;

                } else if (selectedId == R.id.nav_manage) {
                    loadFrag(new JournalsDataFragment(), false);
                    return true;

                } else if (selectedId == R.id.nav_list) {
                    loadFrag(new JournalListFragment(), false);
                    return true;

                }
                return false;
            }
        });
    }

    public void loadFrag(Fragment fragment, boolean fragVal) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        int count = fm.getBackStackEntryCount();

        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_frame_layout);

                // If the stack decreases it means I clicked the back button
                if (fm.getBackStackEntryCount() <= count && bottomNavigationItemView != null) {
//                    //check your position based on selected fragment and set it accordingly.
                    if (currentFragment instanceof HomeFragment) {
                        bottomNavigationItemView.getMenu().getItem(0).setChecked(true);
                    } else if (currentFragment instanceof CalenderViewFragment) {
                        bottomNavigationItemView.getMenu().getItem(1).setChecked(true);
                    } else if (currentFragment instanceof JournalsDataFragment) {
                        bottomNavigationItemView.getMenu().getItem(3).setChecked(true);
                    } else if (currentFragment instanceof JournalListFragment) {
                        bottomNavigationItemView.getMenu().getItem(4).setChecked(true);
                    }
                }

            }
        });

        if (fragVal) {
            ft.add(R.id.main_frame_layout, fragment);
            fm.popBackStack("root_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.addToBackStack("root_fragment");
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_frame_layout);
            if (currentFragment.getClass() != fragment.getClass()) {
                ft.replace(R.id.main_frame_layout, fragment);
                ft.addToBackStack(null);
            }
        }
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_frame_layout);

        if (currentFragment instanceof HomeFragment) {
            // Handle back press for the home fragment
            finish();
        } else {
            super.onBackPressed();
        }
    }
}