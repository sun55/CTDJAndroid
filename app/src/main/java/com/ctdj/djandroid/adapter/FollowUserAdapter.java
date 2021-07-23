package com.ctdj.djandroid.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.UserListBean;

import java.util.List;

/**
 * 关注粉丝adapter
 */
public class FollowUserAdapter extends BaseQuickAdapter<UserListBean.Row, FollowUserAdapter.UserHolder> {

    public FollowUserAdapter(List<UserListBean.Row> data, int type) {
        super(R.layout.user_item_layout, data);
    }

    @Override
    protected void convert(@NonNull UserHolder helper, UserListBean.Row item) {

    }

    public class UserHolder extends BaseViewHolder {


        public UserHolder(View view) {
            super(view);

        }
    }
}
