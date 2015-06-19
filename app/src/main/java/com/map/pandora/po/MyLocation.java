package com.map.pandora.po;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.map.pandora.interfaces.PlanLocation;

/**
 * @author jayce
 * @date 2015/04/13
 */
public class MyLocation implements PlanLocation{
    private BDLocation bdLocation;

    public MyLocation(BDLocation bdLocation) {
        this.bdLocation = bdLocation;
    }

    @Override
    public String getName() {
        return "我的位置";
    }

    @Override
    public String getCity() {
        return bdLocation.getCity();
    }

    @Override
    public LatLng getLatLng() {
        return new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
    }

    @Override
    public String getLocationStr() {
        return bdLocation.getAddrStr();
    }
}
