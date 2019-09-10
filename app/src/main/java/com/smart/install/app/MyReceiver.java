package com.smart.install.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @ProjectName: huike-install-app
 * @Package: com.smart.install.app
 * @ClassName: MyReceiver
 * @Description: java类作用描述
 * @Author: 谢文良
 * @CreateDate: 2019/9/10 17:37
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/9/10 17:37
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
