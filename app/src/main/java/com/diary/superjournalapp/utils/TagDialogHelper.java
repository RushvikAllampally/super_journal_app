package com.diary.superjournalapp.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.diary.superjournalapp.dialogs.TagDialogFragment;

/**
 * @deprecated This class is deprecated and will be removed in a future release.
 * Please use {@link TagDialogFragment} instead.
 */
@Deprecated
public class TagDialogHelper {

    private Context context;
    private long journalId;
    
    /**
     * Constructor for the TagDialogHelper
     * 
     * @param context Context to create dialog
     * @param journalId The journal ID to manage tags for
     */
    @Deprecated
    public TagDialogHelper(Context context, long journalId) {
        this.context = context;
        this.journalId = journalId;
    }
    
    /**
     * Show the tag management dialog
     * @deprecated Use TagDialogFragment.newInstance(journalId).show(fragmentManager, "tag_dialog") instead
     */
    @Deprecated
    public void showTagDialog() {
        if (context instanceof FragmentActivity) {
            // Show the new dialog fragment instead
            TagDialogFragment dialogFragment = TagDialogFragment.newInstance(journalId);
            dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "tag_dialog");
        } else {
            // Cannot show fragment dialog with this context
            Toast.makeText(context, "Cannot show tag dialog with this context type", Toast.LENGTH_SHORT).show();
        }
    }
}
