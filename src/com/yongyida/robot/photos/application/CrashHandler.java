package com.yongyida.robot.photos.application;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by panyzyw on 2017/6/22.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler instance;
    private Context context;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private static final String logName  = ".Photos_log";
    private CrashHandler(){}


    public static CrashHandler getInstance(){
        if(instance == null){
            synchronized (CrashHandler.class){
                if(instance == null){
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if(!handlerException(e) && uncaughtExceptionHandler != null){
            uncaughtExceptionHandler.uncaughtException(t,e);
        }else{
            try {
                Thread.sleep(2*1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handlerException(Throwable e){
        if(e == null)
            return false;
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, "很抱歉,程序出现异常,即将退出", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        saveLog(e);
        return true;
    }

    private void saveLog(Throwable ex){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.e("crash", "sdCard is not exist or not permissiion" );
            return;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        ex.printStackTrace(pw);
        String result = sw.getBuffer().toString();
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        String logPath = absolutePath + "log";
        File logDir = new File(logPath);
        if(!logDir.exists()){
            logDir.mkdirs();
        }
        File fileName = new File(logDir + File.separator + logName);
        if(!fileName.exists()){
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datetime = simpleDateFormat.format(new Date());
            String fengexian = "----------------" + datetime + "----------------\n";
            FileWriter fw = new FileWriter(fileName,true);
            fw.write(fengexian + result + "\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
