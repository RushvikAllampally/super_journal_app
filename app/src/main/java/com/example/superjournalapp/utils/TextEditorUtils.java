package com.example.superjournalapp.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.superjournalapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class TextEditorUtils {


    public static void colorPaletteOnClickListener(BottomSheetDialog bottomSheetDialog, EditText journalContent, Context context) {

        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.color_picker);

        CardView blackImage = bottomSheetDialog.findViewById(R.id.black_color_select);
        CardView greyImage = bottomSheetDialog.findViewById(R.id.grey_color_select);
        CardView redImage = bottomSheetDialog.findViewById(R.id.red_color_select);
        CardView greenImage = bottomSheetDialog.findViewById(R.id.green_color_select);
        CardView brownImage = bottomSheetDialog.findViewById(R.id.brown_color_select);
        CardView yellowImage = bottomSheetDialog.findViewById(R.id.yellow_color_select);
        CardView purpleImage = bottomSheetDialog.findViewById(R.id.purple_color_select);
        CardView blueImage = bottomSheetDialog.findViewById(R.id.blue_color_select);
        CardView orangeImage = bottomSheetDialog.findViewById(R.id.orange_color_select);
        CardView pinkImage = bottomSheetDialog.findViewById(R.id.pink_color_select);
        CardView tealImage = bottomSheetDialog.findViewById(R.id.teal_color_select);

        blackImage.setOnClickListener(v -> journalContent.setText(applyColor(ContextCompat.getColor(context, R.color.black), journalContent)));
        greyImage.setOnClickListener(v -> journalContent.setText(applyColor(ContextCompat.getColor(context, R.color.grey), journalContent)));
        redImage.setOnClickListener(v -> journalContent.setText(applyColor(ContextCompat.getColor(context, R.color.red), journalContent)));
        greenImage.setOnClickListener(v -> journalContent.setText(applyColor(ContextCompat.getColor(context, R.color.green), journalContent)));
        brownImage.setOnClickListener(v -> journalContent.setText(applyColor(ContextCompat.getColor(context, R.color.brown), journalContent)));
        yellowImage.setOnClickListener(v -> journalContent.setText(applyColor(ContextCompat.getColor(context, R.color.yellow), journalContent)));
        purpleImage.setOnClickListener(v -> journalContent.setText(applyColor(ContextCompat.getColor(context, R.color.purple), journalContent)));
        blueImage.setOnClickListener(v -> journalContent.setText(applyColor(ContextCompat.getColor(context, R.color.blue), journalContent)));
        orangeImage.setOnClickListener(v -> journalContent.setText(applyColor(ContextCompat.getColor(context, R.color.orange), journalContent)));
        pinkImage.setOnClickListener(v -> journalContent.setText(applyColor(ContextCompat.getColor(context, R.color.pink), journalContent)));
        tealImage.setOnClickListener(v -> journalContent.setText(applyColor(ContextCompat.getColor(context, R.color.teal), journalContent)));

        // Set BottomSheetBehavior to STATE_EXPANDED
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) bottomSheetDialog.findViewById(R.id.design_bottom_sheet));
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();

    }

    public static void textStylesOnClickListener(BottomSheetDialog bottomSheetDialog,EditText journalContent,Context context) {
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.text_styles_dialog);

        // Wait until the view is created, then get BottomSheetBehavior
        View bottomSheetView = bottomSheetDialog.findViewById(R.id.text_style_bottom_sheet);
        TextView italicStyle = bottomSheetView.findViewById(R.id.italic_style);
        TextView boldStyle = bottomSheetView.findViewById(R.id.bold_style);
        TextView normalStyle = bottomSheetView.findViewById(R.id.normal_style);

        normalStyle.setOnClickListener(v -> journalContent.setText(applyStyle(Typeface.NORMAL, journalContent)));
        boldStyle.setOnClickListener(v -> journalContent.setText(applyStyle(Typeface.BOLD, journalContent)));
        italicStyle.setOnClickListener(v -> journalContent.setText(applyStyle(Typeface.ITALIC, journalContent)));

        if (bottomSheetView != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetView);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        bottomSheetDialog.show();
    }

    public static SpannableStringBuilder applyColor(int color, EditText editText) {

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        Spanned existingSpanned = (Spanned) editText.getText();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(existingSpanned);

        if (start != -1 && end != -1) {
            spannableStringBuilder.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableStringBuilder;
    }

    public static SpannableStringBuilder applyStyle(int style, EditText editText) {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        Spanned existingSpanned = (Spanned) editText.getText();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(existingSpanned);


        if (start != -1 && end != -1) {
            if (style == Typeface.NORMAL) {
                int[] stylesToRemove = {Typeface.BOLD, Typeface.ITALIC};
                spannableStringBuilder = removeStyles(stylesToRemove, editText);
            } else {
                spannableStringBuilder.setSpan(new StyleSpan(style), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannableStringBuilder;
    }

    public static SpannableStringBuilder removeStyles(int[] styles, EditText editText) {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(editText.getText());

        for (int style : styles) {
            // Get existing style spans in the selected range
            StyleSpan[] existingSpans = spannableStringBuilder.getSpans(start, end, StyleSpan.class);

            // Remove the specified style spans
            for (StyleSpan existingSpan : existingSpans) {
                if (existingSpan.getStyle() == style) {
                    spannableStringBuilder.removeSpan(existingSpan);
                }
            }
        }

        return spannableStringBuilder;
    }

}
