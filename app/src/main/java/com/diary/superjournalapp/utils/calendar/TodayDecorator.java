package com.diary.superjournalapp.utils.calendar;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * Decorator to show a subtle circle around today's date
 * This remains visible even when another date is selected
 */
public class TodayDecorator implements DayViewDecorator {
    private final CalendarDay today;
    private final int color;
    
    public TodayDecorator(int color) {
        this.today = CalendarDay.today();
        this.color = color;
    }
    
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(today);
    }
    
    @Override
    public void decorate(DayViewFacade view) {
        // Create a subtle stroke circle
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(2, color); // 2dp stroke
        drawable.setColor(Color.TRANSPARENT); // Transparent fill
        
        view.setBackgroundDrawable(drawable);
    }
}
