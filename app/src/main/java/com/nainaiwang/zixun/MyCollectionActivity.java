package com.nainaiwang.zixun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nainaiwang.adapter.ChannelListviewAdapter;
import com.nainaiwang.adapter.CollectionAdapter;
import com.nainaiwang.bean.Collections;
import com.nainaiwang.bean.HomePageChannels;
import com.nainaiwang.utils.DbUtils;
import com.nainaiwang.utils.NetInfoUtils;
import com.nainaiwang.utils.UrlUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/2 0002.
 */

public class MyCollectionActivity extends Activity implements View.OnClickListener{
    private RelativeLayout back;
/*    private ListView listView;//收藏列表*/
    private Editor editor;
    private List<Collections> cdList = new ArrayList<Collections>();//收藏数据源
    private CollectionAdapter adapter;//收藏适配器
    private PullToRefreshListView myCollResult;
    private DbManager db;//数据库管理器
    private int page=1;
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);
        initView();// 初始化控件
        initData();// 初始化数据

    }
    private void initView() {
        // TODO Auto-generated method stub
        back = (RelativeLayout)findViewById(R.id.relativelayout_mycollection_back);
       // listView = (ListView)findViewById(R.id.listview_mycollection_collection);
        myCollResult = (PullToRefreshListView)findViewById(R.id.listview_mycollection_result);
        back.setOnClickListener(this);
    }

    private void initData() {
        // TODO Auto-generated method stub
        //数据库设置
        DbManager.DaoConfig daoConfig = DbUtils.getDaoConfig();
        db = x.getDb(daoConfig);
        if (NetInfoUtils.isNetworkConnected(MyCollectionActivity.this)) {
            collectList(page);
        }else {
            adapter.notifyDataSetChanged();
//            Toast.makeText(getActivity(), "当前没有网络，请进行网络设置", Toast.LENGTH_SHORT).show();
        }

        myCollResult.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout startLabels = myCollResult.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");
        startLabels.setReleaseLabel("放开立即刷新...");
        startLabels.setRefreshingLabel("正在刷新...");
        ILoadingLayout endLabels= myCollResult.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载更多...");
        endLabels.setReleaseLabel("放开加载更多...");
        endLabels.setRefreshingLabel("正在加载...");
        adapter = new CollectionAdapter(cdList,MyCollectionActivity.this);
        myCollResult.setAdapter(adapter);
        myCollResult.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                System.out.println("刷新");
                collectList(page);
                myCollResult.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myCollResult.onRefreshComplete();
                    }
                }, 1000);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                System.out.println("cdlist="+cdList.size());
              if(cdList.size()>0){
                  page++;
                  collectList(page);
              }else {
                  Toast.makeText(MyCollectionActivity.this, "没有更多的数据啦", Toast.LENGTH_SHORT).show();
              }
                myCollResult.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myCollResult.onRefreshComplete();
                    }
                }, 1000);
            }

        });

        myCollResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myDetails = new Intent(MyCollectionActivity.this, DetailsActivity.class);
                myDetails.putExtra("id", cdList.get(position-1).getArticle_id());
                System.out.println("id"+cdList.get(position-1).getArticle_id());
                startActivity(myDetails);
            }
        });



    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.relativelayout_mycollection_back:

                finish();// 退出当前界面
                break;
            default:
                break;
        }
    }

    private void collectList(final int  page) {
        cdList.clear();
        // TODO Auto-generated method stub
        RequestParams myColListParams = new RequestParams(UrlUtils.COLLECTIONLIST);
        myColListParams.addBodyParameter("page", ""+page);
        x.http().post(myColListParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                // TODO Auto-generated method stub
           //  System.out.println(" = " + arg0 +"信息");
                try {
              /*      JSONObject jsonObject = new JSONObject(arg0);*/
                    JSONArray jsonArray1 = new JSONArray(arg0);
                    System.out.print("有几条数据"+jsonArray1.length());
                    if (jsonArray1.length() == 0) {
                        Toast.makeText(MyCollectionActivity.this, "没有收藏数据", Toast.LENGTH_SHORT).show();
                    }else{
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                            Collections cdLists = new Collections();
                            cdLists.setId(jsonObject2.getInt("id"));
                            cdLists.setName(jsonObject2.getString("name"));
                            cdLists.setArticle_id(jsonObject2.getString("article_id"));
                            cdLists.setCreate_time(jsonObject2.getString("create_time"));
                            cdLists.setUrl(jsonObject2.getString("url"));
                            cdList.add(cdLists);
                            adapter.notifyDataSetChanged();
                            try {
                                db.save(cdLists);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
            }


        });
    }
}
