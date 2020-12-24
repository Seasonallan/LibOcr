# 第一步
在根目录的 build.gradle中 allprojects的repositories里添加jitpack依赖
maven { url 'https://jitpack.io' }
# 第二步
在app项目的build.gradle下的dependencies中添加OCR库依赖
    implementation 'com.github.Seasonallan:LibOcr:1.0'
# 第三步
在app项目的build.gradle下添加配置
在defaultConfig中添加
        ndk {
            abiFilters "x86", 'armeabi-v7a', 'armeabi'
        }

        
# 第四步
无
# 第五步
使用OcrCvBridge调用OCR 库（见demo）
1、OcrCvBridge.getInstance().startEngine 启动引擎
2、身份证正面：OcrCvBridge.getInstance().openCamera(MainActivity.this, true, true);
3、身份证反面：OcrCvBridge.getInstance().openCamera(MainActivity.this, true, false);
4、其他证件（不识别）：OcrCvBridge.getInstance().openCamera(MainActivity.this, false, true); 背面OcrCvBridge.getInstance().openCamera(MainActivity.this, false, false);


# 注意事项
使用demo时，gradle版本不一致方案：
中断更新gradle
修改根目录下的build.gradle中的classpath "com.android.tools.build:gradle:4.0.1" 为你的版本
修改根目录下的gradle/wrapper/gradle-wrapper.properties中的distributionUrl为你的版本
关闭重启项目

 