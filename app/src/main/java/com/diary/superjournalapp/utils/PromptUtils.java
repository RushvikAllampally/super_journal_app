package com.diary.superjournalapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.constants.ApplicationConstants;

import java.util.List;
import java.util.Random;

public class PromptUtils {
    
    /**
     * Shows a writing prompt dialog based on the journal type
     * 
     * @param context Context to show the dialog
     * @param journalType Type of journal (from ApplicationConstants)
     * @param titleEditText The EditText for the journal title
     * @param contentEditText The EditText for the journal content
     */
    public static void showPromptDialog(Context context, String journalType, 
                                      EditText titleEditText, EditText contentEditText) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.prompt_dialog_layout);
        
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView promptText = dialog.findViewById(R.id.prompt_text);
        Button newPromptButton = dialog.findViewById(R.id.new_prompt_button);
        Button usePromptButton = dialog.findViewById(R.id.use_prompt_button);
        
        // Set the dialog title based on journal type
        dialogTitle.setText(journalType + " Prompt");
        
        // Add subtitle/explanation
        TextView dialogSubtitle = dialog.findViewById(R.id.dialog_subtitle);
        if (dialogSubtitle != null) {
            dialogSubtitle.setText("Select a writing prompt to inspire your journal entry");
            dialogSubtitle.setVisibility(View.VISIBLE);
        }
        
        // Get appropriate prompts list based on journal type
        List<String> prompts = getPromptsForJournalType(journalType);
        
        // Show a random prompt
        if (prompts != null && !prompts.isEmpty()) {
            String randomPrompt = getRandomPrompt(prompts);
            promptText.setText(randomPrompt);
        }
        
        // Get a new prompt when clicking "New Prompt" button
        newPromptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prompts != null && !prompts.isEmpty()) {
                    String randomPrompt = getRandomPrompt(prompts);
                    promptText.setText(randomPrompt);
                }
            }
        });
        
        // Use the prompt in the journal
        usePromptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prompt = promptText.getText().toString();
                
                // Set prompt as title only
                titleEditText.setText(prompt);
                
                dialog.dismiss();
            }
        });
        
        dialog.show();
    }
    
    /**
     * Gets a random prompt from the provided list
     */
    private static String getRandomPrompt(List<String> prompts) {
        Random random = new Random();
        int index = random.nextInt(prompts.size());
        return prompts.get(index);
    }
    
    /**
     * Gets the appropriate prompts list based on journal type
     */
    private static List<String> getPromptsForJournalType(String journalType) {
        switch (journalType) {
            case ApplicationConstants.GRATITUDE_JOURNAL:
                return ApplicationConstants.GRATITUDE_PROMPTS;
            case ApplicationConstants.REFLECTIVE_JOURNAL:
                return ApplicationConstants.REFLECTIVE_PROMPTS;
            case ApplicationConstants.DREAM_JOURNAL:
                return ApplicationConstants.DREAM_PROMPTS;
            case ApplicationConstants.BULLET_JOURNAL:
                return ApplicationConstants.BULLET_PROMPTS;
            default:
                return ApplicationConstants.REFLECTIVE_PROMPTS;
        }
    }
    
    /**
     * Checks if daily prompts are enabled in the settings
     */
    public static boolean arePromptsEnabled(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(ApplicationConstants.DAILY_PROMPTS_KEY, true);
    }
    
    /**
     * Sets whether prompts are enabled
     */
    public static void setPromptsEnabled(Context context, boolean enabled) {
        SharedPreferences preferences = context.getSharedPreferences(
                ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ApplicationConstants.DAILY_PROMPTS_KEY, enabled);
        editor.apply();
    }
}
