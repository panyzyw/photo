package com.yongyida.robot.photos.runnable;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yongyida.robot.photos.R;
import com.yongyida.robot.photos.application.PhotosApplication;
import com.yongyida.robot.photos.ui.ImagePagerActivity;
import com.yongyida.robot.photos.ui.MainActivity;
import com.yongyida.robot.photos.ui.PhotosListActivity;
import com.yongyida.robot.photos.utils.CommandState;
import com.yongyida.robot.photos.utils.Constants;
import com.yongyida.robot.photos.utils.MediaPlayBiz;
import com.yongyida.robot.photos.utils.StringToDate;
import com.yongyida.robot.photos.utils.VoiceRead;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class QueryPhotoRunnable implements Runnable{
    private Context mContext;
    private String text;
    private CommandState mState;
    private String newText;
    private int count;
    private MediaPlayBiz mPlayer;

    private int curYear;
    private int curMonth;
    private int curDay;

    private int flag;
    private int flag2;

    private String[] results;
    private List<String> list = new ArrayList<String>();
    private String[] imageUrls;
    private String[] imageUrls1;

    private VoiceRead voiceRead;

    public QueryPhotoRunnable(Context context, String s, CommandState state){
        Calendar c = Calendar.getInstance();
        curYear = c.get(Calendar.YEAR);
        curMonth = c.get(Calendar.MONTH) + 1;
        curDay = c.get(Calendar.DAY_OF_MONTH);

        voiceRead = new VoiceRead(PhotosApplication.getAppContext());
        this.mState = state;
        this.mContext = context;

        if(mState == CommandState.WILL_DAY){
            this.text = getText(s);
        }else if(mState == CommandState.WILL_MONTH){
            this.text = getMonthText(s);
        }else if(mState == CommandState.SPECIFIC){
            this.text = s;
        }
        this.mPlayer = MediaPlayBiz.getInstance();
    }




    @Override
    public void run() {
        if(mState == CommandState.NONE){
            playTeaching();
        }else{
            scanPhotos();
            parseResult(text);
        }
    }

    private void playTeaching(){
        voiceRead.start(PhotosApplication.getAppContext().getResources().getString(R.string.teaching));
    }

    private String getMonthText(String s){
        String parse = "";
        if(s.contains("这个月")){
            parse = curYear + "年" + curMonth + "月";
            return parse;
        }else if(s.contains("本月")){
            parse = curYear + "年" + curMonth + "月";
            return parse;
        }else if(s.contains("上个月")){
            parse = curYear + "年" + (curMonth - 1) + "月";
            return parse;
        }
        return null;
    }
    private String getText(String s){
        String parse = "";
        if(s.contains("今天")){
            parse = curYear + "年" + curMonth + "月" +  curDay +"日";
            return parse;
        }else if(s.contains("前天")){
            parse = curYear + "年" + curMonth + "月" +  (curDay-2) +"日";
            return parse;
        }else if(s.contains("昨天")){
            parse = curYear + "年" + curMonth + "月" +  (curDay-2) +"日";
            return parse;
        }
        return null;
    }
    private Boolean scanPhotos(){

        Constants.listFile = new File(Constants.PHOTO_PATH);
        Constants.photos = Constants.listFile.listFiles();
        if(Constants.photos != null){
            imageUrls = getPhotosList(Constants.photos);
            Arrays.sort(imageUrls, Collections.reverseOrder());
            return true;
        }
        return false;
    }
    private String[] getPhotosList(File[] photoFiles){
        String[] photosList = null;
        if(photoFiles != null){
            photosList = new String[photoFiles.length];
            Constants.map.clear();
            Constants.yearMouths.clear();
            for (int i = 0; i < photoFiles.length; i++) {
                photosList[i] = "file://" + photoFiles[i].getAbsolutePath();
                if(photosList == null){
                }
                String listName = getPhotosName(photoFiles[i].getAbsolutePath());
                String mouth = listName.substring(0, 4)+"年"+listName.substring(4,6)+"月";
                if(!Constants.yearMouths.contains(mouth)){
                    Constants.yearMouths.add(mouth);
                }
                if(Constants.map.get(mouth) == null){
                    List<String> list = new ArrayList<String>();
                    list.clear();
                    list.add(listName);
                    Constants.map.put(mouth,list);
                }else{
                    Constants.map.get(mouth).add(listName);
                }
            }
            Collections.sort(Constants.yearMouths,Collections.reverseOrder());
        }
        return photosList;
    }
    private String getPhotosName(String filePath){
        int num = filePath.lastIndexOf("/");
        String picName = filePath.substring(num+1);
        return picName;
    }
    private void parseResult(String text) {
        for(int i = 0;i < text.length(); i++){
            if (Character.isDigit(text.charAt(i))){
                count++;
            }
        }
        //打开某年某月某日的照片
        if(count >= 6){
            if(text.contains(Constants.Extra.nian) && text.contains(Constants.Extra.yue)
                    && (text.contains(Constants.Extra.ri) || text.contains(Constants.Extra.hao))){
                String[] result = StringToDate.parse(text);
                openSource(result);
            }else if(text.contains(Constants.Extra.nian) && text.contains(Constants.Extra.yue)){
                String[] result = StringToDate.parse(text);
                openSource(result);
            }else{
                playTeaching();
            }

        }else if(count > 4 && count <= 6){
            Log.e("count > 4", "asda"+curMonth+ "asda"+curYear+ "asda"+curDay);
            //打开某年某月的照片
            if(text.contains(Constants.Extra.nian) && text.contains(Constants.Extra.yue)){
                String[] result = StringToDate.parse(text);
                openSource(result);
            }else{
                playTeaching();
            }
            // 打开今年这个月某日的照片
         //打开今年某月某日的照片

        //打开今年某月的照片
            // 打开今年这个月

        // 打开某月某日的照片
            //打开这个月某日
        // 打开某月的照片
            //打开这个月
        }else if(count <= 4 && count >=1 ){
            newText = text;
            //处理月
            if(text.contains("这个月")){
                newText = newText.replace("这个月",(Integer.toString(curMonth)+ Constants.Extra.yue));
            }else if(text.contains("本月")){
                newText = newText.replace("本月",(Integer.toString(curMonth)+ Constants.Extra.yue));
            }else if(text.contains("上个月")){
                newText = newText.replace("上个月",(Integer.toString(curMonth - 1)+ Constants.Extra.yue));
            }

            if(text.contains(Constants.Extra.curYear) || text.contains(Constants.Extra.oneYear)
                        || text.contains(Constants.Extra.twoYear)){


                if(text.contains(Constants.Extra.oneYear)){
                    newText = newText.replace(Constants.Extra.oneYear,(Integer.toString(curYear-1)+ Constants.Extra.nian));
                }else if(text.contains(Constants.Extra.twoYear)){
                    newText = newText.replace(Constants.Extra.twoYear,(Integer.toString(curYear-2)+ Constants.Extra.nian));
                }else if(text.contains(Constants.Extra.curYear)){
                    newText = newText.replace(Constants.Extra.curYear,(Integer.toString(curYear)+ Constants.Extra.nian));
                }
                //今年某月某日
                //今年这个月某日
                if( text.contains(Constants.Extra.yue) &&
                        (text.contains(Constants.Extra.ri) || text.contains(Constants.Extra.hao))){
                        String[] result = StringToDate.parse(newText);
                        openSource(result);
                    //今年某月
                    //今年这个月
                }else if(text.contains(Constants.Extra.yue)){
                        String[] result = StringToDate.parse(newText);
                        openSource(result);
                }else{
                    playTeaching();
                }
                //打开某月某日的照片
                //打开这个月某日的照片
            }else if(text.contains(Constants.Extra.yue) &&
                    (text.contains(Constants.Extra.ri) || text.contains(Constants.Extra.hao))){
                newText = curYear + Constants.Extra.nian + newText;
                String[] result = StringToDate.parse(newText);
                openSource(result);
                //打开某月的照片
                //打开这个月的照片
            }else if(text.contains(Constants.Extra.yue)){
                newText = curYear + Constants.Extra.nian + text;
                String[] result = StringToDate.parse(newText);
                openSource(result);
            }else {
                playTeaching();
            }
        }else{
            playHint();
        }
    }


    private void openSource(String[] result) {
        if(result == null || result.equals("")){
            playTeaching();
            return;
        }
        results = result;
        Log.i("openSource", "photoDate:" + result[0]);
        char[] c = result[0].toCharArray();
        if(c.length == 8){
            int index = -1;
            if(Constants.yearMouths != null && Constants.yearMouths.size() > 0){
                for(int i = 0; i < Constants.yearMouths.size(); i++){
                    if(result[0].equals(Constants.yearMouths.get(i))){
                        index = i;
                    }
                }
                if(index != -1){
                    Bundle data = new Bundle();
                    data.putInt("position", index);
                    Message msg =  new Message();
                    msg.what = 0;
                    msg.setData(data);
                    handler.sendMessage(msg);
                }else{
                    playHint();
                }
            }else{
                playHint();
            }

        }else if(c.length > 8){
            String str = result[0].substring(0, 8);
            int index = -1;
            if(Constants.yearMouths != null && Constants.yearMouths.size() > 0){
                for(int i = 0; i < Constants.yearMouths.size(); i++){

                    if(str.equals(Constants.yearMouths.get(i))){
                        index = i;
                        break;
                    }
                }
                if(index != -1){
                    flag = index;
                    flag2 = -1;
                    changeImageUrls();
                }else{
                    playHint();
                }
            }else{
                playHint();
            }
        }
    }

    private void playHint() {
        if (mPlayer != null) {
            mPlayer.playMusic(mContext,"photos.mp3");
        }
    }

    private void changeImageUrls() {
        list = Constants.map.get(Constants.yearMouths.get(flag));
        imageUrls1 = new String[list.size()];
        imageUrls = new String[list.size()];
        for(int i = 0;i<imageUrls.length;i++){
            imageUrls[i] ="file://" + Constants.PHOTO_PATH+"/"+list.get(i);
            imageUrls1[i] = list.get(i);
        }
        Arrays.sort(imageUrls,Collections.reverseOrder());
        Bundle data = new Bundle();
        Message msg =  new Message();
        msg.what = 1;
        msg.setData(data);
        handler.sendMessage(msg);
    }
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                int position = msg.getData().getInt("position");
                startPhotoListActivity(position);
            }else if(msg.what == 1){
                for(int i = 0; i < imageUrls1.length; i++){
                    if(imageUrls1[i].toCharArray().length >= 9){
                        if(imageUrls1[i].substring(0, 8).equals(results[1])){
                            flag2 = i;
                            break;
                        }
                    }
                }
                if(flag2 != -1){
                    startImagePagerActivity(flag,flag2);
                }else{
                    playHint();
                }
            }
        }


    };


    private void startImagePagerActivity(int flag, int position) {
        // TODO Auto-generated method stub
        if(imageUrls.length > 0 && imageUrls.length > position){
            mContext.startActivities(makeIntentStack(flag,position));
        }

    }

    private Intent[] makeIntentStack(int flag, int position) {
        Intent[] intents = new Intent[2];
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(mContext, MainActivity.class));
        intents[1] = new Intent(mContext, PhotosListActivity.class);
        intents[1].putExtra("key", flag);
        return intents;
    }

    private void startPhotoListActivity(int position) {
        // TODO Auto-generated method stub
        Intent[] intents = new Intent[2];
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(mContext, MainActivity.class));
        intents[1] = new Intent(mContext, PhotosListActivity.class);
        intents[1].putExtra("key", position);
        mContext.startActivities(intents);
    }
}
