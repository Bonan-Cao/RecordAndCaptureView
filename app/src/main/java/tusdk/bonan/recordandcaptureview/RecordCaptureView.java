package tusdk.bonan.recordandcaptureview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.VideoView;

/**
 * Created by bonan on 2017/6/13.
 */

public class RecordCaptureView extends RelativeLayout implements SurfaceHolder.Callback, CameraHelper.CameraHelperDelegate {
    private static final String TAG = "linyan-- ";
    Button mToggleBtn;
    private VideoView mVideoView;
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
        mToggleBtn = (Button) findViewById(R.id.ly_toggle_btn);
        mToggleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                CameraHelper.toggleCamera();
            }
        });
    }

    public void onResume() {
        CameraHelper.openCamera(this);

    }

    @Override
    public void onOpenCameraSuccess() {

        Log.d(TAG, "onOpenCameraSuccess");
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (CameraHelper.prepareCameraAndRecorder((Activity) mContext, mVideoView.getHolder())) {
                    Log.d(TAG, "prepareCameraAndRecorder success");
                } else {
                    Log.w(TAG, "fail to prepareCameraAndRecorder...");
                }

            }
        }).start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: " + Thread.currentThread().getName());

        if (CameraHelper.prepareCameraAndRecorder((Activity) mContext, holder)) {
            Log.d(TAG, "surfaceCreated prepareCameraAndRecorder success");
        } else {
            Log.w(TAG, "surfaceCreated fail to prepareCameraAndRecorder...");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");

        CameraHelper.destroyCameraAndRecorder();
    }
}