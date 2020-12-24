package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.library.ocr.OcrVcOpenApi;

public class MainActivity extends AppCompatActivity {


    ImageView imageViewPicture, imageViewHead;
    TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setTitle("功能选择");

        imageViewPicture = findViewById(R.id.result_iv_picture);
        imageViewHead = findViewById(R.id.result_iv_head);
        textViewResult = findViewById(R.id.result_tv);

        findViewById(R.id.list_btn_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = OcrCvBridge.getInstance().addInterrupt(new OcrVcOpenApi.IActivityInterrupt() {
                    @Override
                    public void onCreate(AppCompatActivity activity) {
                        //多语言，界面适配，如：
                        //LanguageUtil.setLocale(this);
                        //Density.enableUIAdapt(this);
                    }
                }).startEngine(MainActivity.this, new OcrVcOpenApi.IOCRCallback() {
                    @Override
                    public void onOcrPositiveRecognized(String filePath, String filePathHead, String code, String name) {
                        textViewResult.setText("name:" + name
                                + "\ncode:" + code
                                + "\nfilePath:" + filePath
                                + "\nfilePathHead:" + filePathHead);
                        imageViewPicture.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        imageViewHead.setImageBitmap(BitmapFactory.decodeFile(filePathHead));
                    }

                    @Override
                    public void onOcrBackRecognized(String filePath, String issue, String period) {
                        textViewResult.setText("issue:" + issue
                                + "\nperiod:" + period
                                + "\nfilePath:" + filePath
                                + "\n是否是国徽面:" + OcrCvBridge.getInstance().isBackRight(issue, period)
                                + "\n有效期是否过期:" + OcrCvBridge.getInstance().isPeriodExpired(period));
                        imageViewPicture.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        imageViewHead.setImageBitmap(null);
                    }
                });
                if (!TextUtils.isEmpty(result)) {
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.list_btn_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!OcrCvBridge.getInstance().isEngineStart()) {
                    Toast.makeText(MainActivity.this, "引擎未启动", Toast.LENGTH_SHORT).show();
                    return;
                }

                OcrCvBridge.getInstance().openCamera(MainActivity.this, true, true);

            }
        });
        findViewById(R.id.list_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!OcrCvBridge.getInstance().isEngineStart()) {
                    Toast.makeText(MainActivity.this, "引擎未启动", Toast.LENGTH_SHORT).show();
                    return;
                }

                OcrCvBridge.getInstance().openCamera(MainActivity.this, true, false);

            }
        });
        findViewById(R.id.list_btn_ocr_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!OcrCvBridge.getInstance().isEngineStart()) {
                    Toast.makeText(MainActivity.this, "引擎未启动", Toast.LENGTH_SHORT).show();
                    return;
                }

                OcrCvBridge.getInstance().openCamera(MainActivity.this, false, true);

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OcrCvBridge.getInstance().release();
    }
}