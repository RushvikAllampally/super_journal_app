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
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            nothingFoundImage.setVisibility(View.GONE);
        }
        int index = journalsList.size() > 5 ? 5 : journalsList.size();

        journalRecyclerAdaptor = new JournalRecyclerAdaptor(context, new ArrayList<>(journalsList.subList(0, index)));

        recyclerView.setAdapter(journalRecyclerAdaptor);
        journalRecyclerAdaptor.notifyDataSetChanged();
    }

    public static void notifyHomeBulletChanges() {
        bulletJournalEntity = databaseHelper.bulletJournalContentDao().getMostRecentBulletJournal();
        bulletJournalEntities = databaseHelper.bulletJournalContentDao().getPinnedBulletJournals(true);


        Gson gson = new Gson();
        Type listType = new TypeToken<List<BulletEntryDetails>>() {
        }.getType();
        List<BulletEntryDetails> bulletEntryDetails = new ArrayList<>();

        if (bulletJournalEntity != null && (bulletJournalEntities.size() == 0)) {
            if (bulletJournalEntity.isDontShow) {
                tasksCard.setVisibility(View.GONE);
            } else {
                bulletEntryDetails = gson.fromJson(bulletJournalEntity.taskListJson, listType);
                homeDisplayBulletJournalId = bulletJournalEntity.getJournalId();
            }
        } else if (bulletJournalEntities.size() > 0) {
            bulletEntryDetails = gson.fromJson(bulletJournalEntities.get(bulletJournalEntities.size() - 1).taskListJson, listType);
            homeDisplayBulletJournalId = bulletJournalEntities.get(bulletJournalEntities.size() - 1).getJournalId();
        } else {
            tasksCard.setVisibility(View.GONE);
        }

        linearLayoutContainer.removeAllViews();

        // Add the TextView to the LinearLayout

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
        streakCount = view.findViewById(R.id.streak_edit_txt);
        linearLayoutContainer = view.findViewById(R.id.linear_layout_container);

        NestedScrollView nestedScrollView = view.findViewById(R.id.next_scroll_view);
        nestedScrollView.scrollTo(0, 0);

        int streak = JournalUtils.updateStreakOnLoad(view.getContext());
        streakCount.setText(String.valueOf(streak));

        quote.setText(getQuoteOfTheDay().getQuote());
        quoteAuthor.setText(getQuoteOfTheDay().getAuthor());
        affirmation.setText(getAffirmationOfTheDay());

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

        pinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });

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

                TextView moodLevelMessage = dialog.findViewById(R.id.mood_level_msg);
                TextView moodLevelText = dialog.findViewById(R.id.mood_level_text);
                TextView moodLevelName = dialog.findViewById(R.id.mood_level_name);
                ImageButton moodOne = dialog.findViewById(R.id.mood_one);
                ImageButton moodTwo = dialog.findViewById(R.id.mood_two);
                ImageButton moodThree = dialog.findViewById(R.id.mood_three);
                ImageButton moodFour = dialog.findViewById(R.id.mood_four);
                ImageButton moodFive = dialog.findViewById(R.id.mood_five);
                Button saveMoodBtn = dialog.findViewById(R.id.save_mood);
//                EditText reasonForMoodTxt = dialog.findViewById(R.id.reason_for_mood);

                moodLevel = 0;

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

    private QuoteDto getQuoteOfTheDay() {
        // Get the current day of the year
        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        SharedPreferences preferences = getActivity().getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        // Retrieve the stored quote index for the current day
        int storedIndex = preferences.getInt("quoteIndex" + dayOfYear, -1);

        // If there is no stored index for the current day, select a random one
        if (storedIndex == -1) {
            storedIndex = getRandomQuoteIndex();
            // Store the selected index for the current day
            preferences.edit().putInt("quoteIndex" + dayOfYear, storedIndex).apply();
        }

        // Return the quote corresponding to the selected index
        return ApplicationConstants.QUOTES_ARRAY.get(storedIndex);
    }

    private int getRandomQuoteIndex() {
        // Generate a random index within the range of the quotes array
        return new Random().nextInt(ApplicationConstants.QUOTES_ARRAY.size());
    }

    private String getAffirmationOfTheDay() {
        // Get the current day of the year
        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        SharedPreferences preferences = getActivity().getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        // Retrieve the stored quote index for the current day
        int storedIndex = preferences.getInt("affirmationIndex" + dayOfYear, -1);

        // If there is no stored index for the current day, select a random one
        if (storedIndex == -1) {
            storedIndex = getRandomAffirmationIndex();
            // Store the selected index for the current day
            preferences.edit().putInt("affirmationIndex" + dayOfYear, storedIndex).apply();
        }

        // Return the quote corresponding to the selected index
        return ApplicationConstants.AFFIRMATIONS[storedIndex];
    }

    private int getRandomAffirmationIndex() {
        // Generate a random index within the range of the quotes array
        return new Random().nextInt(ApplicationConstants.AFFIRMATIONS.length);
    }

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

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Unpin Task");
        builder.setMessage("Unpinning will remove the tasks list from home screen. Are you sure about it ?");
        builder.setPositiveButton("Unpin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "save," so finish the activity and discard changes
                bulletJournalEntity.setListPinned(false);
                bulletJournalEntity.setDontShow(true);
                databaseHelper.bulletJournalContentDao().updateBulletJournalEntity(bulletJournalEntity);
                tasksCard.setVisibility(View.GONE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Cancel," so do nothing and close the dialog
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}