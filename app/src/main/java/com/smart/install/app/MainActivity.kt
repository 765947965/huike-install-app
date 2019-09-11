package com.smart.install.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.tbruyelle.rxpermissions2.RxPermissions

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissions()
    }

    private fun initView() {
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.id_mini).setOnClickListener(this)
        findViewById<View>(R.id.id_lager).setOnClickListener(this)
        val model = android.os.Build.BOARD
        Log.i("cccccccccc", android.os.Build.ID)
        Log.i("cccccccccc", android.os.Build.PRODUCT)
        Log.i("cccccccccc", android.os.Build.DEVICE)
        Log.i("cccccccccc", android.os.Build.BOARD)
        Log.i("cccccccccc", android.os.Build.MANUFACTURER)
        Log.i("cccccccccc", android.os.Build.BRAND)
        Log.i("cccccccccc", android.os.Build.MODEL)
        Log.i("cccccccccc", android.os.Build.BOOTLOADER)
        Log.i("cccccccccc", android.os.Build.HARDWARE)
        if ("3501Q" == model) {
            findViewById<View>(R.id.id_mini).visibility = View.VISIBLE
        } else if ("SABRESD" == model) {
            findViewById<View>(R.id.id_lager).visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.id_mini -> {
                startActivity(
                    Intent(this, InstallAppActivity::class.java)
                        .putExtra(InstallAppActivity.TYPE, 1)
                )
            }
            R.id.id_lager -> {
                startActivity(
                    Intent(this, InstallAppActivity::class.java)
                        .putExtra(InstallAppActivity.TYPE, 2)
                )
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun permissions() {
        RxPermissions(this).requestEachCombined(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        initView()
                        RootCmd.execRootCmd("am force-stop com.smart.gc.v2")
                        RootCmd.execRootCmd("am force-stop com.smart.gc.v3")
                        sendBroadcast(
                            Intent("com.android.addbar").setPackage(
                                "com.android.systemui"
                            )
                        )
                        sendBroadcast(Intent("action.SHOW_STATUSBAR"))
                    }
                    permission.shouldShowRequestPermissionRationale -> {
                        permissions()
                    }
                    else -> {
                        AlertDialog.Builder(this).setTitle("提示")
                            .setMessage("您已拒绝应用必须权限，请到设置中打开应用所需权限!")
                            .setNegativeButton("取消") { _, _ ->
                                finish()
                            }.setNeutralButton("确定") { _, _ ->
                                startActivity(Intent(Settings.ACTION_SETTINGS))
                            }.setCancelable(false).create().show()
                    }
                }
            }
    }
}
