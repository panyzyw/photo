package com.yongyida.robot.photos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import com.yongyida.robot.photos.R;
import com.yongyida.robot.photos.application.PhotosApplication;
import com.yongyida.robot.photos.entity.Bean;
import com.yongyida.robot.photos.runnable.QueryPhotoRunnable;
import com.yongyida.robot.photos.ui.MainActivity;
import com.yongyida.robot.photos.utils.CommandState;
import com.yongyida.robot.photos.utils.Constants;
import com.yongyida.robot.photos.utils.RemindUtils;
import com.yongyida.robot.photos.utils.VoiceRead;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;

public class OpenPhotosReceiver extends BroadcastReceiver implements VoiceRead.ReadComplete{
    private static final String TAG = "PhotosReceiver";
    public static final int[] zks = new int[]{R.string.zk_1,R.string.zk_2,R.string.zk_3,R.string.zk_4,R.string.zk_5,};
    private Context context;
    private VoiceRead voiceRead;
    public static Bean bean;
	@Override
	public void onReceive(Context context, Intent intent) {
		String result = intent.getStringExtra("result");
        String action = intent.getAction();
        Log.e(TAG,"action:" + action);
        Log.e(TAG,"result:" + result);
        if(action.equals(Constants.INTENT_STOP)){
            voiceRead = new VoiceRead(context);
            voiceRead.stop();
            voiceRead = null;
            return;
        }
		if (action.equals(Constants.INTENT_PHOTOS)) {
			if(result != null){
                //挚康不需要相册功能，作随机回答就跳出
                if(!PhotosApplication.isNeedPhotos()){
                    voiceRead = new VoiceRead(context);
                    voiceRead.setCompleteListener(this);
                    voiceRead.start(context.getString(zks[new Random().nextInt(5)]));
                    this.context = context;
                    return;
                }
				if(!result.equals("")){
                    checkScreenState(context);
					bean = RemindUtils.parseBeanJson(result, Bean.class);
					//如果有数字则查找日期，并打开图片
					for (int i = 0; i < bean.text.length(); i++){
					   if (Character.isDigit(bean.text.charAt(i))){  //用char包装类中的判断数字的方法判断每一个字
						   new QueryPhotoRunnable(context, bean.text, CommandState.SPECIFIC).run();
						   return;
					   }
					}
					if( bean.text.contains("今天") ||  bean.text.contains("昨天") ||  bean.text.contains("前天")){
						new QueryPhotoRunnable(context, bean.text, CommandState.WILL_DAY).run();
						return;
					}
					if( bean.text.contains("上个月") ||  bean.text.contains("本月") ||  bean.text.contains("这个月")
							||bean.text.contains("前个月")){
						new QueryPhotoRunnable(context, bean.text, CommandState.WILL_MONTH).run();
						return;
					}
					if( bean.text.contains("今年") ||  bean.text.contains("去年") ||  bean.text.contains("前年")){
						new QueryPhotoRunnable(context, bean.text, CommandState.NONE).run();
						return;
					}
					Intent inte = new Intent(context , MainActivity.class);
					inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(inte);
                    collectInfoToServer(bean,context.getString(R.string.opendPhotos));
				}
			}else{
				return;
			}
		}
	}

    @Override
    public void onComplete() {
		Intent recycleIntent = new Intent(Constants.INTENT_RECYCLE);
        recycleIntent.putExtra("from","photos");
        context.sendBroadcast(recycleIntent);
    }

    public static void collectInfoToServer(Bean bean, String answer){
        String info = "";
        if(bean != null ){
            try {
                JSONObject infoJsonObject = new JSONObject();
                infoJsonObject.put("semantic","");
                infoJsonObject.put("service",bean.service);
                infoJsonObject.put("operation",bean.operation);
                infoJsonObject.put("text",bean.text);
                infoJsonObject.put("answer",answer);
                info = infoJsonObject.toString();
                Log.i("collectInfoToServer:", info);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Log.i("collectInfoToServer:", "bean=null");
        }
        Intent intent = new Intent("com.yongyida.robot.COLLECT");
        intent.putExtra("collect_result",info);
        intent.putExtra("collect_from",PhotosApplication.APP_NAME);
        PhotosApplication.getAppContext().sendBroadcast(intent);
        Log.i("collectInfoToServer:", "com.yongyida.robot.COLLECT");
    }

    //需要加权限<uses-permission android:name="android.permission.WAKE_LOCK"/>
    private void checkScreenState(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        Log.e(TAG, "isScreenOn: " + powerManager.isScreenOn());
        if(!powerManager.isScreenOn()){
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
            wakeLock.acquire();
            wakeLock.release();
        }
    }
}
