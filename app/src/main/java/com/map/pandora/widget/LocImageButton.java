package com.map.pandora.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;


/**
 * 定位模式切换按钮
 * @author jayce
 * @date 2015/04/19
 */
public class LocImageButton extends ImageButton {
    MyLocationConfiguration.LocationMode locMode = MyLocationConfiguration.LocationMode.NORMAL;
    BaiduMap mBaiduMap;


    public LocImageButton(Context context) {
        this(context, null);
    }

    public LocImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    public void setBaiduMap(BaiduMap mBaiduMap) {
        this.mBaiduMap = mBaiduMap;
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locMode,true,null));
        updateButtonState();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode();
            }
        });

        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                changeToMode(MyLocationConfiguration.LocationMode.NORMAL);
            }
        });
    }

    public void changeMode() {
        if (locMode == MyLocationConfiguration.LocationMode.NORMAL) {
            changeToMode(MyLocationConfiguration.LocationMode.FOLLOWING);
        } else if (locMode == MyLocationConfiguration.LocationMode.FOLLOWING) {
            changeToMode(MyLocationConfiguration.LocationMode.COMPASS);
        } else {
            changeToMode(MyLocationConfiguration.LocationMode.FOLLOWING);
        }
    }

    public void changeToMode(MyLocationConfiguration.LocationMode toMode){
        locMode=toMode;
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locMode,true,null));

        switch (locMode){
            case FOLLOWING:
                MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(0).build();
                MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
                mBaiduMap.animateMapStatus(u);
                break;
        }

        updateButtonState();
    }

    private void updateButtonState() {
        setImageLevel(locMode.ordinal()+1);
    }
}
