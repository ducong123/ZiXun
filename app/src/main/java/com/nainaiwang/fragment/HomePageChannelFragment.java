package com.nainaiwang.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nainaiwang.adapter.ChannelListviewAdapter;
import com.nainaiwang.adapter.HomePageLunbotuAdapter;
import com.nainaiwang.bean.ChannelList;
import com.nainaiwang.bean.HomePageChannels;
import com.nainaiwang.bean.Lunbotu;
import com.nainaiwang.utils.DbUtils;
import com.nainaiwang.utils.NetInfoUtils;
import com.nainaiwang.utils.UrlUtils;
import com.nainaiwang.widget.ChildViewPager;
import com.nainaiwang.widget.MyLazyFragment;
import com.nainaiwang.zixun.DetailsActivity;
import com.nainaiwang.zixun.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/28.
 */
public class HomePageChannelFragment extends MyLazyFragment {

    private PullToRefreshListView listview;
    private ChannelListviewAdapter adapter;
    private List<ChannelList> channelLists = new ArrayList<>();
    private List<ChannelList> channelListsCopy = new ArrayList<>();
    private List<ChannelList> channelListsCopy2 = new ArrayList<>();
    private List<Lunbotu> lunbotuList = new ArrayList<>();

    private int page = 1;
    private String id;

    private boolean isReady = false;//表示初始化已完成
    private boolean mHasLoadedOnce;//是否已经被加载过一次，第二次就不去请求数据了

    View view;
    private ChildViewPager lunbotu;
    private LinearLayout dotsLayout;// 轮播图上的小点
    private TextView lunbotuTv;//轮播图上的文字
    private DbManager db;//数据库管理器

    private HomePageLunbotuAdapter lunbotuAdapter;

    private boolean lunbotuFlag = false;

    // handler定时切换轮播图的图片
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            lunbotu.setCurrentItem(lunbotu.getCurrentItem() + 1);// 设置轮播图下一个图片
//            if (timeFlag){
            handler.sendEmptyMessageDelayed(0, 3000);// 每3秒切换一次图片
//            }

        }

        ;
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_homepage_channel, container, false);
            isReady = true;
            lazyLoad();
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        return view;
    }

    private void initView() {
        listview = (PullToRefreshListView) view.findViewById(R.id.listview_channelfragment_listview);
    }


    @Override
    protected void lazyLoad() {
        if (!isReady || !isVisible || mHasLoadedOnce) {
            return;
        }
        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initData() {
        Bundle bundle = getArguments();
        id = bundle.getString("key");

        listview.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout startLabels = listview.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");
        startLabels.setReleaseLabel("放开立即刷新...");
        startLabels.setRefreshingLabel("正在刷新...");
        ILoadingLayout endLabels = listview.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载更多...");
        endLabels.setReleaseLabel("放开加载更多...");
        endLabels.setRefreshingLabel("正在加载...");


        if ("0".equals(id)) {
            RelativeLayout hearderViewLayout = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.viewpager_homepage_lunbotu, null);
            ListView lv = listview.getRefreshableView();
            lv.addHeaderView(hearderViewLayout);
            lunbotu = (ChildViewPager) hearderViewLayout.findViewById(R.id.viewpager_homepage);
            dotsLayout = (LinearLayout) hearderViewLayout
                    .findViewById(R.id.linearlayout_homepagelunbotu_dotlayout);
            lunbotuTv = (TextView) hearderViewLayout.findViewById(R.id.textview_homepagelunbotu_text);
            setLunbotu();
            lunbotu.setOnSingleTouchListener(new ChildViewPager.OnSingleTouchListener() {
                @Override
                public void onSingleTouch() {
                    //轮播图的点击事件
                    Intent lunbotuToDetails = new Intent(getActivity(), DetailsActivity.class);
                    lunbotuToDetails.putExtra("id", lunbotuList.get(lunbotu.getCurrentItem() % lunbotuList.size()).getId());
                    startActivity(lunbotuToDetails);
                }
            });

        }


        adapter = new ChannelListviewAdapter(getActivity(), channelLists);
        listview.setAdapter(adapter);


        //数据库设置
        DbManager.DaoConfig daoConfig = DbUtils.getDaoConfig();
        db = x.getDb(daoConfig);

//        findData();
//        adapter.notifyDataSetChanged();

        if (NetInfoUtils.isNetworkConnected(getActivity())) {
            downJson(id, page, "");
        } else {
            findData();
            adapter.notifyDataSetChanged();
//            Toast.makeText(getActivity(), "当前没有网络，请进行网络设置", Toast.LENGTH_SHORT).show();
        }


        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                if (channelLists.size() != 0) {


                if (NetInfoUtils.isNetworkConnected(getActivity()) && channelLists.size() > 0) {
                    downJson(id, 1, channelLists.get(0).getUpdate_time());
                } else {
                    listview.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listview.onRefreshComplete();
                        }
                    }, 1000);
                    Toast.makeText(getActivity(), "当前没有网络", Toast.LENGTH_SHORT).show();
                }

