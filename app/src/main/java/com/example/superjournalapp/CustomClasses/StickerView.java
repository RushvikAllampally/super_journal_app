package com.example.superjournalapp.CustomClasses;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.example.superjournalapp.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.superjournalapp.R;

import java.util.ArrayList;
import java.util.List;

public class StickerView extends AppCompatEditText {
    private List<Sticker> stickers = new ArrayList<>();
    private Drawable checkMarkDrawable;
    private Drawable closeDrawable;
    private Sticker activeSticker;

    public StickerView(Context context) {
        super(context);
        init();
    }

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setCursorVisible(true);
        setTextIsSelectable(false);

        checkMarkDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.color_palette, null);
        closeDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.color_palette, null);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activeSticker != null) {
                    setActiveSticker(null);
                    invalidate();
                }
            }
        });
    }

    public void addSticker(int stickerResId) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), stickerResId);
        Sticker sticker = new Sticker(drawable);
        stickers.add(sticker);
        setActiveSticker(sticker);
        invalidate();
    }

    private void setActiveSticker(Sticker sticker) {
        activeSticker = sticker;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Sticker sticker : stickers) {
            sticker.draw(canvas);
        }

        if (activeSticker != null) {
            drawBoundingBox(canvas);
        }
    }

    private void drawBoundingBox(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));

        Rect bounds = activeSticker.bounds;
        canvas.drawRect(bounds, paint);

        int iconSize = 50;
        checkMarkDrawable.setBounds(bounds.right + 20, bounds.top - 20, bounds.right + 20 + iconSize, bounds.top - 20 + iconSize);
        checkMarkDrawable.draw(canvas);

        closeDrawable.setBounds(bounds.left - iconSize - 20, bounds.top - 20, bounds.left - 20, bounds.top - 20 + iconSize);
        closeDrawable.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (Sticker sticker : stickers) {
                    if (sticker.isTouched(touchX, touchY)) {
                        setActiveSticker(sticker);
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (activeSticker != null) {
                    activeSticker.move(touchX, touchY);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                setActiveSticker(null); // Reset active sticker
                break;
        }

        return true;
    }



    private class Sticker {
        private Drawable drawable;
        private Rect bounds;

        private float offsetX, offsetY; // Offset to adjust sticker position

        Sticker(Drawable drawable) {
            this.drawable = drawable;
            this.bounds = new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }

        void setOffset(float offsetX, float offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        void move(float touchX, float touchY) {
            bounds.left = (int) (touchX - offsetX);
            bounds.top = (int) (touchY - offsetY);
            bounds.right = bounds.left + drawable.getIntrinsicWidth();
            bounds.bottom = bounds.top + drawable.getIntrinsicHeight();
        }

        boolean isTouched(float x, float y) {
            return bounds.contains((int) x, (int) y);
        }

        void draw(Canvas canvas) {
            drawable.setBounds(bounds);
            drawable.draw(canvas);
        }
    }

}



