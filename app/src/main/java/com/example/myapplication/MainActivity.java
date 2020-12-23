package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.library.ocr.FastPermissions;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setTitle("功能选择");

        findViewById(R.id.list_btn_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = OcrCvBridge.getInstance().startUp(MainActivity.this);
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

                new FastPermissions(MainActivity.this).need(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new FastPermissions.Subscribe() {
                    @Override
                    public void onResult(int requestCode, boolean allGranted, String[] permissions) {
                        if (allGranted) {
                            String result = OcrCvBridge.getInstance().openCamera(MainActivity.this, 11);
                            if (!TextUtils.isEmpty(result)) {
                                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "权限不足", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).request(100);
            }
        });
        findViewById(R.id.list_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!OcrCvBridge.getInstance().isEngineStart()) {
                    Toast.makeText(MainActivity.this, "引擎未启动", Toast.LENGTH_SHORT).show();
                    return;
                }

                new FastPermissions(MainActivity.this).need(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new FastPermissions.Subscribe() {
                    @Override
                    public void onResult(int requestCode, boolean allGranted, String[] permissions) {
                        if (allGranted) {
                            String result = OcrCvBridge.getInstance().openCamera(MainActivity.this, 22);
                            if (!TextUtils.isEmpty(result)) {
                                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "权限不足", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).request(100);
            }
        });

        //start
    }
}