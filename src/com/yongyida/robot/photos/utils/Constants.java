package com.yongyida.robot.photos.utils;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
    //循环广播
    public static final String INTENT_RECYCLE = "com.yydrobot.RECYCLE";
	//停止语音广播
    public static final String INTENT_STOP = "com.yydrobot.STOP";
	public static class Extra {
		public static final String IMAGES = "com.universalimageloader.IMAGES";
		public static final String IMAGE = "com.universalimageloader.IMAGE";
		public static final String POSITION = "com.universalimageloader.IMAGE_POSITION";
		
		public static final String keyword1 = "相册";
		public static final String keyword2 = "相片";
	    public static final String keyword3 = "照片";
		public static final String curYear = "今年";
	    public static final String oneYear = "去年";
	    public static final String twoYear = "前年";
	    public static final String remind  = "提醒";
	    public static final String hao  = "号";
	    public static final String ri  = "日";
	    public static final String yue  = "月";
	    public static final String nian = "年";
	}
	public static final String INTENT_PHOTOS = "com.yydrobot.PHOTOS";
	
	public static String PHOTO_PATH = Environment.getExternalStorageDirectory().toString() + "/PhotosCamera";
	public static String[] imageUrls; // 图片Url
	public static File listFile;
	 //保存选中的图片信息
	public static File[] photos;
	/** String: 获取到的照片年月  List<string>: 同月份的所有照片*/
	public static Map<String,List<String>> map = new HashMap<String, List<String>>();
	/** yearMouths: 获取不重名的照片名称*/
	public static List<String> yearMouths = new ArrayList<String>();

}
