package com.map.pandora.util;

import android.content.Context;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;

/**
 * 验证工具类
 *
 * @author jayce
 * @date 2015/05/28
 */
public class ValidateUtil {

    public static boolean phone(Context context, String phonenum) {
        //todo  暂时只是empty验证
        if (StringUtils.isEmpty(phonenum)) {
            Toast.makeText(context, "请输入正确的电话号码", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean password(Context context, String password) {
        //todo  暂时只是empty验证
        if (StringUtils.isEmpty(password)) {
            Toast.makeText(context, "密码格式不正确", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
