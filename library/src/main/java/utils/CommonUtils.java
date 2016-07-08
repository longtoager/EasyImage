
package utils;

import android.content.Context;
import android.content.pm.PackageManager;


public class CommonUtils {
    /**
     * 防止重复点击
     */
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 检测相机是否存在 s
     */
    public static boolean isExistCamera(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }
    


}
