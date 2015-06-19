package com.map.pandora.util;

/**
 * @author jayce
 * @date 2015/04/21
 */
public class CommonUtil {
    public static int s2min(int duration){
        return duration/60;
    }

    public static String s2minStr(int duration){
        return s2min(duration)+"分";
    }

    //s转换成标准化格式的时间
    public static String s2timeStr(int duration){
        String res= "";
        int min=s2min(duration);
        if(min>=60){
            res+=min/60+"小时";
        }
        if(min%60!=0){
            res+=min%60+"分";
        }
        return res;
    }

    public static int m2km(int m){
        return m/1000;
    }

    public static String m2kmStr(int m){
        return m2km(m)+"公里";
    }
}
