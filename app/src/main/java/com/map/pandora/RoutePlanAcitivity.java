package com.map.pandora;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

/**
 * @author jayce
 * @date 2015/04/12
 */
@EActivity(R.layout.activity_routeplan)
public class RoutePlanAcitivity extends FragmentActivity implements OnGetRoutePlanResultListener {

    public static final int POI_ST = 0;
    public static final int POI_EN = 1;

    @App
    PandoraApplication app;
    @ViewById
    TextView tv_startloc, tv_targetloc;
    @ViewById
    RadioGroup rg_planway;

    RoutePlanSearch mRoutePlanSearch;
    ProgressDialog proDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case POI_ST:
                setStartTextView();
                break;
            case POI_EN:
                setTargetTextView();
                break;
        }
    }

    @AfterViews
    void init() {
        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(this);

        proDialog = new ProgressDialog(this);

        setStartTextView();

        setTargetTextView();
    }

    private void setTargetTextView() {
        if (app.getTargetLocation() != null) {
            tv_targetloc.setText(app.getTargetLocation().getName());
        }
    }

    private void setStartTextView() {
        if (app.getStartLocation() != null) {
            tv_startloc.setText(app.getStartLocation().getName());
        }
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        proDialog.dismiss();

        app.setWalkingRouteResult(walkingRouteResult);
        Intent intent = new Intent(this, RouteShowActivity_.class);
        intent.putExtra(RouteShowActivity.PLANWAY, RouteShowActivity.ONFOOT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        proDialog.dismiss();

        app.setTransitRouteResult(transitRouteResult);
        Intent intent = new Intent(this, RouteShowActivity_.class);
        intent.putExtra(RouteShowActivity.PLANWAY, RouteShowActivity.BUS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        proDialog.dismiss();

        app.setDrivingRouteResult(drivingRouteResult);
        Intent intent = new Intent(this, RouteShowActivity_.class);
        intent.putExtra(RouteShowActivity.PLANWAY, RouteShowActivity.CAR);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void onBack(View view) {
        app.setStartLocation(null);
        app.setTargetLocation(null);
        finish();
    }

    public void to_searchRoute(View view) {
        PlanNode stNode = PlanNode.withLocation(app.getStartLocation().getLatLng());
        PlanNode enNode = PlanNode.withLocation(app.getTargetLocation().getLatLng());

        proDialog.setMessage("加载中...");
        proDialog.show();

        switch (rg_planway.getCheckedRadioButtonId()) {
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

    public void st_poisearch(View view) {
        Intent intent = new Intent(this, SearchActivity_.class);
        intent.putExtra("action", SearchActivity.ST_POI_ROUTEPLAN);
        startActivityForResult(intent, POI_ST);
    }

    public void en_poisearch(View view) {
        Intent intent = new Intent(this, SearchActivity_.class);
        intent.putExtra("action", SearchActivity.EN_POI_ROUTEPLAN);
        startActivityForResult(intent, POI_EN);
    }
}
