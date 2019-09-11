package com.smart.install.app

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

/**
 * @ProjectName: huike-install-app
 * @Package: com.smart.install.app
 * @ClassName: InstallAppActivity
 * @Description: java类作用描述
 * @Author: 谢文良
 * @CreateDate: 2019/9/10 10:13
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/9/10 10:13
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
class InstallAppActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val TYPE = "type"
    }

    private var type = 0
    private var tips: TextView? = null
    private var dialog: AlertDialog? = null
    private var progressBar: ProgressDialog? = null
    private var broadReceiver: BroadcastReceiver? = null
    private val handler = Handler(Looper.getMainLooper(), Handler.Callback { message ->
        when (message.what) {
            1 -> {
                initChanceApp()
            }
            2 -> {
                initDebugApp()
            }
            3 -> {
                initReleaseApp()
            }
        }
        true
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let {
            type = it.getIntExtra(TYPE, 0)
        }
        if (type != 1 && type != 2) {
            finish()
        } else {
            initView()
        }
    }

    private fun initView() {
        setContentView(R.layout.install_layout)
        tips = findViewById(R.id.tv_tips)
        findViewById<View>(R.id.id_default).setOnClickListener(this)
        findViewById<View>(R.id.id_chance).setOnClickListener(this)
        findViewById<View>(R.id.id_debug).setOnClickListener(this)
        findViewById<View>(R.id.id_release).setOnClickListener(this)
        if (getPackInfo("com.smart.gc.v2") != null
            || getPackInfo("com.smart.gc.v3") != null
        ) {
            findViewById<View>(R.id.id_default).visibility = View.VISIBLE
        }
        registerBroadCast()
    }

    override fun onResume() {
        super.onResume()
        resetData()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProcess()
        dismissDialog()
        broadReceiver?.let {
            unregisterReceiver(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun resetData() {
        tips?.let {
            val info = getPackInfo("com.smart.gc.box")
            info?.let { ifo ->
                it.text = "已安装应用：" + ifo.applicationInfo.loadLabel(packageManager).toString() +
                        "，版本: " + ifo.versionName
            } ?: let { _ ->
                it.text = "当前未安装智能回收应用"
            }
        }
    }

    private fun registerBroadCast() {
        broadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                resetData()
                if ("android.intent.action.PACKAGE_ADDED" == intent?.action) {
                    dismissProcess()
                    showDialog("安装成功")
                }
            }

        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED")
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED")
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED")
        intentFilter.addDataScheme("package")
        registerReceiver(broadReceiver, intentFilter)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.id_default) {
            uninstall()
            return
        }
        RootCmd.execRootCmd("pm uninstall com.smart.gc.box")
        showProcess("正在安装...")
        when (v?.id) {
            R.id.id_chance -> {
                handler.sendEmptyMessageDelayed(1, 3000)
            }
            R.id.id_debug -> {
                handler.sendEmptyMessageDelayed(2, 3000)
            }
            R.id.id_release -> {
                handler.sendEmptyMessageDelayed(3, 3000)
            }
        }
    }


    private fun getPackInfo(packageName: String): PackageInfo? {
        val infoList = packageManager.getInstalledPackages(0)
        for (i in infoList.indices) {
            if (infoList[i].packageName.equals(packageName, ignoreCase = true))
                return infoList[i]
        }
        return null
    }

    private fun showProcess(message: String) {
        if (!isFinishing) {
            progressBar = ProgressDialog.show(this, "提示", message)
        }
    }

    private fun dismissProcess() {
        progressBar?.dismiss()
        progressBar = null
    }

    private fun showDialog(message: String) {
        if (!isFinishing) {
            dialog?.let {
                it.show()
                it.setMessage(message)
            } ?: let {
                dialog = AlertDialog.Builder(this).setTitle("提示").setMessage(message)
                    .setPositiveButton("确定") { _, _ ->
                        dialog?.dismiss()
                        dialog = null
                    }.create()
                dialog?.show()
            }
        }
    }

    private fun dismissDialog() {
        dialog?.dismiss()
    }

    private fun initChanceApp() {
        initChanceApp("Changche")
    }

    private fun initDebugApp() {
        initChanceApp("Debug")
    }

    private fun initReleaseApp() {
        initChanceApp("Release")
    }

    private fun initChanceApp(typeName: String) {
        Utils.cacheThreadExecutor.execute {
            ((when (type) {
                1 -> "portcan"
                2 -> "serialport"
                else -> null
            })?.let {
                val files = assets.list(it)
                var value: String? = null
                if (files != null && files.isNotEmpty()) {
                    for (str in files) {
                        if (str.indexOf(typeName) > 0) {
                            value = it + File.separator + str
                            break
                        }
                    }
                }
                value
            } ?: let {
                null
            })?.let {
                val file =
                    File(
                        Environment.getExternalStorageDirectory().absolutePath,
                        "smart_$typeName.apk"
                    )
                // 找到安装文件it
                FileUtils.downFile(assets.open(it), file)
                if (file.exists()) {
                    RootCmd.execRootCmd("pm install -r -g " + file.absolutePath)
                }
            } ?: let {
                handler.post {
                    dismissProcess()
                    showDialog("未找到安装文件")
                }
            }
        }
    }

    private fun uninstall() {
        showProcess("正在卸载...")
        Utils.cacheThreadExecutor.execute {
            RootCmd.execRootCmd("mount -o rw,remount /system")
            Thread.sleep(1000)
            RootCmd.execRootCmd("chmod 777 /system")
            Thread.sleep(1000)
            val values = ArrayList<String>()
            if (type == 1) {
                values.addAll(getHkValues())
            } else if (type == 2) {
                values.addAll(getXhgValues())
            }
            for (item in values) {
                RootCmd.execRootCmd("rm -rf $item")
                Thread.sleep(2000)
            }
            var isDelete = true
            for (item in values) {
                isDelete = isDelete && !File(item).exists()
            }
            handler.post {
                dismissProcess()
                if (!isDelete) {
                    showDialog("卸载失败")
                } else {
                    showReboot()
                }
            }
        }
    }

    private fun showReboot() {
        dialog = AlertDialog.Builder(this).setTitle("提示").setMessage("卸载成功，系统将重启")
            .setPositiveButton("确定") { _, _ ->
                dialog?.dismiss()
                dialog = null
                RootCmd.execRootCmd("reboot")
            }.create()
        if (!isFinishing) {
            dialog?.show()
        }
    }


    private fun getHkValues(): List<String> {
        val fl = File("/system/priv-app")
        val values = ArrayList<String>()
        for (item in fl.list()) {
            if (item.startsWith("SmartGc")) {
                values.add("/system/priv-app/$item")
            }
        }
        return values
    }

    private fun getXhgValues(): List<String> {
        val fl = File("/system/app")
        val values = ArrayList<String>()
        for (item in fl.list()) {
            if (item.startsWith("SmartGc")) {
                values.add("/system/app/$item")
            }
        }
        return values
    }
}
