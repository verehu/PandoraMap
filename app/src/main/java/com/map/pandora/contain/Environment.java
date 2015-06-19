package com.map.pandora.contain;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 本地变量设置类
 *
 * @author jayce
 * @date 2015/05/27
 */
public class Environment {

    public static final String ENVNAME = "env";

    public static void setLoginState(Context context, boolean isLogin) {
        SharedPreferences env = context.getSharedPreferences(ENVNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = env.edit();
        editor.putBoolean("islogin",isLogin);
        editor.commit();
    }

    public static boolean isLogin(Context context) {
        SharedPreferences env = context.getSharedPreferences(ENVNAME, Context.MODE_PRIVATE);
        return env.getBoolean("islogin",false);
    }

    public static void setPhonenum(Context context,String phone){
        SharedPreferences env = context.getSharedPreferences(ENVNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = env.edit();
        editor.putString("phonenum",phone);
        editor.commit();
    }

    public static String getPhonenum(Context context){
        SharedPreferences env = context.getSharedPreferences(ENVNAME, Context.MODE_PRIVATE);
        return env.getString("phonenum","");
    }
}
