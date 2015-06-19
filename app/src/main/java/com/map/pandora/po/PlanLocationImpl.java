package com.map.pandora.po;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.map.pandora.interfaces.PlanLocation;

/**
 * @author jayce
 * @date 2015/04/13
 */
public class PlanLocationImpl implements PlanLocation{
    private PoiInfo poiInfo;

    public PlanLocationImpl(PoiInfo poiInfo) {
        this.poiInfo = poiInfo;
    }

    @Override
    public String getName() {
        return poiInfo.name;
    }

    @Override
    public String getCity() {
        return poiInfo.city;
    }

    @Override
    public LatLng getLatLng() {
        return poiInfo.location;
    }

    @Override
    public String getLocationStr() {
        return poiInfo.address;
    }
}
