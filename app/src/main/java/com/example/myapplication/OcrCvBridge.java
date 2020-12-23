package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.library.ocr.OcrVcOpenApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OcrCvBridge {


    private volatile static OcrCvBridge sInstance;


    private OcrCvBridge() {
        if (sInstance != null) {
            throw new RuntimeException("请注意使用规范");
        }
    }

    public String startUp(Context context) {
        return null;
    }

    private boolean isEngineStart;


    public void release() {
    }

    public String openCamera(Activity activity, int code) {
        return null;
    }


    public String code, name;
    public String issue, period;
    public Bitmap copySmallBitmap, copyTakeBitmap;

    public boolean isPeriodExpired() {
        try {
            String endTime = period.split("-")[1];
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
            Date endDate = dateFormat.parse(endTime);
            return endDate.getTime() <= System.currentTimeMillis();
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * 背面是否正确
     *
     * @return
     */
    public boolean isBackRight() {
        return !TextUtils.isEmpty(issue) && !TextUtils.isEmpty(period);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return true;
    }


//    public static OcrVcOpenApiBridge getInstance() {
//        if (sInstance == null) {
//            synchronized (OcrVcOpenApiBridge.class) {
//                if (sInstance == null) {
//                    sInstance = new OcrVcOpenApiBridge();
//                }
//            }
//        }
//        return sInstance;
//    }

    public static OcrVcOpenApi getInstance() {
        return OcrVcOpenApi.getInstance();
    }

}