//                } else {
//                    listview.onRefreshComplete();
//                    Toast.makeText(getActivity(), "没有更多的数据啦", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (NetInfoUtils.isNetworkConnected(getActivity())) {
                    page++;
                    downJson(id, page, "");
                } else {
                    listview.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listview.onRefreshComplete();
                        }
                    }, 1000);
                    Toast.makeText(getActivity(), "当前没有网络", Toast.LENGTH_SHORT).show();
                }

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mainToDetails = new Intent(getActivity(), DetailsActivity.class);
                mainToDetails.putExtra("id", channelLists.get(position - 2).getId());
                startActivity(mainToDetails);
            }
        });

    }

    /**
     * 设置轮播图
     */
    private void setLunbotu() {
//        lunbotuList.clear();
        lunbotu.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                updateDot();// 每次滑动轮播图的时候更新小点
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 更新小点，把当前图片所对应的小点显示为特殊色
     */
    private void updateDot() {
        // TODO Auto-generated method stub
        if (lunbotuList.size() != 0) {
            int currentPage = lunbotu.getCurrentItem() % lunbotuList.size();// 获得当前页的position值

            for (int i = 0; i < dotsLayout.getChildCount(); i++) {
                // 如果当前页为i，则第i个小点设置为特殊色
                dotsLayout.getChildAt(i).setEnabled(i == currentPage);
                lunbotuTv.setText("" + lunbotuList.get(currentPage).getShort_content());
            }
        }

    }

    /**
     * 初始化轮播图的小点
     */
    private void initDots() {
        // TODO Auto-generated method stub
        dotsLayout.removeAllViews();
        for (int i = 0; i < lunbotuList.size(); i++) {
            View view = new View(getActivity());// 得到小球对象
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(12, 12);// 设置每个小球的宽高参数
            if (i != 0) {// 第一个点不需要左边距
                params.leftMargin = 15;// 设置其他小点的左边距未5px
            }
            view.setLayoutParams(params);// 给小点添加参数
            view.setBackgroundResource(R.drawable.selector_dots);// 给view设置小点图片背景
            dotsLayout.addView(view);// 添加小点
        }
    }

    /**
     * 下载json数据
     */
    private void downJson(final String id, final int p, final String time) {

        channelListsCopy.clear();
        RequestParams jsonParams;
        if ("".equals(time)) {
            if ("0".equals(id)) {
                jsonParams = new RequestParams(UrlUtils.TUIJIAN);
                jsonParams.addBodyParameter("page", "" + p);
            } else {
                jsonParams = new RequestParams(UrlUtils.TUIJIAN);
                jsonParams.addBodyParameter("id", id);
                jsonParams.addBodyParameter("page", "" + p);
            }
        } else {
            jsonParams = new RequestParams(UrlUtils.TUIJIAN);
            jsonParams.addBodyParameter("id", id);
            jsonParams.addBodyParameter("update_time", time);
        }

        x.http().post(jsonParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("MainActivity", result);
//                timeFlag = false;
//                handler.sendEmptyMessage(1);
                mHasLoadedOnce = true;
                listview.onRefreshComplete();
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("success");
                    if ("1".equals(code)) {

                        if (lunbotuFlag == false) {
                            if ("0".equals(id)) {
                                JSONArray jsonArray3 = jsonObject1.getJSONArray("slides");
                                if (jsonArray3 != null && jsonArray3.length() != 0) {
                                    lunbotuList.clear();
                                    for (int k = 0; k < jsonArray3.length(); k++) {
                                        JSONObject jsonObject5 = jsonArray3.getJSONObject(k);
                                        Lunbotu lunbotu = new Lunbotu();
                                        lunbotu.setId(jsonObject5.getString("id"));
                                        lunbotu.setCover(jsonObject5.getString("cover"));
                                        lunbotu.setShort_content(jsonObject5.getString("short_content"));
                                        lunbotuList.add(lunbotu);
                                    }
                                    initDots();// 初始化小点,这里所有的小点都没有被选中
                                    lunbotuAdapter = new HomePageLunbotuAdapter(lunbotuList,
                                            getActivity());// 得到适配器
                                    lunbotu.setAdapter(lunbotuAdapter);// 给viewpager添加适配器
                                    lunbotu.setCurrentItem(500000 / 2 - ((500000 / 2) % lunbotuList
                                            .size()));// 设置初始化的时候显示的item
                                    handler.sendEmptyMessageDelayed(0, 3000);// 3秒后切换下一个item
//                                timeFlag = true;
                                    updateDot();// 更新小点，把当前图片所对应的小点显示为特殊色
                                }

                                lunbotuFlag = true;

                            }
                        }


                        JSONArray jsonArray1 = jsonObject1.getJSONArray("info");
                        if (jsonArray1.length() == 0) {
                            Toast.makeText(getActivity(), "没有更多的数据啦", Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                                ChannelList channelList = new ChannelList();
                                channelList.setId(jsonObject2.getString("id"));
                                channelList.setName(jsonObject2.getString("name"));
                                channelList.setAuthor(jsonObject2.getString("author"));
                                channelList.setCreate_time(jsonObject2.getString("create_time"));
                                channelList.setUpdate_time(jsonObject2.getString("update_time"));
                                channelList.setChannel(id);
                                channelList.setIs_ad(jsonObject2.getString("is_ad"));

                                JSONArray jsonArray2 = jsonObject2.getJSONArray("cover");
                                channelList.setPicNum("" + jsonArray2.length());
                                List<String> urlList = new ArrayList<>();
                                for (int n = 0; n < jsonArray2.length(); n++) {
                                    urlList.add(jsonArray2.getString(n));
                                }
                                channelList.setChannelImageList(urlList);
                                channelListsCopy.add(channelList);
                            }
                            if ("".equals(time)) {
                                for (int k = 0; k < channelListsCopy.size(); k++) {
                                    channelLists.add(channelListsCopy.get(k));
                                }
                            } else {
                                for (int s = 0; s < channelLists.size(); s++) {
                                    channelListsCopy.add(channelLists.get(s));
                                }
                                channelLists.clear();
                                for (int t = 0; t < channelListsCopy.size(); t++) {
                                    channelLists.add(channelListsCopy.get(t));
                                }
                            }

                            adapter.notifyDataSetChanged();

                            if (page == 1) {
                                try {
                                    db.delete(ChannelList.class, WhereBuilder.b().and("channel", "=", id));
                                    db.save(channelLists);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }

                        }


                    } else {
                        Toast.makeText(getActivity(), "请求网络失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listview.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                listview.onRefreshComplete();
            }

            @Override
            public void onFinished() {
                listview.onRefreshComplete();
            }
        });


    }

    private void findData() {
        channelListsCopy2.clear();
        channelLists.clear();
        try {
            channelListsCopy2 = db.selector(ChannelList.class).where("channel", "=", id).findAll();
            for (int i = 0; i < channelListsCopy2.size(); i++) {
                channelLists.add(channelListsCopy2.get(i));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
