package com.map.pandora.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams;
import com.map.pandora.BNavigatorActivity_;
import com.map.pandora.PandoraApplication;
import com.map.pandora.R;
import com.map.pandora.interfaces.PlanLocation;
import com.map.pandora.util.CommonUtil;
import com.map.pandora.widget.MapContainerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;


/**
 * @author jayce
 * @date 2015/4/14
 */
@EFragment(R.layout.fragment_onfootplan)
public class OnFootRoutePlanFragment extends Fragment {
    @ViewById
    MapView mMapView;
    @ViewById
    MapContainerView mapcontainerview;
    @ViewById
    TextView tv_duration, tv_distance, tv_taxi_price;
    @App
    PandoraApplication app;


    BaiduMap mBaiMap;
    WalkingRouteLine routeLine;
    WalkingRouteResult walkingRouteResult;

    @AfterViews
    void init(){
        walkingRouteResult=app.getWalkingRouteResult();
        routeLine=walkingRouteResult.getRouteLines().get(0);

        initView();
        mBaiMap=mMapView.getMap();

        mapcontainerview.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                WalkingRouteOverlay overlay=new MyWalkingRouteOverlay(mBaiMap);
                overlay.setData(routeLine);
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        });
    }

    @Click(R.id.btn_footnavi)
    void btn_footnaviWasClicked(){
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

    void initView(){
        tv_duration.setText(CommonUtil.s2timeStr(routeLine.getDuration()));
        tv_distance.setText(CommonUtil.m2kmStr(routeLine.getDistance()));
        //tv_taxi_price.setText(walkingRouteResult.getTaxiInfo().getTotalPrice()+"元");
    }

    public class MyWalkingRouteOverlay extends WalkingRouteOverlay {
        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
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
