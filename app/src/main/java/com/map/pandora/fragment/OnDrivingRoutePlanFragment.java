package com.map.pandora.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams;
import com.map.pandora.BNavigatorActivity_;
import com.map.pandora.PandoraApplication;
import com.map.pandora.R;
import com.map.pandora.adapter.DrvingRouteAdapter;
import com.map.pandora.interfaces.PlanLocation;
import com.map.pandora.widget.MapContainerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * @author jayce
 * @date 2015/04/19
 */
@EFragment(R.layout.fragment_ondrivingplan)
public class OnDrivingRoutePlanFragment extends Fragment {
    @ViewById
    MapView mMapView;
    @ViewById
    MapContainerView mapcontainerview;
    @ViewById
    ViewPager bottom;
    @App
    PandoraApplication app;


    BaiduMap mBaiMap;
    DrivingRouteLine routeLine;

    DrvingRouteAdapter drvingRouteAdapter;

    private int choosePos = 0;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            drawDrivingRoutes();
        }
    };

    @AfterViews
    void init() {
        mBaiMap = mMapView.getMap();

        mapcontainerview.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                drawDrivingRoutes();
            }
        });

        drvingRouteAdapter=new DrvingRouteAdapter(getActivity(),app.getDrivingRouteResult().getRouteLines());
        drvingRouteAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlanLocation start=app.getStartLocation();
                PlanLocation target=app.getTargetLocation();

                BaiduNaviManager.getInstance().launchNavigator(getActivity(),
                        start.getLatLng().latitude, start.getLatLng().longitude,start.getLocationStr(),
                        target.getLatLng().latitude, target.getLatLng().longitude,target.getLocationStr(),
                        RoutePlanParams.NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME,       //算路方式
                        true,                                            //真实导航
                        BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略
                        new BaiduNaviManager.OnStartNavigationListener() {                //跳转监听

                            @Override
                            public void onJumpToNavigator(Bundle configParams) {
                                Intent intent = new Intent(getActivity(), BNavigatorActivity_.class);
                                intent.putExtras(configParams);
                                startActivity(intent);
                            }

                            @Override
                            public void onJumpToDownloader() {
                            }
                        });
            }
        });
        bottom.setAdapter(drvingRouteAdapter);
        bottom.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                choosePos=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state==0){
                    drawDrivingRoutes();
                }
            }
        });
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
        mMapView = null;
        super.onDestroy();
    }

    void drawDrivingRoutes(){
        mBaiMap.clear();
        //最后画被选中的路线
        for (int i = 0; i < app.getDrivingRouteResult().getRouteLines().size(); i++) {
            if(i!=choosePos) {
                drawOneDrivingRoute(i);
            }
            drawOneDrivingRoute(choosePos);
        }

    }

    private void drawOneDrivingRoute(int i){
        routeLine = app.getDrivingRouteResult().getRouteLines().get(i);
        DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiMap, i);
        overlay.setData(routeLine);
        overlay.addToMap();
        overlay.zoomToSpan();
    }

    public class MyDrivingRouteOverlay extends DrivingRouteOverlay {
        private int myPos;

        @Override
        public int getLineColor() {
            return myPos == choosePos ? Color.parseColor("#21DA7E") : Color.parseColor("#BFD7E8");  //4A81E6
        }

        public MyDrivingRouteOverlay(BaiduMap baiduMap, int myPos) {
            super(baiduMap);
            this.myPos = myPos;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.bubble_start);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.bubble_end);
        }
    }


}
