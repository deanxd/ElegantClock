package com.deanxd.elegantclock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.deanxd.elegantclock.util.CommonUtils;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 表盘指针
 *
 * @author Dean
 */

public class PointerView extends SurfaceView implements SurfaceHolder.Callback {

    private final static String TAG = "clockPannelView";

    private final static long FRESH_TIME = 1000 / 60;

    /**
     * View默认最小宽度
     */
    private float mDefaultWidth;

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
    private Paint mPaintCircle;
    private float mRadius;
    private Paint mPointerPaint;
    private DrawTimerTask mTimerTask;
    private ScheduledExecutorService mScheduledService;

    public PointerView(Context context) {
        super(context);
        init();
    }

    public PointerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PointerView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        mPointerPaint = new Paint();
        mPointerPaint.setColor(Color.BLACK);
        mPointerPaint.setAntiAlias(true);

        mTimerTask = new DrawTimerTask(this);

        mScheduledService = new ScheduledThreadPoolExecutor(1);
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

        float mBordWith = sizeLength * 0.03f;
        mRadius = (Math.min(getHeight() / 2, getWidth() / 2) - mBordWith / 2);

        mPointBackLength = mRadius * 0.10f;
        mSecondPointerLength = mRadius * 0.8f;
        mMinutePointerLength = mRadius * 0.6f;
        mHourPointerLength = mRadius * 0.5f;
    }


    private static class DrawTimerTask implements Runnable {
        private WeakReference<PointerView> mViewRef;

        DrawTimerTask(PointerView view) {
            mViewRef = new WeakReference<>(view);
        }

        @Override
        public void run() {
            PointerView pointerView = mViewRef.get();
            if (pointerView == null) {
                return;
            }
            Canvas canvas = null;
            try {
                long currentTimeMillis = System.currentTimeMillis();
                canvas = pointerView.getHolder().lockCanvas(null);
                canvas.translate(pointerView.getWidth() / 2, pointerView.getHeight() / 2);
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                pointerView.drawPointer(canvas);

                Log.e(TAG, "cost -->" + (System.currentTimeMillis() - currentTimeMillis));
            } finally {
                pointerView.getHolder().unlockCanvasAndPost(canvas);
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
     * 画指针
     */
    private void drawPointer(Canvas canvas) {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millisecond = now.get(Calendar.MILLISECOND);

        int secondMill = second * 1000 + millisecond;
        int minuteMill = minute * 60 * 1000 + secondMill;
        int hourMill = hour * 60 * 60 * 1000 + minuteMill;

        float[] secondPoints = CommonUtils.calculatePoint(secondMill / (60 * 1000f) * 360, mSecondPointerLength, mPointBackLength);
        float[] minutePoints = CommonUtils.calculatePoint(minuteMill / (60 * 60 * 1000f) * 360, mMinutePointerLength, mPointBackLength);
        float[] hourPoints = CommonUtils.calculatePoint(hourMill / (12 * 60 * 60 * 1000f) * 360, mHourPointerLength, mPointBackLength);

        mPointerPaint.setStrokeWidth(getStrokeWidth(13));
        canvas.drawLine(hourPoints[0], hourPoints[1], hourPoints[2], hourPoints[3], mPointerPaint);
        mPointerPaint.setStrokeWidth(getStrokeWidth(10));
        canvas.drawLine(minutePoints[0], minutePoints[1], minutePoints[2], minutePoints[3], mPointerPaint);
        mPointerPaint.setStrokeWidth(getStrokeWidth(5));
        canvas.drawLine(secondPoints[0], secondPoints[1], secondPoints[2], secondPoints[3], mPointerPaint);

        //画圆心
        mPaintCircle.setColor(Color.WHITE);
        canvas.drawCircle(0, 0, getStrokeWidth(4), mPaintCircle);
    }


    private int getStrokeWidth(int width) {
        float factor = mRadius * 0.005f;
        return (int) (factor * width);
    }


}