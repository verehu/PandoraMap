package com.map.pandora.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.map.pandora.PandoraApplication;

import java.util.Map;


/**
 * 对于网络请求框架volley的封装
 *
 * @author jayce
 * @date 2015/05/24
 */
public class HttpUtil {
    public static final String URLROOT="http://192.168.43.154:8000/pandora/";

    public static void post(String url, final PandoraParams params,HttpHandler handler) {
        url=handleurl(url);

        RequestQueue mQueue = PandoraApplication.getQueue();
        StringRequest request = new StringRequest(Request.Method.POST,url,handler,handler){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> p=params.getParams();
                return p;
            }
        };

        mQueue.add(request);
        mQueue.start();
    }

    public static void get(String url, final PandoraParams params,HttpHandler handler) {
        url=handleurl(url);

        RequestQueue mQueue = PandoraApplication.getQueue();
        StringRequest request = new StringRequest(Request.Method.GET,url,handler,handler){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> p=params.getParams();
                return p;
            }
        };

        mQueue.add(request);
        mQueue.start();
    }

    public static String getCompleteUrl(String addurl){
        return URLROOT+addurl;
    }

    public static String handleurl(String url){
        //自动检测是否为完整地址,不完整地址补全为完整地址
        if(!url.contains("http")){
            url=getCompleteUrl(url);
        }
        //检测地址末尾是否带有'/'
        if(!url.endsWith("/")){
            url+="/";
        }
        return url;
    }

}
