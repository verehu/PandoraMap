package com.map.pandora.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.map.pandora.PandoraApplication;
import com.map.pandora.R;
import com.map.pandora.RoutePlanAcitivity_;
import com.map.pandora.po.MyLocation;
import com.map.pandora.po.PlanLocationImpl;


import java.util.ArrayList;
import java.util.List;

/**
 * @author jayce
 * @date 2015/4/8
 */
public class MarkerPagerAdapter extends PagerAdapter {


    Context context;
    private PandoraApplication app;
    List<PoiInfo> searchResultList = new ArrayList<>();


    public MarkerPagerAdapter(Context context, List<PoiInfo> searchResultList) {
        this.context = context;
        this.searchResultList = searchResultList;
        app = (PandoraApplication) context;
    }

    @Override
    public int getCount() {
        return searchResultList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = getView(position);
        container.addView(convertView);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //container.removeView(container.getChildAt(position));
    }

    View getView(int position) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.itempager_marker_detail, null);
        final PoiInfo poiInfo = searchResultList.get(position);
        TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tv_address = (TextView) convertView.findViewById(R.id.tv_address);
        Button btn_gothere = (Button) convertView.findViewById(R.id.btn_gothere);
        if (poiInfo.type == PoiInfo.POITYPE.BUS_STATION) {
            tv_title.setText((position + 1) + "." + poiInfo.name + "(公交站)");
        } else if (poiInfo.type == PoiInfo.POITYPE.SUBWAY_STATION) {
            tv_title.setText((position + 1) + "." + poiInfo.name + "(地铁站)");
        } else {
            tv_title.setText((position + 1) + "." + poiInfo.name);
        }
        tv_address.setText(poiInfo.address);

        btn_gothere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.setStartLocation(new MyLocation(PandoraApplication.mLocation));
                app.setTargetLocation(new PlanLocationImpl(poiInfo));

                Intent intent = new Intent(context, RoutePlanAcitivity_.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
