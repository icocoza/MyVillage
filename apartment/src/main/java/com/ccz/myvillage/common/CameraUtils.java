package com.ccz.myvillage.common;

import android.app.Activity;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.util.Size;
import android.view.Surface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraUtils {
    private final static int SURFACE_DEGREE_0 = 0;
    private final static int SURFACE_DEGREE_90 = 90;
    private final static int SURFACE_DEGREE_180 = 180;
    private final static int SURFACE_DEGREE_270 = 270;
    private final static int SURFACE_DEGREE_360 = 360;

    public static File getOutputMediaFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        String folderPath = path + File.separator + "MyVillage";
        String filePath = path + File.separator + "MyVillage" + File.separator +   "IMG_"+ timeStamp + ".jpg";
        File fileFolderPath = new File(folderPath);
        fileFolderPath.mkdir();

        return new File(filePath);
    }

    public static int getCameraDisplayOrientation(Activity activity, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = SURFACE_DEGREE_0;
                break;
            case Surface.ROTATION_90:
                degrees = SURFACE_DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = SURFACE_DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = SURFACE_DEGREE_270;
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            int orientation = (info.orientation + degrees) % SURFACE_DEGREE_360;
            return (SURFACE_DEGREE_360 - orientation) % SURFACE_DEGREE_360;
        }
        return (info.orientation - degrees + SURFACE_DEGREE_360) % SURFACE_DEGREE_360;
    }

    public static Camera.Size getOptimalPreviewSize(Camera camera, int w, int h) {
        List<Camera.Size> sizes = getSupportedPictureSizes(camera);
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;
        if (sizes == null) {
            return null;
        }
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private static List<Camera.Size> getSupportedPictureSizes(Camera camera) {
        if (camera == null)
            return null;
        List<Camera.Size> pictureSizes = camera.getParameters().getSupportedPictureSizes();
        checkSupportedPictureSizeAtPreviewSize(camera, pictureSizes);
        return pictureSizes;
    }

    private static void checkSupportedPictureSizeAtPreviewSize(Camera camera, List<Camera.Size> pictureSizes) {
        List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
        Camera.Size pictureSize;
        Camera.Size previewSize;
        double  pictureRatio = 0;
        double  previewRatio = 0;
        final double aspectTolerance = 0.05;
        boolean isUsablePicture = false;

        for (int indexOfPicture = pictureSizes.size() - 1; indexOfPicture >= 0; --indexOfPicture) {
            pictureSize = pictureSizes.get(indexOfPicture);
            pictureRatio = (double) pictureSize.width / (double) pictureSize.height;
            isUsablePicture = false;

            for (int indexOfPreview = previewSizes.size() - 1; indexOfPreview >= 0; --indexOfPreview) {
                previewSize = previewSizes.get(indexOfPreview);

                previewRatio = (double) previewSize.width / (double) previewSize.height;

                if (Math.abs(pictureRatio - previewRatio) < aspectTolerance) {
                    isUsablePicture = true;
                    break;
                }
            }

            if (isUsablePicture == false) {
                pictureSizes.remove(indexOfPicture);
            }
        }
    }

}
