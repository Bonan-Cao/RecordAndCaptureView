package tusdk.bonan.recordandcaptureview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

/**
 * Created by bonan on 2017/6/13.
 */

public class RecordCaptureView extends RelativeLayout implements SurfaceHolder.Callback, CameraHelper.CameraHelperDelegate {
    private static final String TAG = "linyan-- ";
    Button mToggleBtn, mCaptureBtn, mRecordBtn;
    private VideoView mVideoView;
    private Context mContext;
    private ImageView mImageView;
    Camera.PictureCallback mPictureCallBack = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, final Camera camera) {

            int degree = CameraHelper.getCameraDisplayOrientation((Activity) mContext) + 180;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            bitmap = rotate(bitmap, degree);
            mImageView.setImageBitmap(bitmap);
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    camera.startPreview();
                    mImageView.setVisibility(View.GONE);
                }
            });

        }
    };

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

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.record_capture_view_layout, this, true);

        mImageView = (ImageView) findViewById(R.id.ly_imageView);
        mVideoView = (VideoView) findViewById(R.id.ly_videoView);
        mVideoView.getHolder().addCallback(this);
        mToggleBtn = (Button) findViewById(R.id.ly_toggle_btn);
        mToggleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                CameraHelper.switchCamera();
            }
        });

        mCaptureBtn = (Button) findViewById(R.id.ly_capture_btn);
        mCaptureBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _takePicture();
            }
        });
    }

    private void _takePicture() {
        CameraHelper._takePicture(mPictureCallBack);
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

                if (CameraHelper.startPreview((Activity) mContext, mVideoView.getHolder())) {
                    Log.d(TAG, "startPreview success");
                } else {
                    Log.w(TAG, "fail to startPreview...");
                }

            }
        }).start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: " + Thread.currentThread().getName());

        if (CameraHelper.startPreview((Activity) mContext, holder)) {
            Log.d(TAG, "surfaceCreated startPreview success");
        } else {
            Log.w(TAG, "surfaceCreated fail to startPreview...");
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