package com.yongyida.robot.photos.utils;

import com.google.gson.Gson;
import com.yongyida.robot.photos.entity.AlertBean;
import com.yongyida.robot.photos.entity.Bean;

public class RemindUtils {
/**
 * 将json转换成Bean对象
 * @param beanJsonString
 * @param cls
 * @return
 */
	public static  Bean parseBeanJson( String beanJsonString ,Class<Bean> cls){
		Gson g = new Gson();
		Bean b = (Bean) g.fromJson(beanJsonString, cls);
		return b; 		
	}
	
	/**
	 * 将json转换成RemindBean对象
	 * @param remindJsonString
	 * @param cls
	 * @return
	 */
	public static  AlertBean parseJokeJson( String remindJsonString ,Class<AlertBean> cls){
		Gson g = new Gson();
		AlertBean sb = (AlertBean) g.fromJson(remindJsonString, cls);
		return sb; 		
	}
//	
//	//解析预定好的标题
//	public static String parseTitle(String text){		
//		if(text == null)
//			return "";
//		
//		for( String s : Constant.titles ){
//			if(text.contains(s))
//				return s;
//		}
//	}
	
}
