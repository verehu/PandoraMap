package com.map.pandora.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.map.pandora.PandoraApplication;

import org.apache.commons.lang.StringUtils;

/**
 * 定位工具类
 * @author jayce
 * @date 2015/3/24
 */
public class LocationUtil {
    private static LocationClient mLocClient;


    /**
     * 定位是否可用
     * @return
     */
    public static boolean isOpen(Context context){
        LocationManager locationManager= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean gps=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return gps||network;
    }


    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public static void setDefaultLocationOptions(SetLocationOptionsCallBack setCallback){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //option.setProdName("com.map.pandora");

        if(setCallback!=null){
            setCallback.setLocationOptions(option);
        }
        mLocClient.setLocOption(option);
    }

    public static void refreshLocation(Context context,PandoraLocationListener locationListener){
        refreshLocation(context,locationListener,null);
    }

    public static void refreshLocation(Context context,PandoraLocationListener locationListener,SetLocationOptionsCallBack setCallback){

        if(!isOpen(context)){
            locationListener.onLocationNotOpen();
            //return;
        }

        if(mLocClient==null){
            mLocClient=new LocationClient(context);
        }

        setDefaultLocationOptions(setCallback);

        mLocClient.registerLocationListener(new PandoraBDLocationDecorator(locationListener));

        if(!mLocClient.isStarted()){
            mLocClient.start();
            locationListener.onLocating();
        }
    }

    public interface SetLocationOptionsCallBack{
        public void setLocationOptions(LocationClientOption options);
    }


    public interface PandoraLocationListener{
        public void onLocationNotOpen();

        public void onLocating();

        public void onLocationSuccess(BDLocation bdLocation);

        public void onLocationFailure(int type);
    }

    public static class PandoraBDLocationDecorator implements BDLocationListener{
        private PandoraLocationListener listener;

        public PandoraBDLocationDecorator(PandoraLocationListener listener) {
            this.listener = listener;
        }

        /**
         *
         static int	TypeGpsLocation
         定位结果描述：GPS定位结果
         static int	TypeNetWorkException
         定位结果描述：网络连接失败
         static int	TypeNetWorkLocation
         定位结果描述：网络定位结果
         static int	TypeNone
         定位结果描述：无效定位结果
         static int	TypeOffLineLocation
         定位结果描述：离线定位结果
         static int	TypeOffLineLocationFail
         定位结果描述：离线定位失败
         static int	TypeOffLineLocationNetworkFail
         定位结果描述：网络请求失败,基站离线定位结果
         static int	TypeServerError
         定位结果描述：server定位失败，没有对应的位置信息
         */
        @Override
        public final void onReceiveLocation(BDLocation bdLocation) {
            int type=bdLocation.getLocType();
            if(type==BDLocation.TypeGpsLocation||type==BDLocation.TypeNetWorkLocation||type==BDLocation.TypeOffLineLocation
                    ||type==BDLocation.TypeOffLineLocationNetworkFail){
                //
                PandoraApplication.mLocation=bdLocation;
                listener.onLocationSuccess(bdLocation);
            }else{
                listener.onLocationFailure(type);
            }
        }
    }
}
