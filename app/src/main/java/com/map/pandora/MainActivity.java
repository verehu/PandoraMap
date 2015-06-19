package com.map.pandora;

import android.app.Activity;
import android.content.Intent;

import android.speech.SpeechRecognizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;




import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

import com.google.gson.Gson;
import com.map.pandora.contain.Constant;
import com.map.pandora.po.VoicePo;
import com.map.pandora.util.HttpHandler;
import com.map.pandora.util.HttpUtil;
import com.map.pandora.util.PandoraParams;
import com.map.pandora.util.VoiceUtil;
import com.map.pandora.widget.MapContainerView;


import java.util.ArrayList;


/**
 * MainActivity
 * Created by huwei on 2015-3-23.
 */

public class MainActivity extends Activity {

    public static final String TAG="MainActivity";

    private BaiduMap mBaiduMap;
    MapContainerView mapContainerView;
    MapView mMapView;

    public static final int REQUEST_UI = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
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
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_UI:
                if (data != null) {
                    ArrayList<String> resultList = data.getStringArrayListExtra(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (resultList != null && resultList.size() != 0) {
                        PandoraParams params=new PandoraParams();
                        params.add("domainIds",5);
                        params.add("query",resultList.get(0).toString());
                        HttpUtil.post(Constant.VOICEQUERY,params,new HttpHandler() {
                            @Override
                            public void onSuccess(String response) {
                                //Toast.makeText(getBaseContext(),response , Toast.LENGTH_LONG).show();
                                Log.i(TAG,"voiceresponse:"+response);
                                VoiceUtil.parse(getBaseContext(),new Gson().fromJson(response, VoicePo.class));
                            }
                        });
                    }
                }
                break;
        }
    }

    private void initView() {
        mapContainerView = (MapContainerView) findViewById(R.id.map_container);
        mMapView = mapContainerView.mMapView;
        mBaiduMap = mapContainerView.mBaiduMap;
    }

    private void initData() {

    }

    public void onVoice(View view) {
        Intent intent = new Intent();
        intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
        intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
        intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
        intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
        intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        intent.setAction("com.baidu.action.RECOGNIZE_SPEECH");
        startActivityForResult(intent, REQUEST_UI);
    }

    public void to_search(View view) {
        Intent intent = new Intent(MainActivity.this, SearchActivity_.class);
        startActivity(intent);
    }

    public void to_routeplan(View view) {
        Intent intent = new Intent(MainActivity.this, RoutePlanAcitivity_.class);
        startActivity(intent);
    }

    public void to_more(View view){
        Intent intent=new Intent(MainActivity.this,PersonalActivity_.class);
        startActivity(intent);
    }
}
