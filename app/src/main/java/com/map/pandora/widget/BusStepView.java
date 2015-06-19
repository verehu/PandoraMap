package com.map.pandora.widget;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.core.VehicleInfo;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.google.gson.Gson;
import com.map.pandora.R;
import com.map.pandora.util.DensityUtil;
import com.map.pandora.util.TransitUtil;

import java.util.List;

/**
 * @author jayce
 * @date 2015/4/21
 */
public class BusStepView extends LinearLayout {
    public static final String TAG = "BusStepView";

    public BusStepView(Context context) {
        this(context, null);
    }

    public BusStepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BusStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    public void setTransitStep(List<TransitRouteLine.TransitStep> transitSteps) {
        removeAllViews();
        boolean isFirst = true;
        for (int i = 0; i < transitSteps.size(); i++) {
            TransitRouteLine.TransitStep transitStep = transitSteps.get(i);

            VehicleInfo vehicleInfo = transitStep.getVehicleInfo();
            if (vehicleInfo != null) {
                if (!isFirst) {
                    //添加ImageView
                    ImageView imageView = new ImageView(getContext());
                    imageView.setImageResource(R.drawable.arrow_right);
                    LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    int px = DensityUtil.dip2px(getContext(), 8);
                    layoutParams.setMargins(px, 0, px, 0);
                    imageView.setLayoutParams(layoutParams);
                    addView(imageView);
                }
                isFirst = false;

                TextView textView = new TextView(getContext());
                textView.setText(vehicleInfo.getTitle());
                if (TransitUtil.getTransType(transitStep) == TransitUtil.TransType.BUS) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fromto_bus_result_item_bus_icon_hl, 0, 0, 0);
                } else {
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fromto_bus_result_item_subway_icon_hl, 0, 0, 0);
                }
                textView.setCompoundDrawablePadding(DensityUtil.dip2px(getContext(), 4));
                TextPaint tp = textView.getPaint();
                tp.setFakeBoldText(true);
                addView(textView);

                Log.i(TAG, new Gson().toJson("i:" + (i) + " " + transitStep.getVehicleInfo().getTitle()));
            }
        }
    }


}
