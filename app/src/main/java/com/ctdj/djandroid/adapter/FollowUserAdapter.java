package com.ctdj.djandroid.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.UserListBean;
import com.ctdj.djandroid.view.CircleImageView;

import java.util.List;

import static com.ctdj.djandroid.fragment.UserListFragment.FOLLOW;

/**
 * 关注粉丝adapter
 */
public class FollowUserAdapter extends BaseQuickAdapter<UserListBean.Rows, FollowUserAdapter.UserHolder> {

    int type = FOLLOW;

    public FollowUserAdapter(List<UserListBean.Rows> data, int type) {
        super(R.layout.user_item_layout, data);
        this.type = type;
    }

    @Override
    protected void convert(@NonNull UserHolder helper, UserListBean.Rows item) {
        if (type == FOLLOW) {
            Glide.with(mContext).load(item.getFheadimg()).into(helper.avatar);
            helper.name.setText(TextUtils.isEmpty(item.getRemarkName()) ? item.getFmname() : item.getRemarkName() + "(" + item.getFmname() + ")");
            helper.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, item.getFsex() == 1 ? R.drawable.male_small_icon : R.drawable.female_small_icon, 0);
        } else {
            Glide.with(mContext).load(item.getHeadimg()).into(helper.avatar);
            helper.name.setText(TextUtils.isEmpty(item.getRemarkName()) ? item.getMname() : item.getRemarkName() + "(" + item.getMname() + ")");
            helper.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, item.getSex() == 1 ? R.drawable.male_small_icon : R.drawable.female_small_icon, 0);
        }
        if (item.getFtype() == 1) {
            helper.follow.setText("相互关注");
            helper.follow.setBackgroundResource(R.drawable.radius_10_22252f);
        } else {
            if (type == FOLLOW) {
                helper.follow.setText("已关注");
                helper.follow.setBackgroundResource(R.drawable.radius_10_22252f);
            } else {
                helper.follow.setText("关注");
                helper.follow.setBackgroundResource(R.drawable.radius_10_5252fe);
            }
        }
    }

    public class UserHolder extends BaseViewHolder {

        public CircleImageView avatar;
        public TextView name;
        public TextView playStatus;
        public TextView follow;

        public UserHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.iv_avatar);
            name = view.findViewById(R.id.tv_nickname);
            playStatus = view.findViewById(R.id.tv_play_status);
            follow = view.findViewById(R.id.tv_follow);
        }
    }
}
