package com.nainaiwang.zixun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nainaiwang.adapter.ChannelListviewAdapter;
import com.nainaiwang.adapter.SearchHistoryAdapter;
import com.nainaiwang.bean.ChannelList;
import com.nainaiwang.bean.Details;
import com.nainaiwang.bean.SearchHistory;
import com.nainaiwang.myinterface.SearchHistoryInterface;
import com.nainaiwang.utils.DbUtils;
import com.nainaiwang.utils.UrlUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity implements View.OnClickListener {

    private TextView searchbtn, cancle;//搜索按钮，取消按钮
    private EditText input;//输入框
    private LinearLayout nothing;
    private ListView searchHistory;//搜索记录，搜索结果
    private PullToRefreshListView searchResult;

    private DbManager db;//数据库管理器
    private List<SearchHistory> shList = new ArrayList<>();//搜索记录数据源
    private List<SearchHistory> shList2 = new ArrayList<>();//从数据库中获取的数据源
    private SearchHistoryAdapter searchHistoryAdapter;

    private List<ChannelList> channelLists = new ArrayList<>();//搜索出来的结果数据源
    private ChannelListviewAdapter channelListviewAdapter;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
        initData();

        // 判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

    }

    private void initData() {

        setSoftInputFromWindow();

        searchHistoryAdapter = new SearchHistoryAdapter(SearchActivity.this, shList);
        searchHistory.setAdapter(searchHistoryAdapter);


        //数据库设置
        DbManager.DaoConfig daoConfig = DbUtils.getDaoConfig();
        db = x.getDb(daoConfig);

        findData();

        searchHistoryAdapter.getDeleteNum(new SearchHistoryInterface() {
            @Override
            public void passValue(int id) {
                deleteData(id);
            }
        });

        searchHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == shList.size() - 1) {
                    deleteData(-1);
                } else {
                    channelLists.clear();
                    input.setText(shList.get(position).getStr());
                    search(shList.get(position).getStr());
                }
            }
        });

        searchResult.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        ILoadingLayout endLabels = searchResult.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载更多...");
        endLabels.setReleaseLabel("放开加载更多...");
        endLabels.setRefreshingLabel("正在加载...");

        channelListviewAdapter = new ChannelListviewAdapter(SearchActivity.this, channelLists);
        searchResult.setAdapter(channelListviewAdapter);

        searchResult.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                downResultJson();
            }
        });

        searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent searchToDetails = new Intent(SearchActivity.this, DetailsActivity.class);
                searchToDetails.putExtra("id", channelLists.get(position - 1).getId());
                startActivity(searchToDetails);
            }
        });

    }

    /**
     * 添加搜索历史
     */
    private void addSearchHistory(String str) {
        Boolean b = false;
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < shList.size(); i++) {
            stringList.add(shList.get(i).getStr());
        }
        if (stringList.contains(str.trim())) {
            b = true;
        }
        if (!b) {
            SearchHistory sh = new SearchHistory();
            sh.setStr(str.trim());
            try {
                db.save(sh);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 从数据库中查找搜索历史数据
     */
    private void findData() {
        Log.i("SearchActivity", "第一次" + shList.toString());
        shList2.clear();
        shList.clear();
        try {
            shList2 = db.findAll(SearchHistory.class);
            if (shList2 != null) {
                if (shList2.size() == 0) {
                    searchHistory.setVisibility(View.GONE);
                    nothing.setVisibility(View.VISIBLE);
                    searchResult.setVisibility(View.GONE);
                } else {
                    Log.i("SearchActivity", "第二次" + shList2.toString());
                    searchHistory.setVisibility(View.VISIBLE);
                    nothing.setVisibility(View.GONE);
                    searchResult.setVisibility(View.GONE);
                    for (int i = 0; i < shList2.size(); i++) {
                        shList.add(shList2.get(i));
                    }
                    SearchHistory s = new SearchHistory();
                    s.setStr("清除搜索记录");
                    shList.add(s);
                    searchHistoryAdapter.notifyDataSetChanged();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置软键盘搜索按钮
     */
    private void setSoftInputFromWindow() {
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    String str = input.getText().toString().trim();
                    if (TextUtils.isEmpty(str)) {
                        Toast.makeText(SearchActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                    } else {
                        addSearchHistory(str);
                        channelLists.clear();
                        search(str);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void initView() {

        searchbtn = (TextView) findViewById(R.id.textview_search_searchbtn);
        cancle = (TextView) findViewById(R.id.textview_search_cancle);
        input = (EditText) findViewById(R.id.edittext_search_input);
        nothing = (LinearLayout) findViewById(R.id.linearlayout_search_nothing);
        searchHistory = (ListView) findViewById(R.id.listview_search_record);
        searchResult = (PullToRefreshListView) findViewById(R.id.listview_search_result);

        searchbtn.setOnClickListener(this);
        cancle.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.textview_search_searchbtn:
                String str = input.getText().toString().trim();
                if (TextUtils.isEmpty(str)) {
                    Toast.makeText(SearchActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                } else {
                    addSearchHistory(str);
                }
                channelLists.clear();
                search(str);//搜索
                break;
            case R.id.textview_search_cancle:
                finish();
                break;
        }
    }

    /**
     * 搜索
     */
    private void search(String str) {
        searchHistory.setVisibility(View.GONE);
        nothing.setVisibility(View.GONE);
        searchResult.setVisibility(View.VISIBLE);
        downResultJson();
    }

    /**
     * 下载搜索结果的json数据,参数如果是0就代表是点击搜索图标和软键盘搜索，如果是1就代表点击搜索历史搜索
     */
    private void downResultJson() {

        final String str = input.getText().toString().trim();
        RequestParams resultParams = new RequestParams(UrlUtils.TUIJIAN);
        resultParams.addBodyParameter("keyword", str);
        resultParams.addBodyParameter("page", "" + page);
        x.http().post(resultParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                searchResult.onRefreshComplete();
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("success");
                    if ("1".equals(code)) {
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("info");
                        if (jsonArray1.length() == 0) {
                            Toast.makeText(SearchActivity.this, "没有更多关于" + str + "的数据", Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                                ChannelList channelList = new ChannelList();
                                channelList.setId(jsonObject2.getString("id"));
                                channelList.setName(jsonObject2.getString("name"));
                                channelList.setAuthor(jsonObject2.getString("author"));
                                channelList.setCreate_time(jsonObject2.getString("create_time"));

                                JSONArray jsonArray2 = jsonObject2.getJSONArray("cover");
                                List<String> urlList = new ArrayList<>();
                                for (int n = 0; n < jsonArray2.length(); n++) {
                                    urlList.add(jsonArray2.getString(n));
                                }
                                channelList.setChannelImageList(urlList);
                                channelLists.add(channelList);
                                channelListviewAdapter.notifyDataSetChanged();
                            }
                        }

                    } else {
                        Toast.makeText(SearchActivity.this, "请求网络失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                searchResult.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                searchResult.onRefreshComplete();
            }

            @Override
            public void onFinished() {
                searchResult.onRefreshComplete();
            }
        });
    }

    /**
     * 删除数据库中该id的值
     *
     * @param id
     */
    private void deleteData(int id) {
        if (id == -1) {
            try {
                db.delete(SearchHistory.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
        } else {
            try {
                db.deleteById(SearchHistory.class, id);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        findData();
    }

}
