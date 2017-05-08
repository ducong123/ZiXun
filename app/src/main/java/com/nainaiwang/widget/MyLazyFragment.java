package com.nainaiwang.widget;

import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2017/1/19.
 */
public abstract class MyLazyFragment extends Fragment {

    //判断当前的fragment是否可见
    protected boolean isVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
        }
    }

    protected abstract void lazyLoad();

}
