package com.nainaiwang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nainaiwang.utils.NetInfoUtils;
import com.nainaiwang.zixun.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/4.
 */
public class ChannelImageAdapter extends BaseAdapter {

    private Context context;
    private List<String> channelImageList = new ArrayList<>();
    private ImageOptions options;

    public ChannelImageAdapter(Context context, List<String> channelImageList) {
        this.context = context;
        this.channelImageList = channelImageList;
        options = new ImageOptions.Builder().setIgnoreGif(true)
                .setUseMemCache(true)
                .setLoadingDrawableId(R.mipmap.jiazai)
                .setFailureDrawableId(R.mipmap.jiazai)
                .setImageScaleType(ImageView.ScaleType.FIT_XY).build();
    }

    @Override
    public int getCount() {
        return channelImageList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_gridview_channelimage, null);
            holder.pic = (ImageView) convertView.findViewById(R.id.imageview_itemchannelimage_imageview);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        if (NetInfoUtils.isNetworkConnected(context)) {
            x.image().bind(holder.pic, channelImageList.get(position), options);
        } else {
            x.image().bind(holder.pic, "", options);
        }

        return convertView;
    }

    class Holder {
        ImageView pic;
    }

}
