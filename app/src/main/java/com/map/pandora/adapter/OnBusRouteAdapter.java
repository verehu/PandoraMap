package com.map.pandora.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.route.TransitRouteLine;
import com.map.pandora.R;
import com.map.pandora.util.CommonUtil;
import com.map.pandora.util.TransitUtil;
import com.map.pandora.widget.BusStepView;

import java.util.List;

/**
 * @author jayce
 * @date 2015/04/20
 */
public class OnBusRouteAdapter extends BaseAdapter {
    private List<TransitRouteLine> list;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public OnBusRouteAdapter(Context context, List<TransitRouteLine> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.itemlist_busrouteplan, null);

            viewHolder = new ViewHolder();
            //viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.busStepView = (BusStepView) convertView.findViewById(R.id.busStepView);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
            viewHolder.tv_onfoot_distance = (TextView) convertView.findViewById(R.id.tv_onfoot_distance);
            viewHolder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);

            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        TransitRouteLine transitRouteLine = (TransitRouteLine) getItem(position);

        List<TransitRouteLine.TransitStep> transitSteps = transitRouteLine.getAllStep();

        //viewHolder.tv_title.setText(transitRouteLine.getTitle());
        viewHolder.busStepView.setTransitStep(transitSteps);
        viewHolder.tv_distance.setText(transitRouteLine.getDistance() / 1000 + "公里");
        viewHolder.tv_duration.setText(CommonUtil.s2min(transitRouteLine.getDuration()) + "分钟");
        viewHolder.tv_onfoot_distance.setText(TransitUtil.getOnFootDistance(transitSteps) + "米");
        viewHolder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(position);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        //        TextView tv_title;
        BusStepView busStepView;
        TextView tv_duration;
        TextView tv_distance;
        TextView tv_onfoot_distance;
        LinearLayout ll_item;
    }

    public interface OnItemClickListener {
        public void onItemClicked(int position);
    }
}
