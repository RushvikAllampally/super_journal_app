package com.diary.superjournalapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.TextView;

import com.diary.superjournalapp.constants.ApplicationConstants;

public class FontSizeUtils {

    // Font size preference keys
    public static final String FONT_SIZE_PREF = "font_size_preference";
    public static final String FONT_SIZE_SMALL = "small";
    public static final String FONT_SIZE_MEDIUM = "medium"; // default
    public static final String FONT_SIZE_LARGE = "large";
    public static final String FONT_SIZE_EXTRA_LARGE = "extra_large";

    // Corresponding sizes in SP
    private static final int SIZE_SMALL = 15;
    private static final int SIZE_MEDIUM = 18;
    private static final int SIZE_LARGE = 22;
    private static final int SIZE_EXTRA_LARGE = 26;

    /**
     * Get the current font size preference
     * 
     * @param context The context
     * @return The current font size preference
     */
    public static String getFontSizePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        return preferences.getString(FONT_SIZE_PREF, FONT_SIZE_MEDIUM);
    }

    /**
     * Save the font size preference
     * 
     * @param context The context
     * @param fontSizePreference The font size preference to save
     */
    public static void saveFontSizePreference(Context context, String fontSizePreference) {
        SharedPreferences preferences = context.getSharedPreferences(
                ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FONT_SIZE_PREF, fontSizePreference);
        editor.apply();
    }

    /**
     * Get the actual font size in SP based on the preference
     * 
     * @param context The context
     * @return The font size in SP
     */
    public static int getFontSizeInSp(Context context) {
        String preference = getFontSizePreference(context);
        
        switch (preference) {
            case FONT_SIZE_SMALL:
                return SIZE_SMALL;
            case FONT_SIZE_LARGE:
                return SIZE_LARGE;
            case FONT_SIZE_EXTRA_LARGE:
                return SIZE_EXTRA_LARGE;
            case FONT_SIZE_MEDIUM:
            default:
                return SIZE_MEDIUM;
        }
    }

    /**
     * Apply the current font size preference to a TextView
     * 
     * @param context The context
     * @param textView The TextView to apply the font size to
     */
    public static void applyFontSize(Context context, TextView textView) {
        int fontSize = getFontSizeInSp(context);
        textView.setTextSize(fontSize);
    }

    /**
     * Apply the current font size preference to an EditText
     * 
     * @param context The context
     * @param editText The EditText to apply the font size to
     */
    public static void applyFontSize(Context context, EditText editText) {
        int fontSize = getFontSizeInSp(context);
        editText.setTextSize(fontSize);
    }
}
