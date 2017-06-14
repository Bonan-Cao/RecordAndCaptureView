package tusdk.bonan.recordandcaptureview;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.IOException;
import java.util.List;

/**
 * Created by bonan on 2017/6/13.
 */

public class RecordCaptureView extends RelativeLayout implements SurfaceHolder.Callback {
    private static final String TAG = "linyan-- ";
    private VideoView mVideoView;
    private Camera mCamera;
    private Context mContext;

    public RecordCaptureView(Context context) {
        this(context, null);
    }

    public RecordCaptureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordCaptureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.record_capture_view_layout, this, true);

        mVideoView = (VideoView) findViewById(R.id.ly_videoView);
        mVideoView.getHolder().addCallback(this);

        initCamera();
    }

    private void initCamera() {
        mCamera = Camera.open();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");

        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");

        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mVideoView.getHolder().getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        Camera.Parameters parameters = mCamera.getParameters();

        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> supportedVideoSizes = parameters.getSupportedVideoSizes();
        Camera.Size optimizePreviewSize = CameraHelper.getOptimizePreviewSize(supportedPreviewSizes, supportedVideoSizes,
                width, height);
        parameters.setPreviewSize(optimizePreviewSize.width, optimizePreviewSize.height);

        CameraHelper.setCameraDisplayOrientation((Activity) mContext, 0, mCamera);
        mCamera.setParameters(parameters);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mVideoView.getHolder());
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        // empty. Take care of releasing the Camera preview in your activity.
    }
}
