package com.diary.superjournalapp.screens.fragments;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.dto.BulletEntryDetails;
import com.diary.superjournalapp.dto.QuoteDto;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.JournalCategories.BulletJournalEntity;
import com.diary.superjournalapp.entity.MoodTracker;
import com.diary.superjournalapp.recyclerviews.JournalRecyclerAdaptor;
import com.diary.superjournalapp.screens.journals.BulletJournal;
import com.diary.superjournalapp.screens.settings.SettingsScreen;
import com.diary.superjournalapp.utils.JournalUtils;
import com.diary.superjournalapp.utils.QuoteManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static JournalRecyclerAdaptor journalRecyclerAdaptor;
    private static ImageView nothingFoundImage;
    private static TextView nothingFoundText;
    private static RecyclerView recyclerView;
    private static RecyclerView tasksRecyclerView;
    private static CardView tasksCard;
    private static DatabaseHelper databaseHelper;
    private static BulletJournalEntity bulletJournalEntity;
    // TODO: Rename and change types of parameters
    private static Context context;
    private static TextView streakCount;
    private static List<BulletJournalEntity> bulletJournalEntities;
    private static LinearLayout linearLayoutContainer;
    private static TextView hiQuoteName;
    private static long homeDisplayBulletJournalId;
    private TextView quote;
    private TextView quoteAuthor;
    private TextView affirmation;
    private ImageButton rateYourDayBtn;
    private ImageButton settingsBtn;
    private ImageView editBulletBtn;
    private ImageView pinImage;
    private int moodLevel;
    private BottomNavigationView bottomNavigationView;
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void updateUserNameInHome(String newUserName) {
        if (context != null) {
            hiQuoteName.setText("Hi, " + newUserName);
        }
    }

    public static void notifyHomeRecyclerViewChanges() {
        ArrayList<Journal> journalsList;
        journalsList = (ArrayList<Journal>) databaseHelper.journalDao().getAllJournal();

        if (journalsList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            nothingFoundImage.setVisibility(View.VISIBLE);
            nothingFoundText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            nothingFoundImage.setVisibility(View.GONE);
            nothingFoundText.setVisibility(View.GONE);
        }
        int index = journalsList.size() > 5 ? 5 : journalsList.size();

        journalRecyclerAdaptor = new JournalRecyclerAdaptor(context, new ArrayList<>(journalsList.subList(0, index)));

        recyclerView.setAdapter(journalRecyclerAdaptor);
        journalRecyclerAdaptor.notifyDataSetChanged();
    }

    /**
     * Update the display of bullet journals on the home screen
     * This method handles displaying either the most recent or pinned bullet journal
     */
    public static void notifyHomeBulletChanges() {
        if (context == null || linearLayoutContainer == null || tasksCard == null) {
            // Safety check - can't update UI elements if they're not initialized
            return;
        }
        
        // Get all pinned bullet journals and the most recent one
        bulletJournalEntities = databaseHelper.bulletJournalContentDao().getPinnedBulletJournals(true);
        bulletJournalEntity = databaseHelper.bulletJournalContentDao().getMostRecentBulletJournal();

        Gson gson = new Gson();
        Type listType = new TypeToken<List<BulletEntryDetails>>() {}.getType();
        List<BulletEntryDetails> bulletEntryDetails = new ArrayList<>();
        boolean showTasks = false;
        
        // Decide which journal to display (if any)
        if (bulletJournalEntities.size() > 0) {
            // We have pinned journals - show the most recently created/updated one
            BulletJournalEntity pinnedJournal = bulletJournalEntities.get(0); // Most recent pinned journal
            
            // Verify it's not marked as hidden
            if (!pinnedJournal.isDontShow) {
                bulletEntryDetails = gson.fromJson(pinnedJournal.taskListJson, listType);
                homeDisplayBulletJournalId = pinnedJournal.getJournalId();
                showTasks = true;
            }
        } else if (bulletJournalEntity != null && !bulletJournalEntity.isDontShow) {
            // No pinned journals, but we have a recent journal that's not hidden
            bulletEntryDetails = gson.fromJson(bulletJournalEntity.taskListJson, listType);
            homeDisplayBulletJournalId = bulletJournalEntity.getJournalId();
            showTasks = true;
        }
        
        // Update UI based on whether we should show tasks
        if (showTasks && bulletEntryDetails != null && !bulletEntryDetails.isEmpty()) {
            tasksCard.setVisibility(View.VISIBLE);
            displayTasksList(bulletEntryDetails);
        } else {
            tasksCard.setVisibility(View.GONE);
            linearLayoutContainer.removeAllViews();
        }
    }
    
    /**
     * Helper method to display tasks in the LinearLayout
     */
    private static void displayTasksList(List<BulletEntryDetails> bulletEntryDetails) {
        linearLayoutContainer.removeAllViews();

        for (int i = 0; i < bulletEntryDetails.size(); i++) {
            TextView taskTxtView = new TextView(context);

            taskTxtView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            taskTxtView.setText("• " + bulletEntryDetails.get(i).getTaskName());
            if (bulletEntryDetails.get(i).getIsTaskDone()) {
                taskTxtView.setPaintFlags(taskTxtView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                taskTxtView.setPaintFlags(taskTxtView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
            linearLayoutContainer.addView(taskTxtView);
        }
    }

    public static void updateStreakCount(int count) {
        streakCount.setText(String.valueOf(count));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        context = view.getContext();

        recyclerView = view.findViewById(R.id.home_recycler_view);
        rateYourDayBtn = view.findViewById(R.id.rate_mood_icon);
        settingsBtn = view.findViewById(R.id.profile_btn);
        editBulletBtn = view.findViewById(R.id.edit_bullet_journal);
        pinImage = view.findViewById(R.id.home_pin_icon);
        tasksCard = view.findViewById(R.id.tasks_card);
        hiQuoteName = view.findViewById(R.id.hi_quote_name);
        quote = view.findViewById(R.id.quote);
        quoteAuthor = view.findViewById(R.id.quote_author);
        affirmation = view.findViewById(R.id.affirmation);
        nothingFoundImage = view.findViewById(R.id.nothing_found_home_view);
        nothingFoundText = view.findViewById(R.id.nothing_found_text);
        streakCount = view.findViewById(R.id.streak_edit_txt);
        linearLayoutContainer = view.findViewById(R.id.linear_layout_container);

        NestedScrollView nestedScrollView = view.findViewById(R.id.next_scroll_view);
        nestedScrollView.scrollTo(0, 0);

        int streak = JournalUtils.updateStreakOnLoad(view.getContext());
        streakCount.setText(String.valueOf(streak));

        // Use QuoteManager for smart quote/affirmation tracking
        QuoteManager quoteManager = new QuoteManager(view.getContext());
        QuoteDto dailyQuote = quoteManager.getQuoteOfTheDay();
        quote.setText(dailyQuote.getQuote());
        quoteAuthor.setText(dailyQuote.getAuthor());
        affirmation.setText(quoteManager.getAffirmationOfTheDay());

        SharedPreferences preferences = getActivity().getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        String appUserName = preferences.getString(ApplicationConstants.APP_USER_NAME, "Dude");
        hiQuoteName.setText("Hi, " + appUserName);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        databaseHelper = DatabaseHelper.getDb(view.getContext());

        notifyHomeRecyclerViewChanges();
        notifyHomeBulletChanges();

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SettingsScreen.class);
                startActivity(intent);
            }
        });

        // Hide the pin icon on home page - unpinning should only be done from the journal screen
        pinImage.setVisibility(View.GONE);

        editBulletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), BulletJournal.class);
                intent.putExtra("journalId", String.valueOf(homeDisplayBulletJournalId));
                view.getContext().startActivity(intent);
            }
        });

        rateYourDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.activity_rate_your_day);
                dialog.show();
                
                // Set dialog width to 90% of screen width for better spacing
                if (dialog.getWindow() != null) {
                    int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
                    dialog.getWindow().setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                }

                TextView moodLevelMessage = dialog.findViewById(R.id.mood_level_msg);
                TextView moodLevelText = dialog.findViewById(R.id.mood_level_text);
                TextView moodLevelName = dialog.findViewById(R.id.mood_level_name);
                TextView moodOne = dialog.findViewById(R.id.mood_one);
                TextView moodTwo = dialog.findViewById(R.id.mood_two);
                TextView moodThree = dialog.findViewById(R.id.mood_three);
                TextView moodFour = dialog.findViewById(R.id.mood_four);
                TextView moodFive = dialog.findViewById(R.id.mood_five);
                Button saveMoodBtn = dialog.findViewById(R.id.save_mood);
