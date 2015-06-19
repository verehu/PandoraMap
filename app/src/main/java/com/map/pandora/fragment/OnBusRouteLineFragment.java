package com.map.pandora.fragment;

import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.baidu.mapapi.search.route.TransitRouteLine;
import com.map.pandora.PandoraApplication;
import com.map.pandora.R;
import com.map.pandora.RouteShowActivity;
import com.map.pandora.RouteShowActivity_;
import com.map.pandora.adapter.OnBusRouteAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

/**
 * @author jayce
 * @date 2015/04/20
 */
@EFragment(R.layout.fragment_onbuslineplan)
public class OnBusRouteLineFragment extends Fragment{
    @ViewById
    ListView lv_busroute;
    OnBusRouteAdapter adapter;
    @App
    PandoraApplication app;

    @AfterViews
    void init(){
        adapter=new OnBusRouteAdapter(getActivity(),app.getTransitRouteResult().getRouteLines());
        lv_busroute.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnBusRouteAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                ((RouteShowActivity)getActivity()).changeToOnBusRouteFragment(position);
            }
        });
    }


}
