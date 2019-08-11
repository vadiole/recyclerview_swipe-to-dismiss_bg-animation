package c.vadiole.recyclerview;

import android.content.res.Resources;

public class MyUtils {
    public static int dpToPx(double dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return dpToPx((double) (dp));
    }

    public static int pxToDp(double px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int dp) {
        return pxToDp((double) (dp));
    }

    public static int takeDistanse(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}
