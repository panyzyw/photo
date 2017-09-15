package com.yongyida.robot.photos.application;

import android.app.Application;
import android.content.Context;
import android.os.SystemProperties;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.yongyida.robot.photos.log.LogcatHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PhotosApplication extends Application {
	private static Application mApplication;
    public static final String APP_NAME = "YYDRobotPhotos";
	
	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		initImageLoader(getApplicationContext());
		LogcatHelper.getInstance(mApplication).start();
        CrashHandler.getInstance().init(getApplicationContext());
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=56065ce8");
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new WeakMemoryCache())
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public static Context getAppContext() {
		return mApplication;
	}
	
	public static String getSystemPlatform() {
		String key = "ro.product.model";
        Class<?> clazz;
        try {
            clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getDeclaredMethod("get", String.class);
            return (String) method.invoke(clazz.newInstance(), key);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
		return "";
	}	
	//是否需要相册功能
    public static boolean isNeedPhotos(){
        boolean isNeed = SystemProperties.getBoolean("persist.yongyida.photos",true);
        return isNeed;
    }

}
