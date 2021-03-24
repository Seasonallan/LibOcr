package com.library.ocr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.idcard.TFieldID;
import com.idcard.TRECAPI;
import com.idcard.TRECAPIImpl;
import com.idcard.TStatus;
import com.idcard.TengineID;
import com.turui.android.activity.CameraActivity;
import com.turui.engine.EngineConfig;
import com.turui.engine.InfoCollection;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * OCR功能模块
 */
public class OcrVcOpenApi {


    public TRECAPI engine;
    private volatile static OcrVcOpenApi sInstance;

    private OcrVcOpenApi() {
        if (sInstance != null) {
            throw new RuntimeException("请注意使用规范");
        }
    }

    public static OcrVcOpenApi getInstance() {
        if (sInstance == null) {
            synchronized (OcrVcOpenApi.class) {
                if (sInstance == null) {
                    sInstance = new OcrVcOpenApi();
                }
            }
        }
        return sInstance;
    }


    IActivityInterrupt iActivityInterrupt;

    public OcrVcOpenApi addInterrupt(IActivityInterrupt iActivityInterrupt) {
        this.iActivityInterrupt = iActivityInterrupt;
        return this;
    }

    public void preExecute(AppCompatActivity activity) {
        if (iActivityInterrupt != null) {
            iActivityInterrupt.onCreate(activity);
        }
    }

    /**
     * 监听器
     */
    public interface IActivityInterrupt {
        void onCreate(AppCompatActivity activity);
    }

    /**
     * 监听器
     */
    public interface IOCRCallback {
        /**
         * 正面回调
         *
         * @param filePath
         * @param filePathHead
         * @param code
         * @param name
         */
        void onOcrPositiveRecognized(String filePath, String filePathHead, String code, String name, String sex, String birth);

        /**
         * 背面回调
         *
         * @param filePath
         * @param issue
         * @param period
         */
        void onOcrBackRecognized(String filePath, String issue, String period);
    }

    private IOCRCallback iOcrCallback;

    /**
     * 启动引擎
     *
     * @param context
     * @param iOcrCallback
     * @return
     */
    public String startEngine(Context context, IOCRCallback iOcrCallback) {
        this.iOcrCallback = iOcrCallback;
        isEngineStart = false;
        // 如果这里发生异常请检查libs下是不是有对应的 .so 文件
        // 如果这里发生异常请检查libs下是不是有对应的 .so 文件
        // 如果这里发生异常请检查libs下是不是有对应的 .so 文件
        try {
            // 集成时注意对应的so架构文件不要弄混，并且检查配置文件
            engine = new TRECAPIImpl();//======================<<<<<<<<<<<<<<<<<<<<<这里

        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            return "无法加载so文件";
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
            return "无法加载so文件";
        }

        //默认不进行引擎初始化的判断，请根据初始化结果自行处理，进入识别界面则认为这里初始化为成功
        //初始化会申请内存，不用时请释放内存，不要重要调用。
        TStatus initStatus = engine.TR_StartUP(context, engine.TR_GetEngineTimeKey());  // 正式版用这句，且替换正式版 .so .dat .mdl文件
        if (null == initStatus) {
            //如果使用外部字库可能是 assets 里面没有对应文件
            //如果有文件，可能是文件有问题
            return "引擎初始化异常\r\nOCR引擎版本 >=7.4.0 的请检查工程assets文件夹下是否包含对应几个文件\r\nlicense.dat\r\noption.cfg\r\ntrData.mdl";
        } else if (initStatus == TStatus.TR_TIME_OUT) {
            // 版本号 >= 7.4.0 时 TR_StartUP(context, 时间Key不再起作用可以任意传，具体配置改到了 assets 下的 license.dat 中)
            return "引擎时间过期";
        } else if (initStatus == TStatus.TR_FAIL) {
            return "引擎初始化失败";
        } else if (initStatus == TStatus.TR_BUILD_ERR) {
            return "包名绑定不一致";
        } else if (initStatus == TStatus.TR_OK) {
            isEngineStart = true;
            return null;
        } else {
            return "错误：" + initStatus.name();
        }
    }

    private boolean isEngineStart;

    /**
     * 引擎是否已经启动
     *
     * @return
     */
    public boolean isEngineStart() {
        return isEngineStart;
    }


    /**
     * 释放资源
     */
    public void release() {
        if (engine != null) {
            engine.TR_ClearUP();//释放内存===============<<<<<<<<<<<<<<<<<<<<<这里
        }
        iActivityInterrupt = null;
        iOcrCallback = null;
        if (CameraActivity.takeBitmap != null && !CameraActivity.takeBitmap.isRecycled()) {
            CameraActivity.takeBitmap.recycle();
        }
        if (CameraActivity.smallBitmap != null && !CameraActivity.smallBitmap.isRecycled()) {
            CameraActivity.smallBitmap.recycle();
        }
        CameraActivity.takeBitmap = null;
        CameraActivity.smallBitmap = null;
    }

