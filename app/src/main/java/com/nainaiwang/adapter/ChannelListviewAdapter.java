package com.nainaiwang.adapter;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nainaiwang.bean.ChannelList;
import com.nainaiwang.utils.NetInfoUtils;
import com.nainaiwang.widget.NoScrollGridView;
import com.nainaiwang.zixun.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/3.
 */
public class ChannelListviewAdapter extends BaseAdapter {

    private Context context;
    private List<ChannelList> channelList = new ArrayList<>();
    private ImageOptions options;

    public ChannelListviewAdapter(Context context, List<ChannelList> channelList) {
        this.context = context;
        this.channelList = channelList;
        options = new ImageOptions.Builder().setIgnoreGif(true)
                .setUseMemCache(true)
                .setLoadingDrawableId(R.mipmap.jiazai)
                .setFailureDrawableId(R.mipmap.jiazai)
                .setImageScaleType(ImageView.ScaleType.FIT_XY).build();
    }

    @Override
    public int getCount() {
        return channelList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        switch (type) {
            case 0:
                HolderOne holderOne = null;
                if (convertView == null) {
                    holderOne = new HolderOne();
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_channel_one, null);
                    holderOne.pic = (ImageView) convertView.findViewById(R.id.imageview_itemchannelone_image);
                    holderOne.content = (TextView) convertView.findViewById(R.id.textview_itemchannelone_content);
                    holderOne.time = (TextView) convertView.findViewById(R.id.textview_itemchannelone_time);
                    holderOne.author = (TextView) convertView.findViewById(R.id.textview_itemchannelone_zixun);
                    holderOne.ad = (TextView) convertView.findViewById(R.id.textview_itemchannelone_ad);
                    convertView.setTag(holderOne);
                } else {
                    holderOne = (HolderOne) convertView.getTag();
                }
                holderOne.content.setText(channelList.get(position).getName());
                holderOne.time.setText(channelList.get(position).getCreate_time());
//                holderOne.author.setText(channelList.get(position).getAuthor());
                if ("1".equals(channelList.get(position).getIs_ad())){
                    holderOne.ad.setVisibility(View.VISIBLE);
                    holderOne.author.setVisibility(View.GONE);
                } else {
                    holderOne.ad.setVisibility(View.GONE);
                    holderOne.author.setVisibility(View.VISIBLE);
                    holderOne.author.setText(channelList.get(position).getAuthor());
                }
                if (NetInfoUtils.isNetworkConnected(context)) {
                    x.image().bind(holderOne.pic, channelList.get(position).getChannelImageList().get(0), options);
                } else {
                    x.image().bind(holderOne.pic, "", options);
                }

                break;
            case 1:
                HolderTwo holderTwo = null;
                if (convertView == null) {
                    holderTwo = new HolderTwo();
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_channel_two, null);
                    holderTwo.content = (TextView) convertView.findViewById(R.id.textview_itemchanneltwo_content);
                    holderTwo.noScrollGridView = (NoScrollGridView) convertView.findViewById(R.id.noscrollgridview_itemchanneltwo_gridview);
                    holderTwo.time = (TextView) convertView.findViewById(R.id.textview_itemchanneltwo_time);
                    holderTwo.author = (TextView) convertView.findViewById(R.id.textview_itemchanneltwo_zixun);
                    holderTwo.ad = (TextView) convertView.findViewById(R.id.textview_itemchanneltwo_ad);
                    convertView.setTag(holderTwo);
                } else {
                    holderTwo = (HolderTwo) convertView.getTag();
                }
                holderTwo.content.setText(channelList.get(position).getName());
                holderTwo.time.setText(channelList.get(position).getCreate_time());
                if ("1".equals(channelList.get(position).getIs_ad())){
                    holderTwo.ad.setVisibility(View.VISIBLE);
                    holderTwo.author.setVisibility(View.GONE);
                } else {
                    holderTwo.ad.setVisibility(View.GONE);
                    holderTwo.author.setVisibility(View.VISIBLE);
                    holderTwo.author.setText(channelList.get(position).getAuthor());
                }
                holderTwo.noScrollGridView.setClickable(false);
                holderTwo.noScrollGridView.setPressed(false);
                holderTwo.noScrollGridView.setEnabled(false);
                ChannelImageAdapter adapter = new ChannelImageAdapter(context, channelList.get(position).getChannelImageList());
                holderTwo.noScrollGridView.setAdapter(adapter);
                break;
            default:
                break;
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (channelList.get(position).getPicNum().equals("1")) {
            type = 0;
        } else {
            type = 1;
        }
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    class HolderOne {
        ImageView pic;
        TextView content;
        TextView time;
        TextView author;
        TextView ad;
    }

    class HolderTwo {
        TextView content;
        NoScrollGridView noScrollGridView;
        TextView time;
        TextView author;
        TextView ad;
    }

}
