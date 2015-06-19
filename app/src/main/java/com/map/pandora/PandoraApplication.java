package com.map.pandora;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.map.pandora.interfaces.PlanLocation;
import org.androidannotations.annotations.EApplication;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;




/**
 * @author jayce
 * @date 2015/03/23
 */
@EApplication
public class PandoraApplication extends Application {

    public static final String TAG="PandoraApplication";

    private DaoMaster daoMaster;

    private List<PoiInfo> mSearchResultList;
    private String keyword; //关键词
    private PlanLocation startLocation;
    private PlanLocation targetLocation;
    public static BDLocation mLocation;

    private WalkingRouteResult walkingRouteResult;
    private DrivingRouteResult drivingRouteResult;
    private TransitRouteResult transitRouteResult;

    private static RequestQueue mQueue;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context =getApplicationContext();
        SDKInitializer.initialize(context);

        initGreenDao();
    }

    public static RequestQueue getQueue() {
        if(mQueue == null){
            mQueue = Volley.newRequestQueue(context);
        }
        return mQueue;
    }

    public List<PoiInfo> getmSearchResultList() {
        return mSearchResultList;
    }

    public void setmSearchResultList(List<PoiInfo> mSearchResultList) {
        this.mSearchResultList = mSearchResultList;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSimpleKeyWord() {
        String reg = " ";
        String keys[] = keyword.split(reg);
        return keys[0];
    }

    public String getCheckedWord(){
        String str = getSimpleKeyWord();
        String check[] = str.split("-");
        return check[0];
    }

    public List<PoiInfo> getShowSearchList() {
        List<PoiInfo> list = new ArrayList<>();
        List<PoiInfo> bakList = new ArrayList<>();
        int size = mSearchResultList.size();
        for (PoiInfo poiInfo : mSearchResultList.subList(0, size < 10 ? size : 10)) {
            if(isStation(poiInfo)){
                list.add(poiInfo);
            }else{
                //todo poiInfo.name.equals(getSimpleKeyWord()
                if (poiInfo.name.contains(getSimpleKeyWord()))
                    list.add(poiInfo);
            }
            bakList.add(poiInfo);
        }
        return list.size()==0?bakList:list;
    }

    /**
     * 是否为公交站或者地铁站
     * @return
     */
    public boolean isStation(PoiInfo poiInfo){
        return poiInfo.type == PoiInfo.POITYPE.BUS_STATION||poiInfo.type == PoiInfo.POITYPE.SUBWAY_STATION;
    }

    public PoiInfo getPlanPOI() {
        int size = mSearchResultList.size();
        for (PoiInfo poiInfo : mSearchResultList.subList(0, size < 10 ? size : 10)) {
            if (StringUtils.equals(poiInfo.name, getSimpleKeyWord()))
                return poiInfo;
        }
        return null;
    }

    public PlanLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(PlanLocation startLocation) {
        this.startLocation = startLocation;
    }

    public PlanLocation getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(PlanLocation targetLocation) {
        this.targetLocation = targetLocation;
    }

    public void setLocation(BDLocation mLocation) {
        this.mLocation = mLocation;
    }

    public WalkingRouteResult getWalkingRouteResult() {
        return walkingRouteResult;
    }

    public void setWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        this.walkingRouteResult = walkingRouteResult;
    }

    public DrivingRouteResult getDrivingRouteResult() {
        return drivingRouteResult;
    }

    public void setDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        //todo log drivingRouteResult
        this.drivingRouteResult = drivingRouteResult;
    }

    public TransitRouteResult getTransitRouteResult() {
        return transitRouteResult;
    }

    public void setTransitRouteResult(TransitRouteResult transitRouteResult) {
        this.transitRouteResult = transitRouteResult;
    }

    //greenDao begin
    void initGreenDao() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "pandora-db", null);
        daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
    }

    public DaoSession getDaoSession() {
        return daoMaster.newSession();
    }
    //greenDao end
}
