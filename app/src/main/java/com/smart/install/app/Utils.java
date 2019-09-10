package com.smart.install.app;

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
}
