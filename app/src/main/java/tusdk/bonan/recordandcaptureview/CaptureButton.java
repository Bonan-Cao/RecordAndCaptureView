package tusdk.bonan.recordandcaptureview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.nfc.Tag;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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

//        // 内圈
//        mPaint.setColor(0xFFFFFFFF);
//        canvas.drawCircle(screenWidth / 2, 20 + 80 * 0.7f, 80 * 0.7f, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }
}
