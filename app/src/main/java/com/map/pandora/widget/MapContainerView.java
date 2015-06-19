package com.map.pandora.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.map.pandora.R;
import com.map.pandora.util.DensityUtil;
import com.map.pandora.util.LocationUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

/**
 * @author jayce
 * @date 2015/4/15
 */
@EViewGroup(R.layout.map_container)
public class MapContainerView extends RelativeLayout implements LocationUtil.PandoraLocationListener{
    Context context;
    @ViewById
    RelativeLayout header_view, bottom_view;
    @ViewById
    public MapView mMapView;
    @ViewById
    CheckableImageButton ib_traffic;
    @ViewById
    ImageView img_compass;
    @ViewById
    ImageButton ib_maplayer, ib_maplayer_close,ib_zoomin,ib_zoomout;
    @ViewById
    LocImageButton locImageButton;

    RadioButton rb_maplayer_2d,rb_maplayer_3d,rb_maplayer_sate;

    RadioGroup rg_maplayer;
    @SystemService
    LayoutInflater inflater;
    @SystemService
    WindowManager mWindowManager;

    public BaiduMap mBaiduMap;
    public UiSettings mUISettings;

    public View header, bottom;

    private PopupWindow pop;

    private BaiduMap.OnMapLoadedCallback onMapLoadedCallback;

    public static boolean isFirstLoc = true;

    float maxZoomLevel,minZoomLevel;

    public MapContainerView(Context context) {
        super(context);
    }

    public MapContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnMapLoadedCallback(BaiduMap.OnMapLoadedCallback onMapLoadedCallback) {
        this.onMapLoadedCallback = onMapLoadedCallback;
    }

    @Override
    public void onLocationNotOpen() {

    }

    @Override
    public void onLocating() {
        Toast.makeText(getContext(), "定位中", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationSuccess(BDLocation bdLocation) {
        LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        setLocDataOnMap(latLng);
        if (isFirstLoc) {
            isFirstLoc = false;
            mBaiduMap.animateMapStatus(u);
        }
    }

    @Override
    public void onLocationFailure(int type) {

    }

    @AfterViews
    protected void init() {
        context=getContext();
        header = findViewById(R.id.header);
        if (header != null) {
            removeView(header);
            header_view.addView(header);
        }

        bottom = findViewById(R.id.bottom);
        if (bottom != null) {
            removeView(bottom);
            bottom_view.addView(bottom);
        }
        initMap();

        View root = inflater.inflate(R.layout.pop_mapmanager, null);

        rg_maplayer= (RadioGroup) root.findViewById(R.id.rg_maplayer);
        rb_maplayer_2d= (RadioButton) root.findViewById(R.id.rb_maplayer_2d);
        rb_maplayer_3d= (RadioButton) root.findViewById(R.id.rb_maplayer_3d);
        rb_maplayer_sate= (RadioButton) root.findViewById(R.id.rb_maplayer_sate);

        rg_maplayer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                MapStatus ms;
                MapStatusUpdate u;
                switch (checkedId){
                    case R.id.rb_maplayer_2d:
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(0).build();
                        u = MapStatusUpdateFactory.newMapStatus(ms);
                        mBaiduMap.animateMapStatus(u);
                        break;
                    case R.id.rb_maplayer_3d:
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(-45).build();
                        u = MapStatusUpdateFactory.newMapStatus(ms);
                        mBaiduMap.animateMapStatus(u);
                        break;
                    case R.id.rb_maplayer_sate:
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                        break;
                }
            }
        });
        rb_maplayer_2d.setChecked(true);

        pop = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ib_maplayer_close.setVisibility(GONE);
            }
        });
    }

    @Click(R.id.ib_traffic)
    void ib_trafficWasClicked() {
        ib_traffic.toggle();
        mBaiduMap.setTrafficEnabled(ib_traffic.isChecked());
        Toast.makeText(getContext(), (ib_traffic.isChecked() ? "开启" : "关闭") + "实时交通", Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.ib_maplayer)
    void ib_maplayerWasClicked() {
        if (!pop.isShowing()) {
            ib_maplayer_close.setVisibility(VISIBLE);
            pop.showAsDropDown(ib_maplayer, DensityUtil.dip2px(getContext(), 8), 0);
        }
    }

    @Click(R.id.ib_maplayer_close)
    void ib_maplayer_closeWasClicked() {
        if (pop.isShowing()) {
            pop.dismiss();
        }
    }

    @Click(R.id.ib_zoomin)
    void ib_zoominWasClicked(){
        float nowZoomLevel=mBaiduMap.getMapStatus().zoom+1;
        MapStatusUpdate u=MapStatusUpdateFactory.zoomTo(nowZoomLevel);
        mBaiduMap.animateMapStatus(u);
        refreshzoombutton(nowZoomLevel);
    }

    @Click(R.id.ib_zoomout)
    void ib_zoomoutWasClicked(){
        float nowZoomLevel=mBaiduMap.getMapStatus().zoom-1;
        MapStatusUpdate u=MapStatusUpdateFactory.zoomTo(nowZoomLevel);
        mBaiduMap.animateMapStatus(u);
        refreshzoombutton(nowZoomLevel);
    }

    void refreshzoombutton(float zoomLevel){
        if(zoomLevel==maxZoomLevel){
            ib_zoomin.setEnabled(false);
        }else if(zoomLevel==minZoomLevel){
            ib_zoomout.setEnabled(false);
        }else{
            ib_zoomin.setEnabled(true);
            ib_zoomout.setEnabled(true);
        }
    }

    public void initMap() {
        mBaiduMap = mMapView.getMap();
        locImageButton.setBaiduMap(mBaiduMap);
        mUISettings = mBaiduMap.getUiSettings();
        mMapView.showZoomControls(false);   //隐藏缩放控件
        mBaiduMap.setMyLocationEnabled(true);

        minZoomLevel=mBaiduMap.getMinZoomLevel();
        maxZoomLevel=mBaiduMap.getMaxZoomLevel();

        //设置缩放比例
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(18);
        mBaiduMap.setMapStatus(u);

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mUISettings.setCompassPosition(new Point((int) img_compass.getX() + img_compass.getWidth() / 2, (int) img_compass.getY() + img_compass.getHeight() / 2));
                mMapView.setScaleControlPosition(new Point((int)locImageButton.getX()+locImageButton.getWidth(),(int)locImageButton.getY()+locImageButton.getHeight()/2));

                if(onMapLoadedCallback!=null){
                    onMapLoadedCallback.onMapLoaded();
                }
            }
        });

        LocationUtil.refreshLocation(context,this);
    }

    private void setLocDataOnMap(LatLng latLng) {
        MyLocationData locationData = new MyLocationData.Builder().direction(100).latitude(latLng.latitude).longitude(latLng.longitude).build();
        mBaiduMap.setMyLocationData(locationData);
    }
}
