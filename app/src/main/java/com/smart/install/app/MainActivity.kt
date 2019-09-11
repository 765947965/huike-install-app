package com.smart.install.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.tbruyelle.rxpermissions2.RxPermissions

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissions()
    }

    private fun initView(){
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.id_mini).setOnClickListener(this)
        findViewById<View>(R.id.id_lager).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.id_mini -> {
                startActivity(Intent(this, InstallAppActivity::class.java)
                    .putExtra(InstallAppActivity.TYPE, 1))
            }
            R.id.id_lager -> {
                startActivity(Intent(this, InstallAppActivity::class.java)
                    .putExtra(InstallAppActivity.TYPE, 2))
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun permissions() {
        RxPermissions(this).requestEachCombined(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        initView()
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
