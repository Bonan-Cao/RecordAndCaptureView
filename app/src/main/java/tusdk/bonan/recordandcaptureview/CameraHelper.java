package tusdk.bonan.recordandcaptureview;


import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.R.attr.height;
import static android.R.attr.width;

/**
 * Created by bonan on 14/06/2017.
 */

public class CameraHelper {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String TAG = "linyan-- ";
    static MediaRecorder mMediaRecord;
    private static int CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private static Camera mCamera;
    private static boolean isRecording;
    private static boolean isPreviewing = false;
    private static Activity mActivity;
    private static SurfaceHolder mHolder;

    /**
     * 从 support video size 中找到最匹配预览视图宽高并且跟宽高比例最相似的,
     * <p>
     * 如果找不到,则忽略比例这一条件
     *
     * @param previewSizes
     * @param videoSizes
     * @param width
     * @param height
     * @return
     */
    public static Camera.Size getOptimizePreviewSize(List<Camera.Size> previewSizes,
                                                     List<Camera.Size> videoSizes,
                                                     int width, int height) {
        double targetRatio = (double) width / height;

        final double ASPECT_TOLERANCE = 0.1;

        double min_diff = Double.MAX_VALUE;

        Camera.Size optimizeSize = null;

        List<Camera.Size> _videoSize;
        if (videoSizes == null) {
            _videoSize = videoSizes;
        } else {
            _videoSize = previewSizes;
        }

        for (Camera.Size size : _videoSize) {

            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - height) < min_diff && previewSizes.contains(size)) {
                optimizeSize = size;
                min_diff = Math.abs(size.height - height);
            }
        }

        if (optimizeSize != null) {
            Log.d(TAG, "getOptimizePreviewSize() 1: " + optimizeSize.width + "x" + optimizeSize.height);

            return optimizeSize;
        }

        min_diff = Double.MAX_VALUE;

        for (Camera.Size size : _videoSize) {

            if (Math.abs(size.height - height) < min_diff && previewSizes.contains(size)) {
                optimizeSize = size;
                min_diff = Math.abs(size.height - height);
            }
        }

        Log.d(TAG, "getOptimizePreviewSize() 2: " + optimizeSize.width + "x" + optimizeSize.height);
        return optimizeSize;
    }

    static int getCameraDisplayOrientation(Activity activity) {

        if (mCamera == null) return 0;

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();

        android.hardware.Camera.getCameraInfo(CAMERA_ID, info);

        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();

        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        Log.d(TAG, "getCameraDisplayOrientation() result= " + result);
        return result;
    }

    /**
     * Creates a media file in the {@code Environment.DIRECTORY_PICTURES} directory. The directory
     * is persistent and available to other applications like gallery.
     *
     * @param type Media type. Can be video or image.
     * @return A file object pointing to the newly created file.
     */
    static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraSample");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    static boolean toggleRecorder() {

        if (!isRecording) {
            mMediaRecord.start();
            isRecording = true;
        } else {
            mMediaRecord.stop();
            mCamera.lock();
            destroyCameraAndRecorder();
            isRecording = false;
        }

        return isRecording;
    }

    static void switchCamera() {
        if (isPreviewing) {
            mCamera.stopPreview();
        }

        releaseCamera();

        if (CAMERA_ID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }

        mCamera = Camera.open(CAMERA_ID);
        startPreview(mActivity, mHolder);
    }

    static void destroyCameraAndRecorder() {
        releaseMediaRecorder();
        releaseCamera();
    }

    static void openCamera(CameraHelperDelegate delegate) {

        Log.d(TAG, "openCamera");

        mCamera = Camera.open(CAMERA_ID);

        if (mCamera != null && delegate != null) {
            delegate.onOpenCameraSuccess();
        }
    }

    static boolean startPreview(Activity activity, SurfaceHolder holder) {

        if (mCamera == null) return false;

        if (isPreviewing) {
            Log.d(TAG, "camra is still previewing...");
            mCamera.stopPreview();
            return false;
        }

        mActivity = activity;
        mHolder = holder;

        Log.d(TAG, "isPreviewing === false");
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        Camera.Parameters parameters = mCamera.getParameters();

        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> supportedVideoSizes = parameters.getSupportedVideoSizes();
        Camera.Size optimizePreviewSize = CameraHelper.getOptimizePreviewSize(supportedPreviewSizes, supportedVideoSizes,
                width, height);
        parameters.setPreviewSize(optimizePreviewSize.width, optimizePreviewSize.height);

        profile.videoFrameWidth = optimizePreviewSize.width;
        profile.videoFrameHeight = optimizePreviewSize.height;

        Log.d(TAG, "previewSize= " + optimizePreviewSize.width
                + " x "
                + optimizePreviewSize.height);

        mCamera.setParameters(parameters);

        // start preview with new settings
        try {

            mCamera.setDisplayOrientation(getCameraDisplayOrientation(activity));

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            isPreviewing = true;
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            return false;
        }

        Log.d(TAG, "after mCamera.startPreview();");


//        if(mMediaRecord != null) return true;
//
//        mMediaRecord = new MediaRecorder();
//
//        mCamera.unlock();
//        mMediaRecord.setCamera(mCamera);
//
//        mMediaRecord.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//        mMediaRecord.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//
////        mMediaRecord.setProfile(profile);
//
//        mMediaRecord.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mMediaRecord.setOutputFile(
//                CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO).getPath());
//
//        try {
//            mMediaRecord.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }

        return true;
    }

    static void _takePicture(Camera.PictureCallback callback) {
        mCamera.takePicture(null, null, callback);
    }

    private static void releaseMediaRecorder() {
        if (mMediaRecord != null) {
            // clear recorder configuration
            mMediaRecord.reset();
            // release the recorder object
            mMediaRecord.release();
            mMediaRecord = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            mCamera.lock();
        }
    }

    private static void releaseCamera() {
        if (mCamera != null) {
            // release the camera for other applications
            mCamera.release();
            mCamera = null;
            isPreviewing = false;
        }
    }

    interface CameraHelperDelegate {
        void onOpenCameraSuccess();
    }
}
