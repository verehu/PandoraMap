package com.map.pandora;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.map.pandora.adapter.SearchResultAdapter;
import com.map.pandora.contain.Constant;
import com.map.pandora.po.PlanLocationImpl;
import com.map.pandora.util.PoTransUtil;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;


import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * 搜索页面
 *
 * @author jayce
 * @date 2015/04/01
 */
@EActivity(R.layout.activity_search)
public class SearchActivity extends Activity implements OnGetPoiSearchResultListener {
    private String TAG = "SearchActivity";

    public static final int ST_POI_ROUTEPLAN = 0;
    public static final int EN_POI_ROUTEPLAN = 1;
    public static final int POI_SEARCH = 2;

    public static final int REQUEST_UI = 1;

    //intent str begin
    public static final String AUTO="auto";
    public static final String LOCSTR="locstr";
    //intent str end

    private PandoraApplication app;
    Context context;
    @ViewById
    EditText et_search_input;
    @ViewById
    LinearLayout ll_history_search, ll_subtn_container;
    @ViewById
    ListView lv_suggest_search, lv_poiHistory;
    @ViewById
    ProgressBar pro_searching;
    @ViewById
    ImageView img_text_clean;
    @ViewById
    Button btn_search;
    @ViewById
    ImageButton img_voice;
    @SystemService
    LayoutInflater inflater;

    LinearLayout ll_cleanhistory;

    private ProgressDialog proDialog;

    @Bean
    SearchResultAdapter suAdapter, poiHistoryAdapter;

    int pWidth, pHeight;

    private boolean mSUBtnIsShow = false;
    public static final int BTN_ANIMATION_DURATION = 500;

    private SuggestionSearch mSuggestionSearch;
    private PoiSearch mPoiSearch;

