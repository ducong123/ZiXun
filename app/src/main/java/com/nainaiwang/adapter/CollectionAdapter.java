package com.nainaiwang.adapter;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.image.ImageOptions;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nainaiwang.bean.Collections;
import com.nainaiwang.zixun.R;

public class CollectionAdapter extends BaseAdapter {

	private List<Collections> data = new ArrayList<Collections>();
	private Context context;

	public CollectionAdapter(List<Collections> data, Context context) {
		this.data = data;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();//返回数组的长度
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	 //System.out.println("适配器里data = " + data);
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_classificationdetails_list, null);
			holder.name = (TextView) convertView
					.findViewById(R.id.textview_item_cd_list_name);
			holder.create_time = (TextView) convertView
					.findViewById(R.id.textview_item_cd_list_create_time);
			convertView.setTag(holder);//绑定viewHolder对象
		}else {
			holder = (Holder) convertView.getTag();//取出viewHolder对象
		}

		holder.name.setText(data.get(position).getName());
		holder.create_time.setText(data.get(position).getCreate_time());
		return convertView;
	}

	class Holder {
		private TextView name;
		private TextView create_time;
	}

}
