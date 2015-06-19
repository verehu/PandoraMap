package com.map.pandora.interfaces;

import com.baidu.mapapi.model.LatLng;

/**
 * @author jayce
 * @date 2015/04/13
 */
public interface PlanLocation {
    public String getName();

    public String getCity();

    public LatLng getLatLng();

    public String getLocationStr();
}
