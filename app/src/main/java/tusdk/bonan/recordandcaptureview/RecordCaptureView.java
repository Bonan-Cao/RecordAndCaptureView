package tusdk.bonan.recordandcaptureview;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.IOException;

/**
 * Created by bonan on 2017/6/13.
 */

public class RecordCaptureView extends RelativeLayout implements SurfaceHolder.Callback {
    private static final String TAG = "linyan-- ";
    private VideoView mVideoView;
    private Camera mCamera;

    public RecordCaptureView(Context context) {
        this(context, null);
    }

    public RecordCaptureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordCaptureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.record_capture_view_layout, this, true);

        mVideoView = (VideoView) findViewById(R.id.ly_videoView);
        mVideoView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("linyan-- ", "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
        mCamera = Camera.open();
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
    }
}
