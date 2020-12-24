package com.library.ocr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.turui.android.activity.PermissionUtils;
import com.turui.android.activity.WCameraActivity;
import com.turui.android.cameraview.DebugLog;
import com.turui.android.cameraview.FinderView;
import com.turui.android.cameraview.R;
import com.turui.android.cameraview.c;
import com.turui.engine.EngineConfig;
import com.turui.engine.InfoCollection;

import java.io.File;
import java.io.FileOutputStream;

public class CameraCardActivity extends WCameraActivity {

    View cameraIvCoverPositive;
    View cameraIvCoverBack;
    View cameraCoverView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        cameraCoverView = findViewById(R.id.camera_fl_cover);
        cameraIvCoverPositive = findViewById(R.id.camera_iv_positive);
        cameraIvCoverBack = findViewById(R.id.camera_iv_cover_back);
        if (OcrVcOpenApi.getInstance().isOcr) {
            if (OcrVcOpenApi.getInstance().isPositive) {
                cameraIvCoverPositive.setVisibility(View.VISIBLE);
                cameraIvCoverBack.setVisibility(View.GONE);
            } else {
                cameraIvCoverPositive.setVisibility(View.GONE);
                cameraIvCoverBack.setVisibility(View.VISIBLE);
            }
        } else {
            cameraIvCoverPositive.setVisibility(View.GONE);
            cameraIvCoverBack.setVisibility(View.GONE);
        }
        OcrVcOpenApi.getInstance().preExecute(this);
    }

    @Override
    protected void setupCameraCallbacks() {
        super.setupCameraCallbacks();
        if (!OcrVcOpenApi.getInstance().isOcr) {
            this.cameraView.setOnPictureTakenListener(new c.e() {
                public void a(Bitmap var1, int var2) {
                    DebugLog.c("decodeByTake()");

                    if (var1 != null) {

                        try {
                            float var3 = changeDegree((float) var2);
                            int var4 = var1.getWidth();
                            int var5 = var1.getHeight();
                            Matrix var6 = new Matrix();
                            var6.postRotate(var3);
                            if (needMirror()) {
                                var6.postScale(-1.0F, 1.0F);
                            }

                            if (var3 != 0.0F || needMirror()) {
                                var1 = Bitmap.createBitmap(var1, 0, 0, var4, var5, var6, false);
                            }

                            cameraView.getFinderView().getmFramingRect();
                            com.turui.android.cameraview.e var7 = cameraView.calculationRect(true);

                            float var9 = (float) var1.getWidth() / (float) var1.getHeight();
                            float var20 = (float) var7.b().a() / (float) var7.b().b();

                            if (var9 < 1.0F && var20 >= 1.0F && getResources().getConfiguration().orientation == 2) {
                                Matrix var11 = new Matrix();
                                var11.postRotate(-90.0F);
                                Bitmap var8 = Bitmap.createBitmap(var1, 0, 0, var7.b().b(), var7.b().a(), var11, false);
                                //Bitmap var8 = Bitmap.createBitmap(var1, 0, 0, var1.getWidth(), var1.getHeight(), var11, false);
                                takeFilePath = saveBitmapCache(CameraCardActivity.this, var8, null, 100);
                            } else {
                                Bitmap var8 = Bitmap.createBitmap(var1, 0, 0, var7.b().a(), var7.b().b());
                                takeFilePath = saveBitmapCache(CameraCardActivity.this, var8, null, 100);
                            }


                        } catch (Exception var16) {
                            var16.printStackTrace();
                        } catch (OutOfMemoryError var17) {
                            var17.printStackTrace();
                        }

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tipDialog != null && tipDialog.isShowing()) {
                                tipDialog.dismiss();
                            }
                            OcrVcOpenApi.getInstance().onTakeResult(takeFilePath);
                            finish();
                        }
                    });
                }
            });
        }
    }

    @Override
    protected int getCustomContentView() {
        return R.layout.activity_auth_id_camera;
    }

    @Override
    protected void cameraOnResume() {
        if (PermissionUtils.isCameraGranted(this)) {
            DebugLog.c("cameraOnResume()");
            this.cameraView.start();
            this.setupCameraCallbacks();
        } else {
            DebugLog.c("checkPermission()");
            PermissionUtils.checkPermission(this, "android.permission.CAMERA", 3002);
        }

    }

    @Override
    protected float changeDegree(float var1) {
        DebugLog.c("changeDegree() " + var1);
        return var1;
    }


    @Override
    protected void initBaseView() {
        super.initBaseView();
        this.captureButton.setVisibility(View.VISIBLE);
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

    @Override
    protected FinderView setTipText(final FinderView finderView) {
        //super.setTipText(finderView);
        //隐藏 finderView
        finderView.draw(new Canvas());
        finderView.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraCoverView.getLayoutParams();
                params.width = finderView.getmFramingRect().width();
                params.height = finderView.getmFramingRect().height();
                cameraCoverView.requestLayout();
            }
        });
        // finderView.setVisibility(View.INVISIBLE);
        return finderView;
    }


    @Override
    protected void startTakePicture() {
        if (OcrVcOpenApi.getInstance().isOcr)
            changeMode();
        DebugLog.c("startTakePicture()");
        if (this.cameraView != null) {
            this.cameraView.takePicture();
        }

        if (this.tipDialog != null) {
            this.tipDialog.show();
            this.tipDialog.setTipText("");
        }
    }


    String takeFilePath = null;
    String smallFilePath = null;

    @Override
    protected boolean decodeSuccess(final EngineConfig.EngingModeType var1, final InfoCollection var2) {
        DebugLog.c("decodeSuccess()");
        Intent var3 = this.getIntent();
        Bundle var4 = new Bundle();
        var4.putSerializable("info", var2);
        var3.putExtras(var4);
        this.setResult(this.engineConfig.getResultCode(), var3);
        if (takeBitmap != null) {
            takeFilePath = saveBitmapCache(this, takeBitmap, null, 100);
        }
        if (smallBitmap != null) {
            smallFilePath = saveBitmapCache(this, smallBitmap, null, 100);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (var1 == EngineConfig.EngingModeType.TAKE && tipDialog != null && tipDialog.isShowing()) {
                    tipDialog.dismiss();
                }

                OcrVcOpenApi.getInstance().onActivityResult(var2, takeFilePath, smallFilePath);
                finish();
            }
        });
        return true;
    }


    /**
     * 保存图片到缓存文件
     */
    public static String saveBitmapCache(Context context, Bitmap bitmap, String name, int quality) {
        if (bitmap == null) {
            return null;
        }
        File appDir = context.getCacheDir();
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        if (!TextUtils.isEmpty(name)) {
            fileName = name + ".jpg";
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TTT", file.length() + "");
        return file.toString();
    }


}
