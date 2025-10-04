package com.diary.superjournalapp.utils;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for applying real-time text styles as you type.
 * This is automatically enabled for all EditText elements.
 */
public class LiveTextStyler {
    
    // Style constants
    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_BOLD = 1;
    private static final int STYLE_ITALIC = 2;
    private static final int STYLE_UNDERLINE = 3;
    
    // Current active styling
    private static int currentStyle = STYLE_NORMAL;
    private static int currentColor = 0; // 0 means default color
    private static Layout.Alignment currentAlignment = Layout.Alignment.ALIGN_NORMAL;
    private static String currentFont = null;
    
    // Flag to prevent recursive text changes
    private static boolean isUpdating = false;
    
    /**
     * Setup text watcher for an EditText to apply styles in real-time.
     * This is automatically called for journal content EditText fields.
     * 
     * @param editText The EditText to apply styles to
     */
    public static void setupLiveEditText(EditText editText) {
        // Check if the EditText already has our TextWatcher to avoid duplicates
        if (editText.getTag() != null && editText.getTag() instanceof List) {
            List<TextWatcher> watchers = (List<TextWatcher>) editText.getTag();
            for (TextWatcher watcher : watchers) {
                if (watcher instanceof LiveStyleTextWatcher) {
                    return; // Already has our watcher
                }
            }
        }
        
        TextWatcher watcher = new LiveStyleTextWatcher();
        editText.addTextChangedListener(watcher);
    }
    
    /**
     * TextWatcher implementation that applies styles as you type
     */
    private static class LiveStyleTextWatcher implements TextWatcher {
        private int startPosition;
        private int beforeLength;
        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!isUpdating) {
                startPosition = start;
                beforeLength = count;
            }
        }
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not used
        }
        
        @Override
        public void afterTextChanged(Editable s) {
            if (!isUpdating && s.length() > startPosition) {
                isUpdating = true;
                
                // Only apply styles to newly added text
                int count = s.length() - startPosition - beforeLength;
                int end = startPosition + count;
                
                if (end > startPosition) {
                    // Apply current active styles
                    if (currentStyle == STYLE_BOLD) {
                        s.setSpan(new StyleSpan(Typeface.BOLD), startPosition, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (currentStyle == STYLE_ITALIC) {
                        s.setSpan(new StyleSpan(Typeface.ITALIC), startPosition, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (currentStyle == STYLE_UNDERLINE) {
                        s.setSpan(new UnderlineSpan(), startPosition, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    
                    // Apply color if set
                    if (currentColor != 0) {
                        s.setSpan(new ForegroundColorSpan(currentColor), startPosition, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    
                    // Apply alignment if not default
                    if (currentAlignment != Layout.Alignment.ALIGN_NORMAL) {
                        s.setSpan(new AlignmentSpan.Standard(currentAlignment), startPosition, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    
                    // Apply font if set
                    if (currentFont != null) {
                        s.setSpan(new TypefaceSpan(currentFont), startPosition, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                
                isUpdating = false;
            }
        }
    }
    
    /**
     * Set the current text style
     * 
     * @param style Use STYLE_NORMAL, STYLE_BOLD, STYLE_ITALIC, or STYLE_UNDERLINE
     */
    public static void setCurrentStyle(int style) {
        currentStyle = style;
    }
    
    /**
     * Set the current text color
     * 
     * @param color The color value to apply (use 0 for default)
     */
    public static void setCurrentColor(int color) {
        currentColor = color;
    }
    
    /**
     * Set the current text alignment
     * 
     * @param alignment The alignment to apply
     */
    public static void setCurrentAlignment(Layout.Alignment alignment) {
        currentAlignment = alignment;
    }
    
    /**
     * Set the current font
     * 
     * @param fontName The font name to apply (null for default)
     */
    public static void setCurrentFont(String fontName) {
        currentFont = fontName;
    }
}
