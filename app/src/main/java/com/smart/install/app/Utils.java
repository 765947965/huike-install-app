package com.smart.install.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ProjectName: huike-install-app
 * @Package: com.smart.install.app
 * @ClassName: Utils
 * @Description: java类作用描述
 * @Author: 谢文良
 * @CreateDate: 2019/9/10 14:03
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/9/10 14:03
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class Utils {
    /*** 缓存线程池，用来执行生命周期短暂的任务 ***/
    public static final ExecutorService cacheThreadExecutor = Executors.newCachedThreadPool();

    /*** 获取屏幕尺寸 ***/
    public static double getPhysicsScreenSize(Activity context){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        manager.getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int densityDpi = dm.densityDpi;//得到屏幕的密度值，但是该密度值只能作为参考，因为他是固定的几个密度值。
        double x = Math.pow(point.x / dm.xdpi, 2);//dm.xdpi是屏幕x方向的真实密度值，比上面的densityDpi真实。
        double y = Math.pow(point.y / dm.ydpi, 2);//dm.xdpi是屏幕y方向的真实密度值，比上面的densityDpi真实。
        return Math.sqrt(x + y);
    }
}
