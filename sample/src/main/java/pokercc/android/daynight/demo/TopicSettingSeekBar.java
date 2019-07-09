package pokercc.android.daynight.demo;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.ColorRes;
import androidx.annotation.IntDef;

/**
 * 题库设置自定义view
 */
public class TopicSettingSeekBar extends View {

    private int thumbColor;
    private int thumbSize;
    private int lineColor;
    private int lineWidth;
    private int pointSize;
    private Paint linePaint, pointPaint, thumbPaint;
    private int thumbX = 0;


    private static final int SMAll = -1;
    private static final int LARGE = 1;
    private static final int MIDDLE = 0;
    @Stage
    private int stage;
    private ValueAnimator valueAnimator;

    @IntDef({SMAll, MIDDLE, LARGE})
    public @interface Stage {

    }

    public TopicSettingSeekBar(Context context) {
        super(context);
        init(null, 0);
    }

    public TopicSettingSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TopicSettingSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TopicSettingSeekBar, defStyle, 0);

        thumbColor = a.getColor(R.styleable.TopicSettingSeekBar_thumb_color, Color.BLUE);
        thumbSize = a.getDimensionPixelSize(R.styleable.TopicSettingSeekBar_thumb_size, 29);
        lineColor = a.getColor(R.styleable.TopicSettingSeekBar_line_color, Color.GRAY);
        lineWidth = a.getDimensionPixelSize(R.styleable.TopicSettingSeekBar_line_width, 2);
        pointSize = a.getDimensionPixelSize(R.styleable.TopicSettingSeekBar_point_size, 4);

        a.recycle();

        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineWidth);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(lineColor);
        pointPaint.setStyle(Paint.Style.FILL);

        thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thumbPaint.setColor(thumbColor);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setColor(thumbColor);
        setStage(MIDDLE, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(thumbSize * 2 + getPaddingTop() + getPaddingBottom(), MeasureSpec.EXACTLY);

                break;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        thumbX = getMeasuredThumbX();
    }

    @SuppressLint("NewApi")
    private int getMeasuredThumbX() {
        if (stage == MIDDLE) {
            return ((getMeasuredWidth() - getPaddingStart() - getPaddingEnd()) >> 1) + getPaddingStart();
        } else if (stage == SMAll) {
            return getPaddingStart() + thumbSize;
        } else if (stage == LARGE) {
            return getMeasuredWidth() - getPaddingEnd() - thumbSize;
        }
        return thumbSize;
    }

    public void setStage(@Stage int stage, final boolean anim) {
        this.stage = stage;
        if (!anim) {
            thumbX = getMeasuredThumbX();
            invalidate();
        } else {
            valueAnimator = ValueAnimator.ofInt(thumbX, getMeasuredThumbX());
            valueAnimator.setInterpolator(new OvershootInterpolator());
            valueAnimator.setDuration(150);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    thumbX = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            valueAnimator.start();

        }
    }


    public void setPointColor(@ColorRes int colorRes) {
        this.pointPaint.setColor(getResources().getColor(colorRes));
    }

    @SuppressLint({"ClickableViewAccessibility", "NewApi"})
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE: {
                thumbX = (int) event.getX();
                thumbX = Math.max(getPaddingStart() + thumbSize, thumbX);
                thumbX = Math.min(getMeasuredWidth() - getPaddingEnd() - thumbSize, thumbX);
                invalidate();
            }
            break;
            case MotionEvent.ACTION_UP: {
                int contentWidth = getMeasuredWidth() - getPaddingStart() - getPaddingEnd();
                if (thumbX < contentWidth >> 2) {
                    setStage(SMAll, true);
                } else if (thumbX > contentWidth * 0.75f) {
                    setStage(LARGE, true);
                } else {
                    setStage(MIDDLE, true);
                }
            }
            break;
        }
        return true;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int centerX = ((getMeasuredWidth() - getPaddingStart() - getPaddingEnd()) >> 1) + getPaddingStart();
        final int centerY = ((getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) >> 1) + getPaddingTop();
        canvas.drawLine(getPaddingStart(), centerY, getMeasuredWidth() - getPaddingEnd(), centerY, linePaint);
        canvas.drawCircle(pointSize + getPaddingStart(), centerY, pointSize, pointPaint);
        canvas.drawCircle(centerX, centerY, pointSize, pointPaint);
        canvas.drawCircle(getMeasuredWidth() - getPaddingEnd() - pointSize, centerY, pointSize, pointPaint);
        canvas.drawCircle(thumbX, centerY, thumbSize, thumbPaint);
    }
}
