package com.diary.superjournalapp.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.diary.superjournalapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class TextEditorUtils {

    public static void textStylesOnClickListener(BottomSheetDialog bottomSheetDialog, EditText journalContent, Context context) {
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.text_styles_dialog);

        // Wait until the view is created, then get BottomSheetBehavior
        View bottomSheetView = bottomSheetDialog.findViewById(R.id.text_style_bottom_sheet);
        
        // Text style views
        TextView italicStyle = bottomSheetView.findViewById(R.id.italic_style);
        TextView boldStyle = bottomSheetView.findViewById(R.id.bold_style);
        TextView normalStyle = bottomSheetView.findViewById(R.id.normal_style);
        TextView underlineStyle = bottomSheetView.findViewById(R.id.underline_style);
        
        // Text alignment views
        ImageButton alignLeft = bottomSheetView.findViewById(R.id.align_left);
        ImageButton alignCenter = bottomSheetView.findViewById(R.id.align_center);
        ImageButton alignRight = bottomSheetView.findViewById(R.id.align_right);
        
        // List option views
        Button bulletList = bottomSheetView.findViewById(R.id.bullet_list);
        Button numberList = bottomSheetView.findViewById(R.id.number_list);
        
        // Font selection
        Spinner fontSpinner = bottomSheetView.findViewById(R.id.font_spinner);
        setupFontSpinner(fontSpinner, journalContent, context);
        
        // Set click listeners for text styles
        normalStyle.setOnClickListener(v -> {
            LiveTextStyler.setCurrentStyle(0); // STYLE_NORMAL
            journalContent.setText(applyStyle(Typeface.NORMAL, journalContent));
            Toast.makeText(context, "Normal style applied", Toast.LENGTH_SHORT).show();
        });
        
        boldStyle.setOnClickListener(v -> {
            LiveTextStyler.setCurrentStyle(1); // STYLE_BOLD
            journalContent.setText(applyStyle(Typeface.BOLD, journalContent));
            Toast.makeText(context, "Bold style applied", Toast.LENGTH_SHORT).show();
        });
        
        italicStyle.setOnClickListener(v -> {
            LiveTextStyler.setCurrentStyle(2); // STYLE_ITALIC
            journalContent.setText(applyStyle(Typeface.ITALIC, journalContent));
            Toast.makeText(context, "Italic style applied", Toast.LENGTH_SHORT).show();
        });
        
        underlineStyle.setOnClickListener(v -> {
            LiveTextStyler.setCurrentStyle(3); // STYLE_UNDERLINE
            journalContent.setText(applyUnderline(journalContent));
            Toast.makeText(context, "Underline style applied", Toast.LENGTH_SHORT).show();
        });
        
        // Set click listeners for alignment options
        alignLeft.setOnClickListener(v -> {
            LiveTextStyler.setCurrentAlignment(Layout.Alignment.ALIGN_NORMAL);
            journalContent.setText(applyAlignment(Layout.Alignment.ALIGN_NORMAL, journalContent));
            Toast.makeText(context, "Left alignment applied", Toast.LENGTH_SHORT).show();
        });
        
        alignCenter.setOnClickListener(v -> {
            LiveTextStyler.setCurrentAlignment(Layout.Alignment.ALIGN_CENTER);
            journalContent.setText(applyAlignment(Layout.Alignment.ALIGN_CENTER, journalContent));
            Toast.makeText(context, "Center alignment applied", Toast.LENGTH_SHORT).show();
        });
        
        alignRight.setOnClickListener(v -> {
            LiveTextStyler.setCurrentAlignment(Layout.Alignment.ALIGN_OPPOSITE);
            journalContent.setText(applyAlignment(Layout.Alignment.ALIGN_OPPOSITE, journalContent));
            Toast.makeText(context, "Right alignment applied", Toast.LENGTH_SHORT).show();
        });
        
        // Set click listeners for list options
        bulletList.setOnClickListener(v -> journalContent.setText(applyBulletList(journalContent)));
        numberList.setOnClickListener(v -> journalContent.setText(applyNumberedList(journalContent)));

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
                // Remove bold and italic formatting
                int[] stylesToRemove = {Typeface.BOLD, Typeface.ITALIC};
                spannableStringBuilder = removeStyles(stylesToRemove, editText);
                
                // Also remove underlines
                removeSpans(spannableStringBuilder, start, end, UnderlineSpan.class);
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
    
    /**
     * Apply underline to selected text
     * 
     * @param editText The EditText containing the text
     * @return The SpannableStringBuilder with underline applied
     */
    public static SpannableStringBuilder applyUnderline(EditText editText) {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        Spanned existingSpanned = (Spanned) editText.getText();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(existingSpanned);

        if (start != -1 && end != -1) {
            // Check if the selection already has an underline span
            UnderlineSpan[] existingSpans = spannableStringBuilder.getSpans(start, end, UnderlineSpan.class);
            
            if (existingSpans.length > 0) {
                // If already underlined, remove the underline
                for (UnderlineSpan span : existingSpans) {
                    spannableStringBuilder.removeSpan(span);
                }
            } else {
                // If not underlined, add underline
                spannableStringBuilder.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannableStringBuilder;
    }
    
    /**
     * Apply text alignment to selected text
     * 
     * @param alignment The alignment to apply
     * @param editText The EditText containing the text
     * @return The SpannableStringBuilder with alignment applied
     */
    public static SpannableStringBuilder applyAlignment(Layout.Alignment alignment, EditText editText) {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        Spanned existingSpanned = (Spanned) editText.getText();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(existingSpanned);

        if (start != -1 && end != -1) {
            // Remove any existing alignment spans
            removeSpans(spannableStringBuilder, start, end, AlignmentSpan.Standard.class);
            
            // Apply new alignment
            spannableStringBuilder.setSpan(new AlignmentSpan.Standard(alignment), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableStringBuilder;
    }
    
    /**
     * Apply bullet list formatting to selected text
     * 
     * @param editText The EditText containing the text
     * @return The SpannableStringBuilder with bullet list formatting applied
     */
    public static SpannableStringBuilder applyBulletList(EditText editText) {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        Spanned existingSpanned = (Spanned) editText.getText();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(existingSpanned);

        if (start != -1 && end != -1) {
            // Check if bullets already exist
            BulletSpan[] existingBullets = spannableStringBuilder.getSpans(start, end, BulletSpan.class);
            
            if (existingBullets.length > 0) {
                // Remove existing bullets
                for (BulletSpan span : existingBullets) {
                    spannableStringBuilder.removeSpan(span);
                }
            } else {
                // Add bullet span with indent
                spannableStringBuilder.setSpan(new BulletSpan(20), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannableStringBuilder;
    }
    
    /**
     * Apply numbered list formatting to selected text
     * 
     * @param editText The EditText containing the text
     * @return The SpannableStringBuilder with numbered list formatting applied
     */
    public static SpannableStringBuilder applyNumberedList(EditText editText) {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        Spanned existingSpanned = (Spanned) editText.getText();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(existingSpanned);
        
        // Split text into lines
        String text = spannableStringBuilder.subSequence(start, end).toString();
        String[] lines = text.split("\\n");
        
        if (lines.length > 0) {
            // Remove any existing leading margin spans
            removeSpans(spannableStringBuilder, start, end, LeadingMarginSpan.class);
            
            int currentPos = start;
            for (int i = 0; i < lines.length; i++) {
                int lineStart = currentPos;
                int lineEnd = lineStart + lines[i].length();
                
                // If not the last line, include the newline character
                if (i < lines.length - 1) {
                    lineEnd++;
                }
                
                // Create a number prefix for the line
                final String numberStr = (i + 1) + ". ";
                
                // Apply indentation for the numbered list
                spannableStringBuilder.setSpan(
                    new LeadingMarginSpan.Standard(30, 30), 
                    lineStart, 
                    lineEnd, 
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                
                currentPos = lineEnd;
            }
        }
        
        return spannableStringBuilder;
    }
    
    /**
     * Setup the font spinner with available fonts
     * 
     * @param spinner The spinner to set up
     * @param editText The EditText to apply fonts to
     * @param context The context
     */
    private static void setupFontSpinner(Spinner spinner, EditText editText, Context context) {
        // Define available fonts
        String[] fonts = {"Default", "Sans Serif", "Serif", "Monospace", "Cursive"};
        
        // Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, fonts);
        spinner.setAdapter(adapter);
        
        // Set listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Skip "Default"
                    String selectedFont = fonts[position];
                    LiveTextStyler.setCurrentFont(selectedFont.toLowerCase());
                    applyFont(selectedFont, editText);
                    Toast.makeText(context, selectedFont + " font applied", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    
    /**
     * Apply font to selected text
     * 
     * @param fontName The name of the font to apply
     * @param editText The EditText to apply the font to
     */
    private static void applyFont(String fontName, EditText editText) {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        if (start != -1 && end != -1) {
            Spanned existingSpanned = (Spanned) editText.getText();
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(existingSpanned);
            
            // Remove existing font spans
            removeSpans(spannableStringBuilder, start, end, TypefaceSpan.class);
            
            // Apply new font
            spannableStringBuilder.setSpan(new TypefaceSpan(fontName.toLowerCase()), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            
            editText.setText(spannableStringBuilder);
        }
    }
    
    /**
     * Helper method to remove spans of a specific type
     * 
     * @param spannableStringBuilder The SpannableStringBuilder to modify
     * @param start Start position
     * @param end End position
     * @param spanType Type of span to remove
     * @param <T> The span class
     */
    private static <T> void removeSpans(SpannableStringBuilder spannableStringBuilder, int start, int end, Class<T> spanType) {
        T[] spans = spannableStringBuilder.getSpans(start, end, spanType);
        for (T span : spans) {
            spannableStringBuilder.removeSpan(span);
        }
    }
}
