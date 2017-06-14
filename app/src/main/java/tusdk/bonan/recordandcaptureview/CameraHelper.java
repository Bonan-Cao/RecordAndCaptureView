package tusdk.bonan.recordandcaptureview;


import android.app.Activity;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by bonan on 14/06/2017.
 */

public class CameraHelper {

    private static final String TAG = "linyan-- ";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

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

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();

        android.hardware.Camera.getCameraInfo(cameraId, info);

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
        camera.setDisplayOrientation(result);
    }


    /**
     * Creates a media file in the {@code Environment.DIRECTORY_PICTURES} directory. The directory
     * is persistent and available to other applications like gallery.
     *
     * @param type Media type. Can be video or image.
     * @return A file object pointing to the newly created file.
     */
    public  static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return  null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraSample");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

}
