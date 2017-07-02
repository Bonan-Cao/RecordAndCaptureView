package tusdk.bonan.recordandcaptureview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.nfc.Tag;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by bonan on 24/06/2017.
 */

public class CaptureButton extends View {
    private static final String TAG = "linyan-- ";
    float centerX, centerY;
    int screenWidth;
    Paint mPaint;
    CameraEventListener mListener;

    public CaptureButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        initPaints();

        //get width
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;

        Log.d(TAG, "screenWidth= " + screenWidth);
    }

    public CaptureButton(Context context, int size) {
        this(context, null);

    }

    private void initPaints() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 外圈
        mPaint.setColor(0xFFFFFFFF);
        canvas.drawCircle(screenWidth / 2, 100, 80, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
                break;

            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");

                if(mListener == null) return super.onTouchEvent(event);

                mListener.onCapturePhotos();
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "ACTION_MOVE");
                break;
        }

        return true;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public void setOnCameraEventListener(CameraEventListener listener) {
        mListener = listener;
    }
}
