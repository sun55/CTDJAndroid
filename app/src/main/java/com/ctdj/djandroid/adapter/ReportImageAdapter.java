package com.ctdj.djandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.view.CircleImageView;

import java.util.List;

/**
 * @Description : 举报--图片
 */
public class ReportImageAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    private static final int MAX_COUNT = 3;

    public ReportImageAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size() == MAX_COUNT ? list.size() : list.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.report_image_item, null);
//            view.setLayoutParams(new GridView.LayoutParams(DisplayUtil.dip2px(context, 106), DisplayUtil.dip2px(context, 106)));
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (position == list.size()) {
            viewHolder.tvDelete.setVisibility(View.GONE);
            Glide.with(parent.getContext()).load(R.drawable.report_add_image_icon).centerCrop().into(viewHolder.iv);
        } else {
            viewHolder.tvDelete.setVisibility(View.VISIBLE);
            Glide.with(parent.getContext()).load(list.get(position)).into(viewHolder.iv);
            viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getList().size() <= 1) {
                        Utils.showToast(context, "至少要有一张图像");
                        return;
                    }
                    if (listener != null) {
                        deletePhoto(position);
                        listener.onDeleteItem(position);
                    }
                }
            });
        }
        return view;
    }

    public void addPhoto(String imgUrl) {
        list.add(imgUrl);
        notifyDataSetChanged();
    }

    public int getListSize() {
        return list.size();
    }

    public List<String> getList() {
        return list;
    }

    public void setPhotos(List<String> imgUrl) {
        list.clear();
        list.addAll(imgUrl);
        LogUtil.e("imgUris:"  + imgUrl.size() + ",,, list:" + list.size());
        notifyDataSetChanged();
    }

    public void deletePhoto(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        CircleImageView iv;
        TextView tvDelete;

        public ViewHolder(View view) {
            iv = view.findViewById(R.id.iv);
            tvDelete = view.findViewById(R.id.tv_delete);
        }
    }

    OnDeleteItemListener listener;

    public void setOnDeleteItemListener(OnDeleteItemListener listener) {
        this.listener = listener;
    }

    public interface OnDeleteItemListener {
        void onDeleteItem(int position);
    }
}
