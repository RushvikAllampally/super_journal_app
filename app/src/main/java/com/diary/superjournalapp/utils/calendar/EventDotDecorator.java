package com.diary.superjournalapp.utils.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Decorator to show colored dots below dates with journal entries
 * Each dot represents a different journal category
 * Preserves theme-aware text color for date numbers
 */
public class EventDotDecorator implements DayViewDecorator {
    private final HashSet<CalendarDay> dates;
    private final List<Integer> colors;
    private final int textColor;
    
    public EventDotDecorator(Collection<CalendarDay> dates, List<Integer> colors, int textColor) {
        this.dates = new HashSet<>(dates);
        this.colors = colors;
        this.textColor = textColor;
    }
    
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }
    
    @Override
    public void decorate(DayViewFacade view) {
        // Preserve theme-aware text color (black in light, white in dark)
        view.addSpan(new ForegroundColorSpan(textColor));
        // Add dots below the date
        view.addSpan(new MultiDotSpan(colors));
    }
    
    /**
     * Span to draw multiple colored dots below the date
     */
    private static class MultiDotSpan implements LineBackgroundSpan {
        private final List<Integer> colors;
        private final float dotRadius = 5f;  // Increased to 5f for better visibility
        private final float dotSpacing = 8f;  // Increased to 8f for better separation
        
        public MultiDotSpan(List<Integer> colors) {
            this.colors = colors;
        }
        
        @Override
        public void drawBackground(Canvas canvas, Paint paint,
                                   int left, int right, int top, int baseline, int bottom,
                                   CharSequence charSequence, int start, int end, int lineNum) {
            if (colors == null || colors.isEmpty()) {
                return;
            }
            
            System.out.println("EventDotDebug: Drawing " + colors.size() + " dots");
            
            // Enable anti-aliasing for smooth circles
            paint.setAntiAlias(true);
            paint.setStyle(android.graphics.Paint.Style.FILL);
            
            // Calculate the total width needed for all dots
            float totalWidth = colors.size() * (dotRadius * 2) + (colors.size() - 1) * dotSpacing;
            
            // Calculate starting X position to center the dots
            float startX = (left + right) / 2f - totalWidth / 2f + dotRadius;
            
            // Position dots well below the date text
            float cy = bottom + dotRadius * 5;  // Moved even lower
            
            // Draw each dot
            for (int i = 0; i < colors.size(); i++) {
                paint.setColor(colors.get(i));
                float cx = startX + i * (dotRadius * 2 + dotSpacing);
                System.out.println("EventDotDebug: Drawing dot " + i + " at x=" + cx + ", y=" + cy + ", radius=" + dotRadius);
                canvas.drawCircle(cx, cy, dotRadius, paint);
            }
        }
    }
}
