package com.diary.superjournalapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.constants.ApplicationConstants;

public class JournalPopupAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] journalTypes;

    public JournalPopupAdapter(@NonNull Context context, String[] journalTypes) {
        super(context, R.layout.list_item, journalTypes);
        this.context = context;
        this.journalTypes = journalTypes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.textView);
        ImageView iconView = convertView.findViewById(R.id.journal_icon);

        String journalType = journalTypes[position];
        textView.setText(journalType);

        // Set the appropriate icon for each journal type
        int iconRes = getIconForJournalType(journalType);
        iconView.setImageResource(iconRes);

        return convertView;
    }

    private int getIconForJournalType(String journalType) {
        switch (journalType) {
            case ApplicationConstants.REFLECTIVE_JOURNAL:
                return R.drawable.ic_diary;
            case ApplicationConstants.GRATITUDE_JOURNAL:
                return R.drawable.ic_gratitude;
            case ApplicationConstants.BULLET_JOURNAL:
                return R.drawable.ic_bullet;
            case ApplicationConstants.DREAM_JOURNAL:
                return R.drawable.ic_dream;
            default:
                return R.drawable.ic_diary;
        }
    }
}