//                EditText reasonForMoodTxt = dialog.findViewById(R.id.reason_for_mood);

                moodLevel = 0;
                
                // Check if today's mood already exists and pre-select it
                SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
                String formattedDate = dateFormat.format(new Date());
                MoodTracker existingMood = databaseHelper.moodTrackerDao().findMoodEntryByMoodDate(formattedDate);
                
                if (existingMood != null && existingMood.getMoodLevel() > 0) {
                    moodLevel = existingMood.getMoodLevel();
                    moodLevelText.setVisibility(View.VISIBLE);
                    moodLevelMessage.setVisibility(View.VISIBLE);
                    moodLevelName.setVisibility(View.VISIBLE);
                    
                    // Set the appropriate message and name based on existing mood
                    switch (moodLevel) {
                        case 1:
                            moodLevelName.setText("Awful");
                            moodLevelMessage.setText(ApplicationConstants.VERY_SAD_MOOD_MSGS.get(JournalUtils.getRandomNumber()));
                            break;
                        case 2:
                            moodLevelName.setText("Sad");
                            moodLevelMessage.setText(ApplicationConstants.SAD_MOOD_MSGS.get(JournalUtils.getRandomNumber()));
                            break;
                        case 3:
                            moodLevelName.setText("Good");
                            moodLevelMessage.setText(ApplicationConstants.NEUTRAL_MOOD_MSGS.get(JournalUtils.getRandomNumber()));
                            break;
                        case 4:
                            moodLevelName.setText("Happy");
                            moodLevelMessage.setText(ApplicationConstants.HAPPY_MOOD_MSGS.get(JournalUtils.getRandomNumber()));
                            break;
                        case 5:
                            moodLevelName.setText("Excited");
                            moodLevelMessage.setText(ApplicationConstants.VERY_HAPPY_MOOD_MSGS.get(JournalUtils.getRandomNumber()));
                            break;
                    }
                }

                moodOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(moodOne, "alpha", 1f, 0.5f);
                        alphaAnimator.setDuration(200); // Set the duration of the animation
                        alphaAnimator.setRepeatCount(1); // Optionally, you can repeat the animation
                        alphaAnimator.setRepeatMode(ObjectAnimator.REVERSE); // Reverse the animation

                        // Start the animation
                        alphaAnimator.start();

                        moodLevelText.setVisibility(View.VISIBLE);
                        moodLevelMessage.setVisibility(View.VISIBLE);
                        moodLevelName.setVisibility(View.VISIBLE);

                        moodLevelName.setText("Awful");
                        moodLevelMessage.setText(ApplicationConstants.VERY_SAD_MOOD_MSGS.get(JournalUtils.getRandomNumber()));

                        moodLevel = 1;

                    }
                });

                moodTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(moodTwo, "alpha", 1f, 0.5f);
                        alphaAnimator.setDuration(200); // Set the duration of the animation
                        alphaAnimator.setRepeatCount(1); // Optionally, you can repeat the animation
                        alphaAnimator.setRepeatMode(ObjectAnimator.REVERSE); // Reverse the animation

                        // Start the animation
                        alphaAnimator.start();

                        moodLevelText.setVisibility(View.VISIBLE);
                        moodLevelMessage.setVisibility(View.VISIBLE);
                        moodLevelName.setVisibility(View.VISIBLE);

                        moodLevelName.setText("Sad");
                        moodLevelMessage.setText(ApplicationConstants.SAD_MOOD_MSGS.get(JournalUtils.getRandomNumber()));

                        moodLevel = 2;
                    }
                });

                moodThree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(moodThree, "alpha", 1f, 0.5f);
                        alphaAnimator.setDuration(200); // Set the duration of the animation
                        alphaAnimator.setRepeatCount(1); // Optionally, you can repeat the animation
                        alphaAnimator.setRepeatMode(ObjectAnimator.REVERSE); // Reverse the animation

                        // Start the animation
                        alphaAnimator.start();

                        moodLevelText.setVisibility(View.VISIBLE);
                        moodLevelMessage.setVisibility(View.VISIBLE);
                        moodLevelName.setVisibility(View.VISIBLE);

                        moodLevelName.setText("Good");
                        moodLevelMessage.setText(ApplicationConstants.NEUTRAL_MOOD_MSGS.get(JournalUtils.getRandomNumber()));

                        moodLevel = 3;
                    }
                });

                moodFour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(moodFour, "alpha", 1f, 0.5f);
                        alphaAnimator.setDuration(200); // Set the duration of the animation
                        alphaAnimator.setRepeatCount(1); // Optionally, you can repeat the animation
                        alphaAnimator.setRepeatMode(ObjectAnimator.REVERSE); // Reverse the animation

                        // Start the animation
                        alphaAnimator.start();

                        moodLevelText.setVisibility(View.VISIBLE);
                        moodLevelMessage.setVisibility(View.VISIBLE);
                        moodLevelName.setVisibility(View.VISIBLE);

                        moodLevelName.setText("Happy");
                        moodLevelMessage.setText(ApplicationConstants.HAPPY_MOOD_MSGS.get(JournalUtils.getRandomNumber()));

                        moodLevel = 4;
                    }
                });

                moodFive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(moodFive, "alpha", 1f, 0.5f);
                        alphaAnimator.setDuration(200); // Set the duration of the animation
                        alphaAnimator.setRepeatCount(1); // Optionally, you can repeat the animation
                        alphaAnimator.setRepeatMode(ObjectAnimator.REVERSE); // Reverse the animation

                        // Start the animation
                        alphaAnimator.start();

                        moodLevelText.setVisibility(View.VISIBLE);
                        moodLevelMessage.setVisibility(View.VISIBLE);
                        moodLevelName.setVisibility(View.VISIBLE);

                        moodLevelName.setText("Excited");
                        moodLevelMessage.setText(ApplicationConstants.VERY_HAPPY_MOOD_MSGS.get(JournalUtils.getRandomNumber()));

                        moodLevel = 5;

                    }
                });

                saveMoodBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (moodLevel == 0) {
                            Toast.makeText(getContext(), "Select a mood before saving", Toast.LENGTH_LONG).show();
                            return;
                        }
