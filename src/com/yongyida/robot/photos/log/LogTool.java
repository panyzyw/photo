package com.yongyida.robot.photos.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.yongyida.robot.photos.application.PhotosApplication;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LogTool {
    private static final String TAG = "YYDRobotPhotos";
    private static final String PATH_LOG = "log";
    //没有停止运行
    private static final String USER_FILE_NAME = File.separator + ".log_user_photos.yyd";
    private static FileOutputStream out = null;
    private static File logFile;

    private static Handler writeLog = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String[] logs = (String[]) msg.obj;
            if (logs != null && logs.length >= 2) {
                Log.d(TAG, logs[0]);
                writeLogFile(logs[1]);
            }
        };
    };

    public static void showLog(String className, String methodName, String msg) {
        String log = className + "." + methodName + ": " + msg;
        int mPId = android.os.Process.myPid();
        String record = "D/" + TAG + " (" + String.valueOf(mPId) + "):" + log;
        String[] logs = { log, record };

        if (true) {
            Message message = writeLog.obtainMessage();
            message.obj = logs;
            writeLog.sendMessage(message);
        }
    }

    static synchronized void writeLogFile(String string) {
        Context context = PhotosApplication.getAppContext();

//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            File file = context.getExternalFilesDir(File.separator + PATH_LOG);
//            if (file != null && !file.exists()) {
//                file.mkdir();
//            }
//            if (file != null) {
//                logFile = new File(file.getAbsolutePath() + USER_FILE_NAME);
//            }
//        } else {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + PATH_LOG;
            File file = new File(path);
            if (file != null && !file.exists()) {
                file.mkdir();
            }
            if (file != null) {
                logFile = new File(file.getAbsolutePath() + USER_FILE_NAME);
            }
//        }
        if (logFile != null) {
            try {
                out = new FileOutputStream(logFile, true);
                if (isFileOver5M(logFile.getAbsolutePath())) {
                    if (logFile.exists()) {
                        if (logFile.isFile()) {
                            logFile.delete();
                        }
                    }
                }
                if (string.length() == 0) {
                    return;
                }
                byte[] buffer = (MyDate.getDateEN() + "  " + string + "\n").getBytes();
                if (buffer != null && out != null) {
                    out.write(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
            } finally {
                try {
                    if (out != null) {
                        out.close();
                        out = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static boolean isFileOver5M(String filePath) {
        File f = new File(filePath);

        if (f.exists() && f.length() > 1024 * 1024 * 5)
            return true;
        return false;
    }
}
