package com.nainaiwang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nainaiwang.bean.HomePageChannels;
import com.nainaiwang.zixun.R;

import java.util.List;

/**
 * Created by Administrator on 2016/12/29.
 */
public class ChannelsAdapter extends BaseAdapter {

    private Context context;
    private List<HomePageChannels> stringList;

    public ChannelsAdapter(Context context, List<HomePageChannels> stringList) {
        this.context = context;
        this.stringList = stringList;
    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    @Override
    public Object getItem(int position) {
        return stringList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if(convertView == null){
            holder = new Holder();
            LayoutInflater li = LayoutInflater.from(context);
            convertView = li.inflate(R.layout.item_gridview_channel,null);
            holder.channel = (TextView) convertView.findViewById(R.id.textview_gridview_channel);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.channel.setText(stringList.get(position).getName());
        return convertView;
    }

    class Holder {
        private TextView channel;
    }

}
