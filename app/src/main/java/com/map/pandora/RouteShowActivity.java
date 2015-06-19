package com.map.pandora;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import com.baidu.navisdk.BNaviEngineManager;
import com.baidu.navisdk.BaiduNaviManager;
import com.map.pandora.fragment.OnBusRouteFragment_;
import com.map.pandora.fragment.OnBusRouteLineFragment_;
import com.map.pandora.fragment.OnDrivingRoutePlanFragment_;
import com.map.pandora.fragment.OnFootRoutePlanFragment_;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


/**
 * @author jayce
 * @date 2015/4/14
 */
@EActivity(R.layout.activity_routeshow)
public class RouteShowActivity extends FragmentActivity implements OnGetRoutePlanResultListener {

    public static final String TAG="RouteShowActivity";

    public static final String PLANWAY = "planway";
    public static final int CAR = 0;
    public static final int BUS = 1;
    public static final int ONFOOT = 2;

    @App
    PandoraApplication app;
    @ViewById
    RadioGroup rg_planway;
    @ViewById
    RadioButton rb_plan_car, rb_plan_bus, rb_plan_onfoot;
    ProgressDialog proDialog;

    OnDrivingRoutePlanFragment_ onDrivingRoutePlanFragment;
    OnFootRoutePlanFragment_ onFootRoutePlanFragment;
    OnBusRouteLineFragment_ onBusRouteLineFragment;

    OnBusRouteFragment_ onBusRouteFragment;

    RoutePlanSearch mRoutePlanSearch;
    int planway;

    FragmentManager fm;


    private boolean mIsEngineInitSuccess = false;
    private BNaviEngineManager.NaviEngineInitListener mNaviEngineInitListener = new BNaviEngineManager.NaviEngineInitListener() {
        public void engineInitSuccess() {
            //导航初始化是异步的，需要一小段时间，以这个标志来识别引擎是否初始化成功，为true时候才能发起导航
            mIsEngineInitSuccess = true;
            Log.i(TAG,"初始化导航引擎成功");
        }

        public void engineInitStart() {
        }

        public void engineInitFail() {
            Log.i(TAG,"初始化导航引擎失败");
        }
    };
    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    @AfterViews
    void init() {
        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(this);
        proDialog = new ProgressDialog(this);
        proDialog.setMessage("加载中...");

        planway = getIntent().getIntExtra(PLANWAY, BUS);

        fm = getSupportFragmentManager();
        changeToFragment(planway);

        rg_planway.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                PlanNode stNode = PlanNode.withLocation(app.getStartLocation().getLatLng());
                PlanNode enNode = PlanNode.withLocation(app.getTargetLocation().getLatLng());

                proDialog.show();
                switch (checkedId) {
                    case R.id.rb_plan_car:
                        mRoutePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
                        break;
                    case R.id.rb_plan_bus:
                        mRoutePlanSearch.transitSearch(new TransitRoutePlanOption().from(stNode).city(app.getStartLocation().getCity()).to(enNode));
                        break;
                    case R.id.rb_plan_onfoot:
                        mRoutePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(enNode));
                        break;
                }
            }
        });

        //初始化导航引擎
        if(!BaiduNaviManager.getInstance().checkEngineStatus(getApplicationContext())) {
            BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
                    mNaviEngineInitListener, new LBSAuthManagerListener() {
                        @Override
                        public void onAuthResult(int status, String msg) {
                            String str = null;
                            if (0 == status) {
                                str = "key校验成功!";
                            } else {
                                str = "key校验失败, " + msg;
                            }
                            Log.i(TAG,str);
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRoutePlanSearch.destroy();
    }

//    @Override
//    public void onBackPressed() {
//        onBack(null);
//    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        app.setWalkingRouteResult(walkingRouteResult);
        changeToFragment(ONFOOT);

        proDialog.dismiss();
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        app.setTransitRouteResult(transitRouteResult);
        changeToFragment(BUS);

        proDialog.dismiss();
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        app.setDrivingRouteResult(drivingRouteResult);
        changeToFragment(CAR);

        proDialog.dismiss();
    }


    public void onBack(View view) {
        onBackPressed();
    }

    private void changeToFragment(int planway) {
        FragmentTransaction ft =fm.beginTransaction();
        //清空回退栈
        int backStackCount =fm.getBackStackEntryCount();
        while (backStackCount > 0) {
            fm.popBackStackImmediate();
            backStackCount =fm.getBackStackEntryCount();
        }
        switch (planway) {
            case CAR:
                if (onDrivingRoutePlanFragment == null) {
                    onDrivingRoutePlanFragment = new OnDrivingRoutePlanFragment_();
                }
                ft.replace(R.id.map_container, onDrivingRoutePlanFragment);
                rb_plan_car.setChecked(true);
                break;
            case BUS:
                if (onBusRouteLineFragment == null) {
                    onBusRouteLineFragment = new OnBusRouteLineFragment_();
                }
                if (onBusRouteFragment == null) {
                    onBusRouteFragment = new OnBusRouteFragment_();
                }
                ft.replace(R.id.map_container, onBusRouteLineFragment);
                break;
            case ONFOOT:
                if (onFootRoutePlanFragment == null) {
                    onFootRoutePlanFragment = new OnFootRoutePlanFragment_();
                }
                ft.replace(R.id.map_container, onFootRoutePlanFragment);
                rb_plan_onfoot.setChecked(true);
                break;
        }
        ft.commit();
    }

    public void changeToOnBusRouteFragment(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (onBusRouteFragment == null) {
            onBusRouteFragment = new OnBusRouteFragment_();
        }
        Bundle bundle = new Bundle();
        bundle.putInt("postion", position);
        onBusRouteFragment.setArguments(bundle);
        ft.replace(R.id.map_container, onBusRouteFragment);
        ft.addToBackStack(onBusRouteFragment.getClass().getName());
        ft.commit();
    }
}