    private OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
        public void onGetSuggestionResult(SuggestionResult res) {
            suAdapter.clear();

            img_text_clean.setVisibility(View.VISIBLE);
            pro_searching.setVisibility(View.GONE);

            if (res == null || res.getAllSuggestions() == null) {
                //未找到相关结果
            } else {
                //获取在线建议检索结果
                suAdapter.setResultList(res.getAllSuggestions());
            }
            suAdapter.notifyDataSetInvalidated();
        }
    };

    @AfterViews
    protected void init() {
        app = (PandoraApplication) getApplicationContext();

        mSuggestionSearch = mSuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);


        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        context = getBaseContext();

        initView();
        popInput();

        Intent intent=getIntent();
        if(intent.getBooleanExtra(AUTO,false)){
            String str=intent.getStringExtra(LOCSTR);
            et_search_input.setText(str);
            onSearch(null);
        }
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSuggestionSearch.destroy();
        mPoiSearch.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_UI:
                if(data!=null) {
                    ArrayList<String> resultList = data.getStringArrayListExtra(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (resultList != null && resultList.size() != 0) {
                        Toast.makeText(context, resultList.get(0).toString(), Toast.LENGTH_LONG).show();
                        et_search_input.setText(resultList.get(0).toString());
                        onSearch(null);
                    }
                }
                break;
        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        //获取POI检索结果
        proDialog.dismiss();
        if (poiResult != null) {
            if (poiResult.error == PoiResult.ERRORNO.NO_ERROR) {
                app = (PandoraApplication) getApplicationContext();
                app.setmSearchResultList(poiResult.getAllPoi());

                int action = getIntent().getIntExtra("action", POI_SEARCH);
                if (action == POI_SEARCH) {
                    Intent intent = new Intent(this, SearchPoiShowActivity_.class);
                    startActivity(intent);
                } else {
                    PoiInfo poiInfo = app.getPlanPOI();
                    if (poiInfo != null) {

                        if (action == ST_POI_ROUTEPLAN) {
                            app.setStartLocation(new PlanLocationImpl(poiInfo));
                        } else if (action == EN_POI_ROUTEPLAN) {
                            app.setTargetLocation(new PlanLocationImpl(poiInfo));
                        }

                        setResult(RESULT_OK);
                        finish();
                    } else {
                        showNoResultToast();
                    }
                }
            } else if (poiResult.error == PoiResult.ERRORNO.RESULT_NOT_FOUND) {
                showNoResultToast();
            }
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @TextChange
    void et_search_inputTextChanged(CharSequence text, TextView et_search_input, int before, int start, int count) {
        pro_searching.setVisibility(View.GONE);
        if (StringUtils.isNotEmpty(et_search_input.getText().toString())) {
            ll_history_search.setVisibility(View.GONE);
            lv_suggest_search.setVisibility(View.VISIBLE);
            img_text_clean.setVisibility(View.VISIBLE);

            if (before == 0 && !mSUBtnIsShow) {
                show_btn_search();
            }
        } else {
            ll_history_search.setVisibility(View.VISIBLE);
            lv_suggest_search.setVisibility(View.GONE);
            img_text_clean.setVisibility(View.GONE);

            if (before > 0 && mSUBtnIsShow) {
                hide_btn_search();
                show_poi_history();
            }
        }
    }

    @AfterTextChange
    void et_search_inputAfterTextChanged(TextView et_search_input) {
        if (et_search_input.getText().length() != 0) {
            img_text_clean.setVisibility(View.GONE);
            pro_searching.setVisibility(View.VISIBLE);
            mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                    .keyword(et_search_input.getText().toString()).city(""));
        }
    }

    @ItemClick
    void lv_suggest_searchItemClicked(int position) {
        SuggestionResult.SuggestionInfo suggestionInfo = (SuggestionResult.SuggestionInfo) suAdapter.getItem(position);
        et_search_input.setText(suggestionInfo.key + " " + suggestionInfo.city + suggestionInfo.district);

        toSearch(suggestionInfo);
    }

    @ItemClick
    void lv_poiHistoryItemClicked(int position) {
        if (position == poiHistoryAdapter.getCount()) {
            clearPoiHistory();
        } else {
            SuggestionResult.SuggestionInfo suggestionInfo = (SuggestionResult.SuggestionInfo) poiHistoryAdapter.getItem(position);
            et_search_input.setText(suggestionInfo.key + " " + suggestionInfo.city + suggestionInfo.district);

            toSearch(suggestionInfo);
        }
    }

    private void initView() {
        proDialog = new ProgressDialog(this);
        ll_cleanhistory = (LinearLayout) inflater.inflate(R.layout.item_cleanpoihistory, null);

        ll_cleanhistory.setVisibility(View.GONE);
        et_search_input.setFocusable(true);
        et_search_input.setFocusableInTouchMode(true);
        pro_searching.setVisibility(View.GONE);
        img_text_clean.setVisibility(View.GONE);

        lv_suggest_search.setAdapter(suAdapter);
        lv_poiHistory.addFooterView(ll_cleanhistory);
        lv_poiHistory.setAdapter(poiHistoryAdapter);
        lv_poiHistory.setFooterDividersEnabled(false);

        poiHistoryAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (poiHistoryAdapter.getCount() == 0) {
                    ll_cleanhistory.setVisibility(View.GONE);
                } else {
                    ll_cleanhistory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                if (poiHistoryAdapter.getCount() == 0) {
                    ll_cleanhistory.setVisibility(View.GONE);
                } else {
                    ll_cleanhistory.setVisibility(View.VISIBLE);
                }
            }
        });
        show_poi_history();
    }

    public void onSearch(View view) {
        proDialog.setMessage("正在搜索\"" + et_search_input.getText().toString() + "\"");
        proDialog.show();
        app.setKeyword(et_search_input.getText().toString());
        mPoiSearch.searchInCity(new PoiCitySearchOption().keyword(app.getKeyword()).city(""));

        writePoiHistoryToSQL(et_search_input.getText().toString());
    }

    public void toSearch(SuggestionResult.SuggestionInfo suggestionsInfo) {
        proDialog.setMessage("正在搜索\"" + et_search_input.getText().toString() + "\"");
        proDialog.show();
        app.setKeyword(et_search_input.getText().toString());
        mPoiSearch.searchInCity(new PoiCitySearchOption().keyword(app.getKeyword()).city(suggestionsInfo.city));

        writePoiHistoryToSQL(suggestionsInfo.key, suggestionsInfo.district, suggestionsInfo.city);
    }

    public void onBack(View view) {
        finish();
    }

    public void onCleanText(View view) {
        et_search_input.setText("");
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

    private void popInput() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) et_search_input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(et_search_input, 0);
                           }
                       },
                500);
    }

    private void showNoResultToast() {
        Toast.makeText(context, "未搜索到结果", Toast.LENGTH_LONG).show();
    }

    void show_btn_search() {
        mSUBtnIsShow = true;
        pWidth = ll_subtn_container.getMeasuredWidth();
        pHeight = ll_subtn_container.getMeasuredHeight();

        ValueAnimator animation = ValueAnimator.ofInt(0, pWidth);
        animation.setDuration(BTN_ANIMATION_DURATION);
        //animation.setInterpolator(new AccelerateInterpolator());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int w = (int) animation.getAnimatedValue();
                img_voice.layout(0, 0, pWidth - w, pHeight);
                btn_search.layout(pWidth - w, 0, pWidth, pHeight);

                if (w == pWidth) {
                    btn_search.setEnabled(true);
                } else {
                    btn_search.setEnabled(false);
                }
            }
        });

        animation.start();
    }

    void hide_btn_search() {
        mSUBtnIsShow = false;
        pWidth = ll_subtn_container.getMeasuredWidth();
        pHeight = ll_subtn_container.getMeasuredHeight();

        ValueAnimator animation = ValueAnimator.ofInt(0, pWidth);
        animation.setDuration(BTN_ANIMATION_DURATION);
        //animation.setInterpolator(new AccelerateInterpolator());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int w = (int) animation.getAnimatedValue();
                img_voice.layout(0, 0, w, pHeight);
                btn_search.layout(w, 0, pWidth, pHeight);

                if (w == 0) {
                    btn_search.setEnabled(true);
                } else {
                    btn_search.setEnabled(false);
                }
            }
        });

        animation.start();
    }

    /**
     * 从数据库里面查询搜索过的记录
     */
    void show_poi_history() {
        DaoSession session = app.getDaoSession();
        PoiHistoryDao poiHistoryDao = session.getPoiHistoryDao();
        QueryBuilder qb = poiHistoryDao.queryBuilder();
        qb.orderDesc(PoiHistoryDao.Properties.Timestamp);
        qb.limit(10);

        poiHistoryAdapter.setResultList(PoTransUtil.toSuggestionInfoList(qb.list()));
        poiHistoryAdapter.notifyDataSetInvalidated();
    }

    private void writePoiHistoryToSQL(String key) {
        writePoiHistoryToSQL(key, "", "");
    }

    private void writePoiHistoryToSQL(String key, String district, String city) {
        //搜索记录写入数据库
        DaoSession session = app.getDaoSession();
        PoiHistoryDao poiHistoryDao = session.getPoiHistoryDao();

        PoiHistory poiHistory = new PoiHistory();
        poiHistory.setKey(key);
        poiHistory.setDistrict(district);
        poiHistory.setCity(city);
        poiHistory.setTimestamp(new Date());

        poiHistoryDao.insertOrReplace(poiHistory);
    }

    private void clearPoiHistory() {
        DaoSession session = app.getDaoSession();
        PoiHistoryDao poiHistoryDao = session.getPoiHistoryDao();
        poiHistoryDao.deleteAll();

        poiHistoryAdapter.clear();
        poiHistoryAdapter.notifyDataSetInvalidated();
    }
}
