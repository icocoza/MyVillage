package com.ccz.myvillage.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageUtils {

    public static Matrix getMatrix(Context ctx, Uri uri) {
        try {
            ExifInterface exif = new ExifInterface(ctx.getContentResolver().openInputStream(uri));
            return getMatrix(exif);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Matrix getMatrix(String filePath) {
        try {
            ExifInterface exif = new ExifInterface(filePath);
            return getMatrix(exif);
        }catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Matrix getMatrix(ExifInterface exif) {
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = ImageUtils.exifOrientationToDegrees(exifOrientation);
        Matrix matrix = new Matrix();
        matrix.postRotate(exifDegree);
        return matrix;
    }

    public static  Bitmap resizeContent(Context ctx, Uri uri, int maxWidth, int maxHeight) throws FileNotFoundException {
        Matrix matrix = ImageUtils.getMatrix(ctx, uri);
        Bitmap srcBmp = BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri));
        return resizeContent(srcBmp, maxWidth, maxHeight, matrix);
    }

    public static  Bitmap resizeContent(String filePath, int maxWidth, int maxHeight) {
        Matrix matrix = ImageUtils.getMatrix(filePath);
        Bitmap srcBmp = BitmapFactory.decodeFile(filePath);
        return resizeContent(srcBmp, maxWidth, maxHeight, matrix);
    }

    private static  Bitmap resizeContent(Bitmap srcBmp, int maxWidth, int maxHeight, Matrix matrix) {
        int targetW = srcBmp.getWidth();
        int targetH = srcBmp.getHeight();
        float scale = 0f;
        if(targetW > maxWidth&& targetH > maxHeight) {

            if(targetW > targetH)
                scale = (float)maxWidth / (float)targetW;
            else
                scale = (float)maxHeight / (float)targetH;
        }
        matrix.postScale(scale, scale);
        Bitmap bm = Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), srcBmp.getHeight(), matrix, true);
        return bm;
    }

    public static  Bitmap scaleUpContent(Bitmap srcBmp, int maxWidth, int maxHeight) {
        int srcWidth = srcBmp.getWidth();
        int srcHeight = srcBmp.getHeight();
        float scale = 0f;
        if(srcWidth > srcHeight)
            scale = (float)maxWidth / (float)srcWidth;
        else
            scale = (float)maxWidth / (float)srcHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap bm = Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), srcBmp.getHeight(), matrix, true);
        return bm;
    }

    public static int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static Bitmap rotate(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static Bitmap resizePhoto(String photoPath, int targetW, int targetH) {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }
}