//                        String reasonForMood = reasonForMoodTxt.getText().toString();
                        saveMood(moodLevel, "", databaseHelper);
                        dialog.dismiss();
                    }
                });

            }
        });
        return view;
    }

    // Old methods removed - now using QuoteManager utility class
    // QuoteManager handles:
    // - Same quote/affirmation throughout the day
    // - Smart tracking to never repeat until all are used
    // - Automatic reset when exhausted

    public void saveMood(int moodLevel, String reasonForMood, DatabaseHelper databaseHelper) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");

        // Get the current date and format it as a string
        String formattedDate = dateFormat.format(new Date());

        MoodTracker moodTracker;
        moodTracker = databaseHelper.moodTrackerDao().findMoodEntryByMoodDate(formattedDate);

        if (moodTracker == null) {
            moodTracker = new MoodTracker();
        }

        if (reasonForMood != null && !reasonForMood.trim().isEmpty()) {
            moodTracker.setReasonForTheMood(reasonForMood.trim());
        }

        if (moodTracker.getMoodLevel() == 0) {
            moodTracker.setMoodLevel(moodLevel);
            moodTracker.setMoodDate(formattedDate);
            moodTracker.setCreatedDate(new Date());
            databaseHelper.moodTrackerDao().addMood(moodTracker);
        } else {
            moodTracker.setMoodLevel(moodLevel);
            databaseHelper.moodTrackerDao().updateMood(moodTracker);
        }

    }

    // Unused pin/unpin methods removed - pin/unpin now handled only in BulletJournal screen


}