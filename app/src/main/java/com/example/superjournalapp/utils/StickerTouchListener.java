package com.example.superjournalapp.utils;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class StickerTouchListener implements View.OnTouchListener {
    private Drawable stickerDrawable;
    private float lastTouchX, lastTouchY;
    private EditText yourEditText;

    public StickerTouchListener(Drawable stickerDrawable, EditText yourEditText) {
        this.stickerDrawable = stickerDrawable;
        this.yourEditText = yourEditText;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getRawX();
                lastTouchY = event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getRawX() - lastTouchX;
                float deltaY = event.getRawY() - lastTouchY;

                // Adjust the position of the sticker
                stickerDrawable.setBounds(
                        stickerDrawable.getBounds().left + (int) deltaX,
                        stickerDrawable.getBounds().top + (int) deltaY,
                        stickerDrawable.getBounds().right + (int) deltaX,
                        stickerDrawable.getBounds().bottom + (int) deltaY);

                lastTouchX = event.getRawX();
                lastTouchY = event.getRawY();

                // Update the ImageSpan with the modified Drawable
                updateImageSpan();

                break;

            case MotionEvent.ACTION_UP:
                // Remove the TouchListener after the dragging ends
                v.setOnTouchListener(null);
                break;
        }

        return true;
    }

    private void updateImageSpan() {
        // Update the ImageSpan with the modified Drawable
        SpannableStringBuilder builder = new SpannableStringBuilder(yourEditText.getText());
        ImageSpan[] imageSpans = builder.getSpans(0, builder.length(), ImageSpan.class);

        if (imageSpans.length > 0) {
            builder.setSpan(new ImageSpan(stickerDrawable), builder.getSpanStart(imageSpans[0]), builder.getSpanEnd(imageSpans[0]), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            yourEditText.setText(builder);
        }
    }
}
