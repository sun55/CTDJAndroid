package com.ctdj.djandroid.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.ReportBean;

import java.util.List;

/**
 * 举报adapter
 */
public class ReportAdapter extends BaseQuickAdapter<ReportBean.Data, ReportAdapter.UserHolder> {

    private int selectIndex = -1;

    public ReportAdapter(List<ReportBean.Data> data) {
        super(R.layout.report_item_layout, data);
    }

    @Override
    protected void convert(@NonNull UserHolder helper, ReportBean.Data item) {
        helper.reportName.setText(item.getTypeName());
        if (helper.getAdapterPosition() == selectIndex) {
            helper.reportName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.report_item_checked, 0);
        } else {
            helper.reportName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.report_item_uncheck, 0);
        }

        helper.reportName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectIndex == helper.getAdapterPosition()) {
                    selectIndex = -1;
                } else {
                    selectIndex = helper.getAdapterPosition();
                }
                notifyDataSetChanged();
            }
        });
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public ReportBean.Data getSelectItem() {
        return getItem(selectIndex);
    }

    public class UserHolder extends BaseViewHolder {

        public TextView reportName;

        public UserHolder(View view) {
            super(view);
            reportName = view.findViewById(R.id.tv_report_name);
        }
    }
}
