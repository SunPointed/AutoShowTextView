package test.lqy.com.autoshowtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lqy on 16/3/21.
 */
public class AutoShowTextView extends TextView {

    float textWidth = 0;
    float textBaseLine;
    float viewWidth = 0;
    float viewHeight;
    String text = null;

    float textSize;
    int textColor;

    Paint mPaint;

    Rect bounds;

    float start;

    Timer timer;

    boolean isScroll = false;

    float speed = 2;

    public AutoShowTextView(Context context) {
        this(context, null);
    }

    public void setIsScroll(boolean isScroll) {
        this.isScroll = isScroll;
        start = 0;
        postInvalidate();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public AutoShowTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoShowTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //this must be single line
        setSingleLine(true);

        //gravity must be this
        setGravity(Gravity.TOP | Gravity.START);

        //text alignment must be this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(TEXT_ALIGNMENT_GRAVITY);
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoShow);
        isScroll = a.getBoolean(R.styleable.AutoShow_isScroll, false);
        a.recycle();

        bounds = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        start = 0;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isScroll) {
                    start -= speed;
                    if (start < -textWidth) {
                        start = viewWidth / 2;
                    }
                    postInvalidate();
                }
            }
        }, 500, 50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (text == null) {
            text = getText().toString();
//            Log.i("lqy", "text : " + text);
        }

        if (textWidth == 0) {
            getPaint().getTextBounds(text, 0, text.length(), bounds);
            textWidth = bounds.width();
            textBaseLine = getBaseline();

            textSize = getTextSize();
            textColor = getCurrentTextColor();
            mPaint.setColor(textColor);
            mPaint.setTextSize(textSize);
        }
//
//        if (viewWidth == 0) {
//            viewWidth = getWidth();
//            viewHeight = getHeight();
//        }

        if (viewWidth > textWidth) {
//            Log.i("lqy", "viewWidth:"+viewWidth);
//            Log.i("lqy", "textWidth:"+textWidth);
            canvas.drawText(text, (viewWidth - textWidth) / 2, textBaseLine, mPaint);
        } else {
//            Log.i("lqy", "start:"+start);
//            Log.i("lqy", "viewWidth:"+viewWidth);
//            Log.i("lqy", "textWidth:"+textWidth);
            if (isScroll) {
                canvas.drawText(text, start, textBaseLine, mPaint);
                if (start < 0) {
                    canvas.drawText(text, start + textWidth + viewWidth / 2, textBaseLine, mPaint);
                } else {
                    canvas.drawText(text, start - (textWidth + viewWidth / 2), textBaseLine, mPaint);
                }
            } else {
                canvas.drawText(text, 0, textBaseLine, mPaint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = getWidth();
        viewHeight = getHeight();
        textWidth = 0;
        text = null;
    }
}
