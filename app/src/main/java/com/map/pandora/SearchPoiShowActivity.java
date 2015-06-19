package com.map.pandora;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.map.pandora.adapter.MarkerPagerAdapter;
import com.map.pandora.widget.MapContainerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 路径显示，poi搜索结果展示
 * @author jayce
 * @date 2015/04/12
 */
@EActivity(R.layout.activity_searchpoishow)
public class SearchPoiShowActivity extends Activity implements OnGetRoutePlanResultListener,BaiduMap.OnMapLoadedCallback {
    public static final String ACTION = "action";
    public static final int POI_DISPLAY = 0;
    public static final int PATH_DISPLAY = 1;

    private int action;

    @App
    PandoraApplication app;
    @ViewById
    MapContainerView  mapcontainerview;
    @ViewById
    MapView mMapView;
    @ViewById
    TextView tv_search_input;
    @ViewById
    ViewPager bottom;

    private BaiduMap mBaiduMap;
    MyPoiOverlay poiOverlay;

    private int marksDraw[] = {R.drawable.b_poi_1, R.drawable.b_poi_2, R.drawable.b_poi_3,
            R.drawable.b_poi_4, R.drawable.b_poi_5, R.drawable.b_poi_6, R.drawable.b_poi_7,
            R.drawable.b_poi_8, R.drawable.b_poi_9, R.drawable.b_poi_10};

    private int marksDrawhl[] = {R.drawable.b_poi_1_hl, R.drawable.b_poi_2_hl, R.drawable.b_poi_3_hl,
            R.drawable.b_poi_4_hl, R.drawable.b_poi_5_hl, R.drawable.b_poi_6_hl, R.drawable.b_poi_7_hl,
            R.drawable.b_poi_8_hl, R.drawable.b_poi_9_hl, R.drawable.b_poi_10_hl};

    @AfterViews
    void init() {
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        action = getIntent().getIntExtra(ACTION, POI_DISPLAY);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        mMapView=null;
        super.onDestroy();
    }

    @Override
    public void onMapLoaded() {
        switch (action) {
            case POI_DISPLAY:
                //处理标题栏
                tv_search_input.setHint(app.getKeyword());
                //处理地图
                final List<PoiInfo> showList = app.getShowSearchList();
                MarkerPagerAdapter adapter = new MarkerPagerAdapter(getApplicationContext(), showList);
                bottom.setAdapter(adapter);
                bottom.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        for (int i = 0; i < poiOverlay.list.size(); i++) {
                            MarkerOptions options = (MarkerOptions) poiOverlay.list.get(i);
                            BitmapDescriptor bitmap = BitmapDescriptorFactory
                                    .fromResource(i == position ? marksDrawhl[i] : marksDraw[i]);

                            options.icon(bitmap);
                            if (i == position) {
                                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(options.getPosition());
                                mBaiduMap.animateMapStatus(u);
                            }
                        }
                        poiOverlay.addToMap();
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                show_poi(0, showList);
                break;
            case PATH_DISPLAY:
                break;
        }
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    private void initView() {
        mBaiduMap = mMapView.getMap();
        mapcontainerview.setOnMapLoadedCallback(this);
    }

    public void onBack(View view) {
        finish();
    }

    public void to_search(View view) {
        Intent intent = new Intent(SearchPoiShowActivity.this, SearchActivity_.class);
        startActivity(intent);
    }

    private void show_poi(int select_poi, List<PoiInfo> showList) {
        poiOverlay=new MyPoiOverlay(mBaiduMap);
        for (int i = 0; i < 10 && i < showList.size(); i++) {
            PoiInfo poiInfo = showList.get(i);
            //定义Maker坐标点
            LatLng point = poiInfo.location;
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(i == select_poi ? marksDrawhl[i] : marksDraw[i]);
            //构建MarkerOption，用于在地图上添加Marker
            MarkerOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap).title(i+"");

            poiOverlay.getOverlayOptions().add(option);
            if (i == select_poi) {
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
                mBaiduMap.animateMapStatus(u);
            }
        }
        poiOverlay.addToMap();
        poiOverlay.zoomToSpan();
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int postion=Integer.parseInt(marker.getTitle());    //通过title获取位置
                bottom.setCurrentItem(postion, true);
                return true;
            }
        });

    }

    public class MyPoiOverlay extends OverlayManager{
        List<OverlayOptions> list;
        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
            list=new ArrayList<>();
        }

        @Override
        public List<OverlayOptions> getOverlayOptions() {
            return list;
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;
        }
    }
}
