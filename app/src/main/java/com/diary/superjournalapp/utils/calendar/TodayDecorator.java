package com.diary.superjournalapp.utils.calendar;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * Decorator to make today's date bold with theme-aware text color
 * This remains visible even when another date is selected
 */
public class TodayDecorator implements DayViewDecorator {
    private final CalendarDay today;
    private final int textColor;
    
    public TodayDecorator(int textColor) {
        this.today = CalendarDay.today();
        this.textColor = textColor;
    }
    
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(today);
    }
    
    @Override
    public void decorate(DayViewFacade view) {
        // Make today's date bold with theme-aware color
        view.addSpan(new ForegroundColorSpan(textColor));
        view.addSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD));
    }
}
