package com.map.pandora.util;

import com.baidu.mapapi.search.core.VehicleInfo;
import com.baidu.mapapi.search.route.TransitRouteLine;

import java.util.List;

/**
 * Created by huwei on 2015-04-21.
 */
public class TransitUtil {
    /**
     * 判断当前Setup的换乘方式
     * 不准确的判断方式
     *
     * @param transitStep
     * @return
     */
    public static TransType getTransType(TransitRouteLine.TransitStep transitStep) {
        VehicleInfo vehicleInfo = transitStep.getVehicleInfo();
        if (vehicleInfo == null) {
            return TransType.ONFOOT;
        }
        return isMatchSubWay(vehicleInfo.getTitle()) ? TransType.SUBWAY : TransType.BUS;
    }

    public static int getOnFootDistance(List<TransitRouteLine.TransitStep> transitStepList) {
        int distance = 0;
        for (TransitRouteLine.TransitStep transitStep : transitStepList) {
            if (getTransType(transitStep) == TransType.ONFOOT) {
                distance += transitStep.getDistance();
            }
        }
        return distance;
    }

    public static boolean isMatchSubWay(String title) {
        char last = title.trim().charAt(title.length() - 1);
        return last == '线';
    }

    public enum TransType {
        ONFOOT,
        BUS,
        SUBWAY,
    }
}
