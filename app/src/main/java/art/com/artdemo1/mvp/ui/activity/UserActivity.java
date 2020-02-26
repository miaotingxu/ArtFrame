package art.com.artdemo1.mvp.ui.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.paginate.Paginate;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

import art.com.artdemo1.R;
import art.com.artdemo1.mvp.presenter.UserPresenter;
import art.com.artdemo1.mvp.ui.adapter.UserAdapter;
import butterknife.BindView;
import me.jessyan.art.base.BaseActivity;
import me.jessyan.art.base.DefaultAdapter;
import me.jessyan.art.mvp.IView;
import me.jessyan.art.mvp.Message;
import me.jessyan.art.utils.ArtUtils;

import static me.jessyan.art.utils.Preconditions.checkNotNull;

/**
 * ================================================
 * 展示 View 的用法
 * ================================================
 */
public class UserActivity extends BaseActivity<UserPresenter>
        implements IView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Paginate mPaginate;
    private boolean isLoadingMore;
    private RxPermissions mRxPermissions;
    private UserAdapter mAdapter;

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_user;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initRecyclerView();
        mRecyclerView.setAdapter(mAdapter);
        initPaginate();
        mPresenter.requestUsers(Message.obtain(this, new Object[]{true}));//打开app时自动加载列表
    }

    @Override
    @Nullable
    public UserPresenter obtainPresenter() {
        this.mRxPermissions = new RxPermissions(this);
        this.mAdapter = new UserAdapter(new ArrayList<>());
        return new UserPresenter(ArtUtils.obtainAppComponentFromContext(this), mAdapter, mRxPermissions);
    }

    @Override
    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArtUtils.snackbarText(message);
    }

    @Override
    public void handleMessage(@NonNull Message message) {
        checkNotNull(message);
        switch (message.what) {
            case 0:
                isLoadingMore = true;//开始加载更多
                break;
            case 1:
                isLoadingMore = false;//结束加载更多
                break;
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.requestUsers(Message.obtain(this, new Object[]{true}));
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ArtUtils.configRecyclerView(mRecyclerView, new GridLayoutManager(this, 2));
    }


    /**
     * 初始化Paginate,用于加载更多
     */
    private void initPaginate() {
        if (mPaginate == null) {
            Paginate.Callbacks callbacks = new Paginate.Callbacks() {
                @Override
                public void onLoadMore() {
                    mPresenter.requestUsers(Message.obtain(UserActivity.this, new Object[]{false}));
                }

                @Override
                public boolean isLoading() {
                    return isLoadingMore;
                }

                @Override
                public boolean hasLoadedAllItems() {
                    return false;
                }
            };

            mPaginate = Paginate.with(mRecyclerView, callbacks)
                    .setLoadingTriggerThreshold(0)
                    .build();
            mPaginate.setHasMoreDataToLoad(false);
        }
    }

    @Override
    protected void onDestroy() {
        DefaultAdapter.releaseAllHolder(mRecyclerView);//super.onDestroy()之后会unbind,所有view被置为null,所以必须在之前调用
        super.onDestroy();
        this.mRxPermissions = null;
        this.mPaginate = null;
    }
}
