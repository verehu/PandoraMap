package com.map.pandora.util;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.map.pandora.SearchActivity;
import com.map.pandora.SearchActivity_;
import com.map.pandora.po.PoiPo;
import com.map.pandora.po.RoutePo;
import com.map.pandora.po.VoicePo;
import com.map.pandora.po.VoiceResult;

import java.util.List;

/**
 * 语音识别工具类
 *
 * @author jayce
 * @date 2015/05/24
 */
public class VoiceUtil {
    public static final String TAG = "VoiceUtil";

    public static void parse(Context context, VoicePo voicePo) {
        List<VoiceResult> results = voicePo.getResults();
        if (results == null || results.size() == 0) {
            Toast.makeText(context, "未搜索到结果", Toast.LENGTH_LONG).show();
            return;
        }
        VoiceResult voiceResult = results.get(0);
        switch (voiceResult.getIntent()) {
            case VoiceResult.POI:
            case VoiceResult.LOCATION:
                PoiPo locationPo = new Gson().fromJson(voiceResult.getObjectStr(), PoiPo.class);
                Log.i(TAG, "location:" + locationPo.getCentre());
                Intent intent=new Intent(context, SearchActivity_.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(SearchActivity.AUTO,true);
                intent.putExtra(SearchActivity.LOCSTR,locationPo.getCentre());
                context.startActivity(intent);
                break;
            case VoiceResult.ROUTE:
                RoutePo routePo = new Gson().fromJson(voiceResult.getObjectStr(), RoutePo.class);
                Log.i(TAG, "route:" + routePo.toString());
                break;
        }


    }
}
