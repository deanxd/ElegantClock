package com.deanxd.elegantclock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.deanxd.elegantclock.util.CommonUtils;

/**
 * 绘制时钟表盘
 *
 * @author Dean
 */

public class ClockDialView extends View {
    private final static String TAG = "clockPannelView";

    /**
     * View默认最小宽度
     */
    private float mDefaultWidth;
    /**
     * 外圆边框宽度
     */
    private float mBordWith;
    /**
     * 长刻度线
     */
    private float mLongDegreeLength;
    /**
     * 短刻度线
     */
    private float mShortDegreeLength;

    /**
     * 刻度字大小
     */
    private int mDegreeNumSize;
    private float mRadius;

    private Paint mPaintCircle;
    private Paint mPaintDegree;
    private Paint mPaintDegreeNumber;
    private Rect mDegreeTextRect;
    private Typeface mTypeSatisfy;
    private Typeface mTypeHelvetica;

    public ClockDialView(Context context) {
        super(context);
        init();
    }

    public ClockDialView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClockDialView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDefaultWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());

        mPaintCircle = new Paint();
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setAntiAlias(true);

        mPaintDegree = new Paint();
        mPaintDegree.setAntiAlias(true);

        mPaintDegreeNumber = new Paint();
        mPaintDegreeNumber.setTextAlign(Paint.Align.CENTER);
        mPaintDegreeNumber.setFakeBoldText(true);

        mDegreeTextRect = new Rect();
        mTypeSatisfy = Typeface.createFromAsset(getContext().getAssets(), "Satisfy-Regular.ttf");
        mTypeHelvetica = Typeface.createFromAsset(getContext().getAssets(), "HelveticaNeueLt.ttf");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
        setSize();
    }

    /**
     * 测量长度
     */
    private void setSize() {
        int sizeLength = Math.min(getHeight() / 2, getWidth() / 2);
        mBordWith = sizeLength * 0.03f;
        mRadius = (Math.min(getHeight() / 2, getWidth() / 2) - mBordWith / 2);

        mLongDegreeLength = mRadius * 0.10f;
        mShortDegreeLength = mRadius * 0.08f;

        mDegreeNumSize = (int) (mRadius * 0.18f);
    }


    private int measure(int origin) {
        int result = (int) mDefaultWidth;
        int specMode = MeasureSpec.getMode(origin);
        int specSize = MeasureSpec.getSize(origin);

        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(result, specSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "ClockDialView onDraw-->" + this);
        drawPanel(canvas);
    }

    /**
     * 绘制表盘
     */
    private void drawPanel(Canvas canvas) {
        //画外圆
        mPaintCircle.setColor(Color.BLACK);
        mPaintCircle.setStrokeWidth(mBordWith);
        mPaintCircle.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mPaintCircle);

        mPaintCircle.setStyle(Paint.Style.FILL);
        mPaintCircle.setColor(Color.parseColor("#B2FA9C"));
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mPaintCircle);

        //画刻度线
        float degreeLength;
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                mPaintDegree.setStrokeWidth(getStrokeWidth(6));
                degreeLength = mLongDegreeLength;
            } else {
                mPaintDegree.setStrokeWidth(getStrokeWidth(3));
                degreeLength = mShortDegreeLength;
            }
            canvas.drawLine(getWidth() / 2, Math.abs(getHeight() / 2 - mRadius),
                    getWidth() / 2, Math.abs(getHeight() / 2 - mRadius) + degreeLength, mPaintDegree);

            canvas.rotate(360 / 60, getWidth() / 2, getHeight() / 2);
        }

        mPaintDegreeNumber.setTextSize(mDegreeNumSize);
        mPaintDegreeNumber.setTypeface(mTypeHelvetica);
        //刻度数字

        canvas.translate(getWidth() / 2, getHeight() / 2);

        for (int i = 0; i < 12; i++) {
            float[] floats = CommonUtils.calculatePoint((i + 1) * 30, mRadius * 0.8f * 0.98f, 0);
            String text = String.valueOf(i + 1);

            mPaintDegreeNumber.getTextBounds(text, 0, text.length(), mDegreeTextRect);
            canvas.drawText(text, floats[2], floats[3] + Math.abs(mDegreeTextRect.bottom - mDegreeTextRect.top) / 2, mPaintDegreeNumber);
        }

        mPaintDegreeNumber.setTypeface(mTypeSatisfy);
        String tips = "Designed by Dean";
        mPaintDegreeNumber.setTextSize(mDegreeNumSize / 2);
        mPaintDegreeNumber.getTextBounds(tips, 0, tips.length(), mDegreeTextRect);
        canvas.drawText(tips, 0, getHeight() / 4 + Math.abs(mDegreeTextRect.bottom - mDegreeTextRect.top) / 2, mPaintDegreeNumber);
    }

    private int getStrokeWidth(int width) {
        float factor = mRadius * 0.005f;
        return (int) (factor * width);
    }
}
