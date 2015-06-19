package com.map.pandora.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.map.pandora.R;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jayce
 * @date 2015/04/04
 */
@EBean
public class SearchResultAdapter extends BaseAdapter {

    @RootContext
    Context context;

    @SystemService
    LayoutInflater inflater;

    private List<SuggestionInfo> list = new ArrayList<>();

    public void setResultList(List<SuggestionInfo> list) {
        this.list = list;
    }

    public void add(SuggestionInfo suggestionsInfo) {
        list.add(suggestionsInfo);
    }

    public void clear(){
        list.clear();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView == null) {
            convertView = inflater.from(context).inflate(R.layout.itemlist_searchinfo, null);

            viewHolder=new ViewHolder();
            viewHolder.img_searchresult_type= (ImageView) convertView.findViewById(R.id.img_searchresult_type);
            viewHolder.tv_searchresult_title= (TextView) convertView.findViewById(R.id.tv_searchresult_title);
            viewHolder.tv_searchresult_address= (TextView) convertView.findViewById(R.id.tv_searchresult_address);
            //viewHolder.img_searchresult_type= (ImageView) convertView.findViewById(R.id.img_searchresult_plus);

            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        SuggestionInfo suggestionInfo= (SuggestionInfo) getItem(position);
        viewHolder.tv_searchresult_title.setText(suggestionInfo.key);
        viewHolder.tv_searchresult_address.setText(suggestionInfo.city+suggestionInfo.district);
        return convertView;
    }

    class ViewHolder {
        ImageView img_searchresult_type;
        TextView tv_searchresult_title;
        TextView tv_searchresult_address;
        //ImageView img_searchresult_plus;
    }
}
