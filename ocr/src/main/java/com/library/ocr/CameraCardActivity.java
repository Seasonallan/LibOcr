package com.library.ocr;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;


import androidx.annotation.NonNull;

import com.turui.android.activity.WCameraActivity;
import com.turui.android.cameraview.FinderView;
import com.turui.android.cameraview.R;

public class CameraCardActivity extends WCameraActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //LanguageUtil.setLocale(this);
        //Density.enableUIAdapt(this);
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解决华为8.1.0版本的横屏异常
        if (android.os.Build.VERSION.SDK_INT >= 27) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    protected int getCustomContentView() {
        return R.layout.activity_auth_id_camera;
    }

    @Override
    protected FinderView setTipText(FinderView finderView) {
        //LanguageUtil.setLocale(this);
        //finderView.setTipText(getResources().getString(R.string.tip_text));
        return finderView;
    }


}
