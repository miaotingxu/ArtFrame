package art.com.artdemo1.mvp.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import art.com.artdemo1.R;
import art.com.artdemo1.mvp.model.entity.User;
import butterknife.BindView;
import me.jessyan.art.base.BaseHolder;
import me.jessyan.art.base.DefaultAdapter;
import me.jessyan.art.di.component.AppComponent;
import me.jessyan.art.http.imageloader.ImageLoader;
import me.jessyan.art.http.imageloader.glide.ImageConfigImpl;
import me.jessyan.art.utils.ArtUtils;

/**
 * ================================================
 * 展示 {@link BaseHolder} 的用法
 * <p>
 * Created by JessYan on 9/4/16 12:56
 *
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class UserItemHolder extends BaseHolder<User> {

    @BindView(R.id.iv_avatar)
    ImageView mAvatar;
    @BindView(R.id.tv_name)
    TextView mName;

    private AppComponent mAppComponent;
    /**
     * 用于加载图片的管理类, 默认使用 Glide, 使用策略模式, 可替换框架
     */
    private ImageLoader mImageLoader;

    public UserItemHolder(View itemView) {
        super(itemView);
        //可以在任何可以拿到Application的地方, 拿到AppComponent, 从而得到用 Dagger 管理的单例对象
        mAppComponent = ArtUtils.obtainAppComponentFromContext(itemView.getContext());
        mImageLoader = mAppComponent.imageLoader();
    }

    @Override
    public void setData(@NonNull User data, int position) {
        mName.setText(data.getLogin());

        //itemView 的 Context 就是 Activity, Glide 会自动处理并和该 Activity 的生命周期绑定
        mImageLoader.loadImage(itemView.getContext(),
                ImageConfigImpl
                        .builder()
                        .url(data.getAvatarUrl())
                        .imageView(mAvatar)
                        .build());
    }

    /**
     * 在 Activity 的 onDestroy 中使用 {@link DefaultAdapter# releaseAllHolder(RecyclerView)} 方法 (super.onDestroy() 之前)
     * {@link BaseHolder#onRelease()} 才会被调用, 可以在此方法中释放一些资源
     */
    @Override
    protected void onRelease() {
        //只要传入的 Context 为 Activity, Glide 就会自己做好生命周期的管理, 其实在上面的代码中传入的 Context 就是 Activity
        //所以在 onRelease 方法中不做 clear 也是可以的, 但是在这里想展示一下 clear 的用法
        mImageLoader.clear(mAppComponent.application(), ImageConfigImpl.builder()
                .imageViews(mAvatar)
                .build());
        this.mAvatar = null;
        this.mName = null;
        this.mAppComponent = null;
        this.mImageLoader = null;
    }
}
