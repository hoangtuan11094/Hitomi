package com.hitomi;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;



public class WriteLog {
    private String fileName = "hitomi_Log.txt";
    private final String TAG = this.getClass().getSimpleName();
    File pathExternalPublicDir =
            Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);

    String dir_s = pathExternalPublicDir.getPath() + "/LOG/";
    File dir = new File(dir_s);
    String logFilePath = dir_s + fileName;


    public WriteLog(String writedata) {
        if (dir.exists()) {
            System.out.println("フォルダは存在します");
        } else {
            System.out.println("フォルダは存在しません");
            if (dir.mkdir()) {
                System.out.println("フォルダの作成に成功しました");
            } else {
                System.out.println("フォルダの作成に失敗しました");
            }
        }

        Log.d(TAG, "fileName: " + fileName);
        Log.d(TAG, "dir: " + dir_s);
        Log.d(TAG, "logFilePath: " + logFilePath);

        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(logFilePath, true), "SHIFT_JIS");
            BufferedWriter bw = new BufferedWriter(osw);
            //FileWriter fw = new FileWriter(logFilePath,true);
            bw.write(writedata + "\n");
            bw.close();
            Log.d(TAG, "output_logfile: " + logFilePath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