    public boolean isOcr = true;
    public boolean isPositive = true;

    /**
     * 打开摄像头
     *
     * @param activity
     * @param ocr        是否需要OCR识别
     * @param isPositive 是否是正面
     * @return
     */
    public String openCamera(Activity activity, boolean ocr, boolean isPositive) {
        if (!isEngineStart) {
            return "引擎启动异常";
        }
        this.isOcr = ocr;
        this.isPositive = isPositive;
        //Intent intent = new Intent(activity, WCameraActivity.class);
        Intent intent = new Intent(activity, CameraCardActivity.class);
        EngineConfig config = new EngineConfig(engine, TengineID.TIDCARD2);
        if (ocr) {
            config.setEngingModeType(EngineConfig.EngingModeType.SCAN);//扫描模式
        } else {
            config.setEngingModeType(EngineConfig.EngingModeType.TAKE);//扫描模式
        }

        config.setShowModeChange(true);//是否显示模式切换按钮
        config.setbMattingOfIdcard(true);//正常下不用，引擎内部裁切外部无法调整，跟so挂钩，安卓层无法调整
//                config.setbMattingOfIdcard(true, 209);//当使用.setbMattingOfIdcard(true)无法输出图片时尝试使用这个方法，第二个参数可能要修改
        config.setCheckCopyOfIdcard(false);//正常下不用，翻拍检测，在结果中获取InfoCollection类中的 .getImageProperty()
        config.setOpenSmallPicture(true);//开启小图（身份证头像与银行卡卡号其它证件没有）
//                config.setOpenImageRotateCheck(false);//返回身份证旋转方向信息，在结果中获取InfoCollection类中的 .getImageRotate()
//                config.setTipBitmapType(EngineConfig.TipBitmapType.IDCARD_PORTRAIT);//默认提示图片(目前只有身份证，其它证件可以自定义)：IDCARD_PORTRAIT:头像面，IDCARD_EMBLEM:国徽面，NONE:不显示
        config.setDecodeInRectOfTakeMode(false);//UI层裁切，无法非常准确。只有拍照模式支持，如果是身份证 setMattingOfIdcard 为 false 时才有效
        config.setSaveToData(false);//保存到私有目录
        config.setResultCode(601);
//                config.setHideVersionTip(true);//强制关闭测试版提示
        config.setMakeMeasureSpec(true);
//                config.setEngineSavePath(imgPath);//仅在测试时使用
//                config.setShowAboutInfo(true);//仅在测试时使用
        config.setLogcatEnable(false);//是否在控制台打印log
        config.setLogcatSaveToFile(false);//日志是否保存到文件
//                config.setLogSaveToFilePath(path);//日志保存路径，自行确认路径与权限
        intent.putExtra(EngineConfig.class.getSimpleName(), config);//必须有
        activity.startActivity(intent);

        return null;
    }

    /**
     * 文本类型 : infoCollection.getFieldString(TFieldID.NAME);//姓名
     * 文本类型 : infoCollection.getFieldString(TFieldID.SEX);//性别
     * 文本类型 : infoCollection.getFieldString(TFieldID.FOLK);//民族
     * 文本类型 : infoCollection.getFieldString(TFieldID.BIRTHDAY);//出生日期
     * 文本类型 : infoCollection.getFieldString(TFieldID.ADDRESS);//住址
     * 文本类型 : infoCollection.getFieldString(TFieldID.NUM);//公民身份证号码
     * <p>
     * 文本类型 : infoCollection.getFieldString(TFieldID.ISSUE);//签发机关
     * 文本类型 : infoCollection.getFieldString(TFieldID.PERIOD);//有效期限
     */
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

    public void onTakeResult(String takeFilePath) {
        if (iOcrCallback != null) {
            if (isPositive) {
                iOcrCallback.onOcrPositiveRecognized(takeFilePath, null
                        , null, null, null, null);
            } else {
                iOcrCallback.onOcrBackRecognized(takeFilePath, null, null);
            }
        }
    }

    public void onActivityResult(InfoCollection info, String takeFilePath, String smallFilePath) {
        if (iOcrCallback != null) {
            if (isPositive) {
                String code = "";
                String name = "";
                String sex = "";
                String birth = "";
                if (info != null) {
                    code = info.getFieldString(TFieldID.NUM);// 证件号码
                    name = info.getFieldString(TFieldID.NAME);// 姓名
                    sex = info.getFieldString(TFieldID.SEX);// 性能
                    birth = info.getFieldString(TFieldID.BIRTHDAY);// 姓名
                }
                iOcrCallback.onOcrPositiveRecognized(takeFilePath, smallFilePath, code, name, sex, birth);
            } else {
                String issue = "";
                String period = "";
                if (info != null) {
                    issue = info.getFieldString(TFieldID.ISSUE);// 签发机关
                    period = info.getFieldString(TFieldID.PERIOD);// 有效期
                }
                iOcrCallback.onOcrBackRecognized(takeFilePath, issue, period);
            }
        }
    }


}
