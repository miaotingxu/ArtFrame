package art.com.artdemo1.mvp.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import java.util.List;

import art.com.artdemo1.R;
import art.com.artdemo1.mvp.model.entity.User;
import art.com.artdemo1.mvp.ui.holder.UserItemHolder;
import me.jessyan.art.base.BaseHolder;
import me.jessyan.art.base.DefaultAdapter;

/**
 * ================================================
 * 展示 {@link DefaultAdapter} 的用法
 * ================================================
 */
public class UserAdapter extends DefaultAdapter<User> {

    public UserAdapter(List<User> infos) {
        super(infos);
    }

    @NonNull
    @Override
    public BaseHolder<User> getHolder(@NonNull View v, int viewType) {
        return new UserItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.recycle_list;
    }
}
