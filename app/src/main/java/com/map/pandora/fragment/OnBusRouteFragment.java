package com.map.pandora.fragment;

import android.support.v4.app.Fragment;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.map.pandora.PandoraApplication;
import com.map.pandora.R;
import com.map.pandora.widget.MapContainerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * 公交信息路线列表页
 *
 * @author jayce
 * @date 2015/04/21
 */
@EFragment(R.layout.fragment_onbusplan)
public class OnBusRouteFragment extends Fragment {
    @ViewById
    MapView mMapView;
    @ViewById
    MapContainerView mapcontainerview;
    @App
    PandoraApplication app;

    BaiduMap mBaiMap;

    TransitRouteLine routeLine;

    @AfterViews
    void init(){
        mBaiMap=mMapView.getMap();

        mapcontainerview.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                int pos= (int) getArguments().get("postion");
                routeLine=app.getTransitRouteResult().getRouteLines().get(pos);
                MyTransitRouteOverlay overlay=new MyTransitRouteOverlay(mBaiMap);
                overlay.setData(routeLine);
                overlay.addToMap();
                overlay.zoomToSpan();
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

    public class MyTransitRouteOverlay extends TransitRouteOverlay {
        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

//        @Override
//        public BitmapDescriptor getStartMarker() {
//            return BitmapDescriptorFactory.fromResource(R.drawable.bubble_start);
//        }
//
//        @Override
//        public BitmapDescriptor getTerminalMarker() {
//            return BitmapDescriptorFactory.fromResource(R.drawable.bubble_end);
//        }
    }
}
