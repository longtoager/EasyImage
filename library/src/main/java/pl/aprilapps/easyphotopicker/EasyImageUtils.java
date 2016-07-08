package pl.aprilapps.easyphotopicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import logutils.LogUtil;

/**
 * Created by longtaoge on 2016/7/8.
 */
public class EasyImageUtils {

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public static File saveCutBitmapForCache2(Context context, Bitmap bitmap) {
        if (Environment.getExternalStorageDirectory()==null){

            Toast.makeText(context,"请检查您的内存卡是否存在！",Toast.LENGTH_LONG).show();
            return null;
        }


        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + UUID.randomUUID().toString() + ".jpg");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;

    }


    public static Bitmap getCameraBitmap(File photoFile) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 2;

        int degree = EasyImageUtils.readPictureDegree(photoFile.getAbsolutePath());
        Bitmap cameraBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bitmapOptions);

        cameraBitmap = EasyImageUtils.rotaingImageView(degree, cameraBitmap);


        LogUtil.i(EasyImage.class.getSimpleName(), photoFile.getAbsolutePath() + "__" + photoFile.length());

        return cameraBitmap;
    }


    public static File getNewFile(Activity activity, File photoFile) {
        Bitmap cameraBitmap = EasyImageUtils.getCameraBitmap(photoFile);
        return EasyImageUtils.saveCutBitmapForCache2(activity, cameraBitmap);
    }


}
