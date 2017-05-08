package com.nainaiwang.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nainaiwang.bean.SearchHistory;
import com.nainaiwang.myinterface.SearchHistoryInterface;
import com.nainaiwang.zixun.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 */
public class SearchHistoryAdapter extends BaseAdapter {

    private Context context;
    private List<SearchHistory> shList = new ArrayList<>();
    private SearchHistoryInterface searchHistoryInterface;

    public SearchHistoryAdapter(Context context, List<SearchHistory> shList) {
        this.context = context;
        this.shList = shList;
    }

    public void getDeleteNum(SearchHistoryInterface searchHistoryInterface) {
        this.searchHistoryInterface = searchHistoryInterface;
    }

    @Override
    public int getCount() {
        return shList.size();
    }

    @Override
    public Object getItem(int position) {
        return shList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        int type = getItemViewType(position);
        switch (type) {
            case 0:
                Holder holder = null;
                if (convertView == null) {
                    holder = new Holder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_search_searchhistory, null);
                    holder.searchHistory = (TextView) convertView.findViewById(R.id.textview_itemsearchhistory_textview);
                    holder.delete = (RelativeLayout) convertView.findViewById(R.id.relativelayout_itemsearchhistory_delete);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                holder.searchHistory.setText(shList.get(position).getStr());
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchHistoryInterface.passValue(shList.get(position).getId());
                    }
                });
                break;
            case 1:
                HolderTwo holderTwo = null;
                if (convertView == null) {
                    holderTwo = new HolderTwo();
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_search_searchhistory_two, null);
                    holderTwo.clear = (TextView) convertView.findViewById(R.id.textview_itemsearchhistory_clearall);
                    convertView.setTag(holderTwo);
                } else {
                    holderTwo = (HolderTwo) convertView.getTag();
                }
                holderTwo.clear.setText(shList.get(position).getStr());
                break;
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (position == (shList.size() - 1)) {
            type = 1;
        } else {
            type = 0;
        }
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    class Holder {
        TextView searchHistory;
        RelativeLayout delete;
    }

    class HolderTwo {
        TextView clear;
    }

}
