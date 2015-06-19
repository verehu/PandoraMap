package com.map.pandora.util;

import com.baidu.mapapi.search.sug.SuggestionResult;
import com.map.pandora.PoiHistory;
import java.util.ArrayList;
import java.util.List;

/**
 * po类数据库实体类的转换
 * @author jayce
 * @date 2015/4/28
 */
public class PoTransUtil {
    public static SuggestionResult.SuggestionInfo toSuggestionInfo(PoiHistory poiHistory) {
        SuggestionResult.SuggestionInfo suggestionInfo = new SuggestionResult.SuggestionInfo();
        suggestionInfo.key = poiHistory.getKey();
        suggestionInfo.city = poiHistory.getCity();
        suggestionInfo.district = poiHistory.getDistrict();
        return suggestionInfo;
    }

    public static List<SuggestionResult.SuggestionInfo> toSuggestionInfoList(List<PoiHistory> historyList){
        ArrayList<SuggestionResult.SuggestionInfo> list=new ArrayList<>();
        for(PoiHistory poiHistory:historyList){
            list.add(toSuggestionInfo(poiHistory));
        }
        return list;
    }
}