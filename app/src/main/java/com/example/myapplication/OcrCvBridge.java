package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;


import androidx.appcompat.app.AppCompatActivity;

import com.library.ocr.OcrVcOpenApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OcrCvBridge {

//    public static OcrCvBridge getInstance() {
//        if (sInstance == null) {
//            synchronized (OcrCvBridge.class) {
//                if (sInstance == null) {
//                    sInstance = new OcrCvBridge();
//                }
//            }
//        }
//        return sInstance;
//    }
//    public OcrCvBridge addInterrupt(IOcrInterrupt iOcrInterrupt) {
//        return this;
//    }
//    public interface IOcrInterrupt {
//        void onCreate(AppCompatActivity activity);
//    }
//    public interface IOcrListener {
//        void onOcrPositiveRecognized(String filePath, String filePathHead, String code, String name);
//        void onOcrBackRecognized(String filePath, String issue, String period);
//    }


        /**
     * 移除OCR模块，注释这边即可
     * @return
     */
    public static OcrVcOpenApi getInstance() {
        return OcrVcOpenApi.getInstance();
    }
    public OcrVcOpenApi addInterrupt(IOcrInterrupt iOcrInterrupt) {
        return OcrVcOpenApi.getInstance().addInterrupt(iOcrInterrupt);
    }

    public interface IOcrListener extends OcrVcOpenApi.IOCRCallback {
    }

    public interface IOcrInterrupt extends OcrVcOpenApi.IActivityInterrupt {
    }



    private volatile static OcrCvBridge sInstance;

    private OcrCvBridge() {
        if (sInstance != null) {
            throw new RuntimeException("请注意使用规范");
        }
    }

    public String startEngine(MainActivity mainActivity, IOcrListener iOcrListener) {
        return null;
    }

    public String startUp(Context context) {
        return null;
    }

    private boolean isEngineStart;


    public void release() {
    }


    public boolean isPeriodExpired(String period) {
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
    public boolean isBackRight(String issue, String period) {
        return !TextUtils.isEmpty(issue) && !TextUtils.isEmpty(period);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return true;
    }


    public boolean isEngineStart() {
        return isEngineStart;
    }

    public void openCamera(MainActivity mainActivity, boolean b, boolean b1) {

    }

}
