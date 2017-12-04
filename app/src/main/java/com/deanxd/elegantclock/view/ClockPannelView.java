package com.deanxd.elegantclock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;
import java.text.Format;
import java.util.Calendar;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 绘制时钟面板
 *
 * @author Dean
 */

public class ClockPannelView extends SurfaceView implements SurfaceHolder.Callback {

    private final static String TAG = "clockPannelView";

    private final static long FRESH_TIME = 1000 / 60;

    /**
     * View默认最小宽度
     */
    private float mDefaultWidth;
    /**
     * 外圆边框宽度
     */
    private float mBordWith;

    /**
     * 秒针长度
     */
    private float mSecondPointerLength;
    /**
     * 分针长度
     */
    private float mMinutePointerLength;
    /**
     * 时针长度
     */
    private float mHourPointerLength;

    /**
     * 指针反向超过圆点的长度
     */
    private float mPointBackLength;
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
    private Paint mPaintPoint;
    private DrawTimerTask mTimerTask;
    private ScheduledExecutorService mScheduledService;
    private Rect mDegreeTextRect;
    private Typeface mTypeSatisfy;
    private Typeface mTypeHelvetica;


    public ClockPannelView(Context context) {
        super(context);
        init();
    }

    public ClockPannelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClockPannelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDefaultWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());

        SurfaceHolder mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        mPaintCircle = new Paint();
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setAntiAlias(true);

        mPaintDegree = new Paint();
        mPaintDegree.setAntiAlias(true);

        mPaintDegreeNumber = new Paint();
        mPaintDegreeNumber.setTextAlign(Paint.Align.CENTER);
        mPaintDegreeNumber.setFakeBoldText(true);

        mPaintPoint = new Paint();
        mPaintPoint.setColor(Color.BLACK);
        mPaintPoint.setAntiAlias(true);

        mTimerTask = new DrawTimerTask(this);
        mScheduledService = new ScheduledThreadPoolExecutor(1);

        mDegreeTextRect = new Rect();
        mTypeSatisfy = Typeface.createFromAsset(getContext().getAssets(), "Satisfy-Regular.ttf");
        mTypeHelvetica = Typeface.createFromAsset(getContext().getAssets(), "HelveticaNeueLt.ttf");

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "on surfaceCreate");
        mScheduledService.scheduleAtFixedRate(mTimerTask, 0, FRESH_TIME, TimeUnit.MILLISECONDS);
        setSize();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "on surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "on surfaceDestroyed");
        mScheduledService.shutdown();
    }

    /**
     * 测量长度
     */
    private void setSize() {
        int sizeLength = Math.min(getHeight() / 2, getWidth() / 2);
        mBordWith = sizeLength * 0.03f;
        mRadius = (Math.min(getHeight() / 2, getWidth() / 2) - mBordWith / 2);

        Log.e(TAG, "mRadius :" + mRadius);

        mLongDegreeLength = mRadius * 0.10f;
        mShortDegreeLength = mRadius * 0.08f;

        mPointBackLength = mRadius * 0.10f;

        mSecondPointerLength = mRadius * 0.8f;
        mMinutePointerLength = mRadius * 0.6f;
        mHourPointerLength = mRadius * 0.5f;

        mDegreeNumSize = (int) (mRadius * 0.18f);
    }


    private static class DrawTimerTask implements Runnable {
        private WeakReference<ClockPannelView> mViewRef;

        DrawTimerTask(ClockPannelView view) {
            mViewRef = new WeakReference<>(view);
        }

        @Override
        public void run() {
            ClockPannelView clockPannelView = mViewRef.get();
            if (clockPannelView == null) {
                return;
            }
            Canvas canvas = null;
            try {
                long currentTimeMillis = System.currentTimeMillis();
                canvas = clockPannelView.getHolder().lockCanvas(null);
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                clockPannelView.drawPanel(canvas);
                clockPannelView.drawPoint(canvas);


                Log.e(TAG, "cost -->" + (System.currentTimeMillis() - currentTimeMillis));

            } finally {
                clockPannelView.getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
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
            float[] temp = calculatePoint((i + 1) * 30, mSecondPointerLength * 0.98f);
            String text = String.valueOf(i + 1);
            mPaintDegreeNumber.getTextBounds(text, 0, text.length(), mDegreeTextRect);

            canvas.drawText(text, temp[2], temp[3] + Math.abs(mDegreeTextRect.bottom - mDegreeTextRect.top) / 2, mPaintDegreeNumber);
        }

        mPaintDegreeNumber.setTypeface(mTypeSatisfy);
        String tips = "Designed by Dean";
        mPaintDegreeNumber.setTextSize(mDegreeNumSize / 2);
        mPaintDegreeNumber.getTextBounds(tips, 0, tips.length(), mDegreeTextRect);
        canvas.drawText(tips, 0, getHeight() / 4 + Math.abs(mDegreeTextRect.bottom - mDegreeTextRect.top) / 2, mPaintDegreeNumber);
    }


    /**
     * 画指针
     */
    private void drawPoint(Canvas canvas) {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millisecond = now.get(Calendar.MILLISECOND);

        int secondMill = second * 1000 + millisecond;
        int minuteMill = minute * 60 * 1000 + secondMill;
        int hourMill = hour * 60 * 60 * 1000 + minuteMill;

        float[] secondPoints = calculatePoint(secondMill / (60 * 1000f) * 360, mSecondPointerLength);
        float[] minutePoints = calculatePoint(minuteMill / (60 * 60 * 1000f) * 360, mMinutePointerLength);

        float[] hourPoints = calculatePoint(hourMill / (12 * 60 * 60 * 1000f) * 360, mHourPointerLength);

        mPaintPoint.setStrokeWidth(getStrokeWidth(13));
        canvas.drawLine(hourPoints[0], hourPoints[1], hourPoints[2], hourPoints[3], mPaintPoint);
        mPaintPoint.setStrokeWidth(getStrokeWidth(10));
        canvas.drawLine(minutePoints[0], minutePoints[1], minutePoints[2], minutePoints[3], mPaintPoint);
        mPaintPoint.setStrokeWidth(getStrokeWidth(5));
        canvas.drawLine(secondPoints[0], secondPoints[1], secondPoints[2], secondPoints[3], mPaintPoint);

        //画圆心
        mPaintCircle.setColor(Color.WHITE);
        canvas.drawCircle(0, 0, getStrokeWidth(4), mPaintCircle);
    }


    private int getStrokeWidth(int width) {
        float factor = mRadius * 0.005f;
        return (int) (factor * width);
    }

    /**
     * 根据角度和长度计算线段的起点和终点的坐标
     */
    private float[] calculatePoint(float angle, float length) {
        float[] points = new float[4];
        if (angle <= 90f) {
            points[0] = -(float) Math.sin(angle * Math.PI / 180) * mPointBackLength;
            points[1] = (float) Math.cos(angle * Math.PI / 180) * mPointBackLength;
            points[2] = (float) Math.sin(angle * Math.PI / 180) * length;
            points[3] = -(float) Math.cos(angle * Math.PI / 180) * length;
        } else if (angle <= 180f) {
            points[0] = -(float) Math.cos((angle - 90) * Math.PI / 180) * mPointBackLength;
            points[1] = -(float) Math.sin((angle - 90) * Math.PI / 180) * mPointBackLength;
            points[2] = (float) Math.cos((angle - 90) * Math.PI / 180) * length;
            points[3] = (float) Math.sin((angle - 90) * Math.PI / 180) * length;
        } else if (angle <= 270f) {
            points[0] = (float) Math.sin((angle - 180) * Math.PI / 180) * mPointBackLength;
            points[1] = -(float) Math.cos((angle - 180) * Math.PI / 180) * mPointBackLength;
            points[2] = -(float) Math.sin((angle - 180) * Math.PI / 180) * length;
            points[3] = (float) Math.cos((angle - 180) * Math.PI / 180) * length;
        } else if (angle <= 360f) {
            points[0] = (float) Math.cos((angle - 270) * Math.PI / 180) * mPointBackLength;
            points[1] = (float) Math.sin((angle - 270) * Math.PI / 180) * mPointBackLength;
            points[2] = -(float) Math.cos((angle - 270) * Math.PI / 180) * length;
            points[3] = -(float) Math.sin((angle - 270) * Math.PI / 180) * length;
        }
        return points;
    }

}
