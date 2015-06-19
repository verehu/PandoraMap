package com.map.pandora.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams;
import com.map.pandora.BNavigatorActivity_;
import com.map.pandora.PandoraApplication;
import com.map.pandora.R;
import com.map.pandora.interfaces.PlanLocation;
import com.map.pandora.util.CommonUtil;

import org.androidannotations.annotations.Bean;

import java.util.List;

/**
 * @author jayce
 * @date 2015/4/23
 */
public class DrvingRouteAdapter extends PagerAdapter {

    private Context context;
    private List<DrivingRouteLine> list;
    private View.OnClickListener onClickListener;

    public DrvingRouteAdapter(Context context, List<DrivingRouteLine> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
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
//        super.destroyItem(container, position, object);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    View getView(int position){
        View convertView = LayoutInflater.from(context).inflate(R.layout.itempager_drvingroute,null);
        DrivingRouteLine routeLine=list.get(position);

        TextView tv_duration= (TextView) convertView.findViewById(R.id.tv_duration);
        TextView tv_distance= (TextView) convertView.findViewById(R.id.tv_distance);
        TextView tv_taxi_price = (TextView) convertView.findViewById(R.id.tv_taxi_price);
        TextView tv_traffic_light= (TextView) convertView.findViewById(R.id.tv_traffic_light);
        Button btn_navi= (Button) convertView.findViewById(R.id.btn_navi);

        tv_duration.setText(CommonUtil.s2timeStr(routeLine.getDuration()));
        tv_distance.setText(CommonUtil.m2kmStr(routeLine.getDistance()));
        if(onClickListener!=null) {
            btn_navi.setOnClickListener(onClickListener);
        }
        return convertView;
    }
}
