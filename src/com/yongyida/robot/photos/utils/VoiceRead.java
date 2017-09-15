package com.yongyida.robot.photos.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.yongyida.robot.photos.application.PhotosApplication;
import com.yongyida.robot.photos.entity.Bean;
import com.yongyida.robot.photos.receiver.OpenPhotosReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bright on 2016/9/28.
 */
public class VoiceRead {
    public static final String APP_NAME = "YYDRobotPhotos";
    private static VoiceRead mReadSpeech;

    private static ReadComplete mListener;
    private Context mContext;
    //获取对象
    private SpeechSynthesizer mSpeechSynthesizer;

    public interface ReadComplete{
        void onComplete();
    }

    public void setCompleteListener(ReadComplete listener){
        mListener = listener;
    }
    public VoiceRead(Context context){
        this(context, SpeechConstant.TYPE_LOCAL, "jiajia", "50", "50", "100", "3", "true", "wav", "");

    }

    public VoiceRead(Context context, String engineType, String voicer, String speed , String tone, String volume, String type, String interpt, String format, String path){
        this.mContext = context;
        this.mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(context, new InitListener() {

            @Override
            public void onInit(int arg0) {

            }
        });
        setParam(engineType, voicer, speed, tone, volume, type, interpt, format, path);
    }



    public boolean isSpeaking(){
        return mSpeechSynthesizer.isSpeaking();
    }

    public void start(String words) {
        if(mSpeechSynthesizer == null){
            return;
        }
        if(words == null){
            return;
        }

        if(mSpeechSynthesizer.isSpeaking()){
            mSpeechSynthesizer.stopSpeaking();
        }

        mSpeechSynthesizer.startSpeaking(words, new SynthesizerListener() {

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int arg0, int arg1, int arg2) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakBegin() {
            }

            @Override
            public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

            }

            @Override
            public void onCompleted(SpeechError arg0) {

                if(arg0==null){
                }else{
                    Log.e("error", "int:"+arg0.getErrorCode() + "string:"+arg0.getErrorDescription());
                }
                if(mListener != null){
                    mListener.onComplete();
                }
            }

            @Override
            public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {

            }
        });
        collectInfoToServer(OpenPhotosReceiver.bean,words);

    }


    public void stop() {
        if(mSpeechSynthesizer == null){
            return;
        }
        if(mSpeechSynthesizer.isSpeaking()){
            mSpeechSynthesizer.stopSpeaking();
            mSpeechSynthesizer.destroy();
        }
    }

    /**
     * 参数设置.
     * @param engineType
     * @param voicer
     * @param speed
     * @param tone
     * @param volume
     * @param type
     * @param interpt
     * @param format
     * @param path
     */
    private void setParam(String engineType, String voicer, String speed , String tone, String volume, String type, String interpt, String format, String path){
        mSpeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
        //mSpeechSynthesizer.setParameter(SpeechConstant.AUDIO_SOURCE, AudioSource.VOICE_COMMUNICATION+"");
        if(engineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voicer);
        }else {
            mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voicer);

            mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            //设置发音人资源路径
            mSpeechSynthesizer.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        }
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, speed);
        mSpeechSynthesizer.setParameter(SpeechConstant.PITCH, tone);
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, volume);
        mSpeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, type);

        mSpeechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, interpt);

        mSpeechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, format);
        mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, path);

    }

    /**
     * 获取发音人资源路径
     */
    private String getResourcePath(){
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/"+"jiajia"+".jet"));
        return tempBuffer.toString();
    }


    public static VoiceRead getInstence(Context context){
        if(mReadSpeech == null){
            synchronized(VoiceRead.class){
                if(mReadSpeech == null){
                    mReadSpeech = new VoiceRead(context);
                }
            }

        }

        return mReadSpeech;
    }

    public void setStatus(boolean set)
    {

    }

    public boolean getStatus()
    {
        boolean ret = false;

        return ret;
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
        intent.putExtra("collect_from",APP_NAME);
        PhotosApplication.getAppContext().sendBroadcast(intent);
        Log.i("collectInfoToServer:", "com.yongyida.robot.COLLECT");
    }

}


