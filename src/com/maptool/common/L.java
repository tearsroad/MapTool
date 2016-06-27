package com.maptool.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * log类
 * @author mazhibin
 */
public class L {
	static Context context = null;
	
	public static void setContext(Context context){
		L.context = context;
	}
	
	/**
	 * debug
	 */
	public static void d(String msg){
		Log.d(Const.app_name, msg);
	}
	
	public static void d(Object o,String msg){
		Log.d(Const.app_name+"_"+o.getClass().getSimpleName(),msg);
	}
	
	/**
	 * error
	 */
	public static void e(String msg){
		Log.e(Const.app_name, msg);
	}
	
	public static void e(Object o,String msg){
		Log.e(Const.app_name+"_"+o.getClass().getSimpleName(),msg);
	}
	
	/**
	 * toast
	 */
	public static void showToast(String msg){
		L.d(msg);
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	private static Boolean MYLOG_SWITCH=true; // 日志文件总开关  
    private static Boolean MYLOG_WRITE_TO_FILE=true;// 日志写入文件开关  
    private static char MYLOG_TYPE='v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息  
    private static String MYLOG_PATH_SDCARD_DIR="/sdcard/";// 日志文件在sdcard中的路径  
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;// sd卡中日志文件的最多保存天数  
    private static String MYLOGFILEName = "Log.txt";// 本类输出的日志文件名称  
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat(  
            "yyyy-MM-dd HH:mm:ss");// 日志的输出格式  
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式  
	/** 
     * 打开日志文件并写入日志 
     *  
     * @return 
     * **/  
    public static void writeLogtoFile( String text) {// 新建或打开日志文件  
        Date nowtime = new Date();  
        String needWriteFiel = logfile.format(nowtime);  
        String needWriteMessage = myLogSdf.format(nowtime) + "    " +text;  
        File file = new File(MYLOG_PATH_SDCARD_DIR, needWriteFiel  
                + MYLOGFILEName);  
        try {  
            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖  
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);  
            bufWriter.write(needWriteMessage);  
            bufWriter.newLine();  
            bufWriter.close();  
            filerWriter.close();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    } 
}
