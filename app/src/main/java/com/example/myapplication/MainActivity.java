package com.example.myapplication;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    ImageView imageViewPicture, imageViewHead;
    TextView textViewResult, textViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setTitle("功能选择");

        textViewStatus = findViewById(R.id.result_tv_status);
        imageViewPicture = findViewById(R.id.result_iv_picture);
        imageViewHead = findViewById(R.id.result_iv_head);
        textViewResult = findViewById(R.id.result_tv);

        String result = OcrCvBridge.getInstance().addInterrupt(new OcrCvBridge.IOcrInterrupt() {
            @Override
            public void onCreate(AppCompatActivity activity) {
                //多语言，界面适配，如：
                //LanguageUtil.setLocale(this);
                //Density.enableUIAdapt(this);
            }
        }).startEngine(MainActivity.this, new OcrCvBridge.IOcrListener() {
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
            textViewStatus.setText("引擎启动失败，原因：" + result);
        } else {
            textViewStatus.setText("引擎已启动");
        }

        findViewById(R.id.list_btn_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OcrCvBridge.getInstance().openCamera(MainActivity.this, true, true);

            }
        });
        findViewById(R.id.list_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OcrCvBridge.getInstance().openCamera(MainActivity.this, true, false);

            }
        });
        findViewById(R.id.list_btn_ocr_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OcrCvBridge.getInstance().openCamera(MainActivity.this, false, true);
                //OcrCvBridge.getInstance().openCamera(MainActivity.this, false, false); //背面

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OcrCvBridge.getInstance().release();
    }
}