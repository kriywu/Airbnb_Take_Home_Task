package com.rengao.homework;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rengao.homework.Abstract.BindHolder;
import com.rengao.homework.Abstract.ResultCallback;
import com.rengao.homework.Holder.FootHolder;
import com.rengao.homework.Holder.ItemHolder;
import com.rengao.homework.Module.Project;
import com.rengao.homework.Util.CacheDbCRUD;
import com.rengao.homework.Util.Constants;
import com.rengao.homework.Util.HttpStringUtil;
import com.rengao.homework.Util.OkHttpEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity implements ResultCallback {
    private static final String TAG = "MainActivity";

    Toolbar toolbar;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView tvWarm; // 在联网失败或者not found的时候显示提醒用户

    List<Project> projects = new ArrayList<>(); // 项目表
    String queryString; // 用于下拉刷新的时候使用
    boolean end = false;// 是否到底
    int page = 1;// 当前页序号
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        toolbar = findViewById(R.id.bar);
        swipeRefreshLayout = findViewById(R.id.srl);
        tvWarm = findViewById(R.id.tv_warm);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setAdapter(new GithubAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    int position = ((LinearLayoutManager) Objects.requireNonNull(layoutManager)).findLastVisibleItemPosition();
                    int visualItems = layoutManager.getChildCount();
                    int sumItems = layoutManager.getItemCount();

                    if (visualItems > 0 && position == sumItems - 1 && sumItems > visualItems) {
                        page++;
                        // 读取缓存
                        List<Project> temp = CacheDbCRUD.getInstance(context).query(queryString, page);
                        if (temp.size() != 0) {
                            projects.addAll(temp);
                            return;
                        }
                        // 如果缓存中没有，联网查询
                        OkHttpEngine.getInstance(getApplicationContext()).get(queryString, page, MainActivity.this);
                    }
                }
            }
        });

        setSupportActionBar(toolbar);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                CacheDbCRUD.getInstance(context).clearOne(queryString);// 刷新的时候，清空该缓存
                OkHttpEngine.getInstance(getApplicationContext()).get(queryString, page, MainActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.hint_name));
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryString = query; // 记录当前查询的字符串，用于刷新
                page = 1; // 在输入框中查询的内容，默认显示第一页
                List<Project> temp = CacheDbCRUD.getInstance(context).query(queryString, page);// 读取缓存
                if (temp.size() != 0) {
                    projects.clear(); // 清空之前的列表
                    projects.addAll(temp); //  添加新的内容
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
                    return true;
                }
                // 没有缓存
                OkHttpEngine.getInstance(getApplicationContext()).get(query, page, MainActivity.this);
                swipeRefreshLayout.setRefreshing(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    /**
     * 访问api成功，但是返回的数据可能是 "[]",表示Not Found
     * @param page 如果page == 1 需要清楚之前的projects列表，否则添加到projects列表中即可
     * @param str  返回的json数据，需要解析
     */
    @Override
    public void onSuccess(int page, String str) {
        // onSuccess被回调说明网络没问题，如果tv_warm显示表明该用户Not Found
        tvWarm.setText(R.string.warm_query);
        swipeRefreshLayout.setRefreshing(false);
        // 如果返回的str为null，显示提示语
        if (str == null) {
            handleNull();
            return;
        }

        // 返回 "[]" 说明没有更多了
        if (str.equals("[]")) {
            end = true; //  标记到底了 "HIT BOTTOM"
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(projects.size());
            return;
        } else {
            end = false; // 没有到底
        }

        // 解析JSON数据
        List<Project> temp = null;
        try {
            temp = HttpStringUtil.parse(str);
            if (page == 1) projects.clear();
            projects.addAll(temp);
        } catch (Exception e) {
            handleNull(); // 解析异常 说明用户Not Found
            return;
        }
        CacheDbCRUD.getInstance(context).insertOne(temp, page);// 缓存到数据库
        end = (temp.size() != Constants.PAGE_SIZE);// 判断是否到底

        recyclerView.setVisibility(View.VISIBLE);
        tvWarm.setVisibility(View.GONE);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
    }

    /**
     * 无网络的时候会调用
     * @param page    区别带给你前请求
     * @param request
     * @param e
     */
    @Override
    public void onFailed(int page, Request request, Exception e) {
        Log.d(TAG, "onFailed: ");
        swipeRefreshLayout.setRefreshing(false);
        tvWarm.setText(R.string.warm_internet);
        handleNull();
    }

    /**
     * 处理返回数据未空或者无效
     */
    private void handleNull() {
        projects.clear();
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        recyclerView.setVisibility(View.GONE); //隐藏列表
        tvWarm.setVisibility(View.VISIBLE); // 显示提示语
    }


    /**
     * recycler view 的适配器
     */
    class GithubAdapter extends RecyclerView.Adapter<BindHolder> {
        final static int TYPE_ITEM = 0; // ItemHolder
        final static int TYPE_FOOT = 1; // FootHolder

        @NonNull
        @Override
        public BindHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View view = getLayoutInflater().inflate(R.layout.view_item, parent, false);
                return new ItemHolder(MainActivity.this, view);
            } else {
                View view = getLayoutInflater().inflate(R.layout.view_foot, parent, false);
                return new FootHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull BindHolder holder, int position) {
            if (position >= projects.size()) holder.bind(end); // FootHolder，如果是true表示到底了，否则显示Loading
            else holder.bind(projects.get(position));
        }

        @Override
        public int getItemCount() {
            if (projects.isEmpty()) return 0;
            else return projects.size() + 1; // 用于FootHolder显示Hit Bottom
        }

        @Override
        public int getItemViewType(int position) {
            if (position >= projects.size()) return TYPE_FOOT;
            else return TYPE_ITEM;
        }
    }

    // 关闭应用的时候清空表
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CacheDbCRUD.getInstance(getApplicationContext()).clearAll();
    }
}
