package com.ctdj.djandroid.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctdj.djandroid.R;

import java.util.List;

/**
 * 比赛价格adapter
 */
public class PlayPriceAdapter extends BaseQuickAdapter<String, PlayPriceAdapter.PlayPriceHolder> {

    private int playType = 1; // 1金币挑战赛 2赏金挑战赛
    private int selectedIndex = 0; // 选择下标

    public PlayPriceAdapter(List<String> data) {
        super(R.layout.play_price_layout, data);
    }

    @Override
    protected void convert(@NonNull PlayPriceHolder helper, String item) {
        helper.tvPrice.setText(item);
        helper.tvPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, playType == 1 ? R.drawable.gold_icon_2 : R.drawable.diamond_icon, 0);
        if (helper.getAdapterPosition() == selectedIndex) {
            helper.rlItem.setBackgroundResource(R.drawable.play_item_select_bg);
            helper.ivSelect.setVisibility(View.VISIBLE);
        } else {
            helper.rlItem.setBackgroundResource(R.drawable.radius_5_22252f);
            helper.ivSelect.setVisibility(View.GONE);
        }

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedIndex == helper.getAdapterPosition()) {
                    return;
                }
                selectedIndex = helper.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    public void setPlayType(int playType) {
        this.playType = playType;
        notifyDataSetChanged();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    public String getSelectedItem() {
        return getItem(selectedIndex);
    }

    public class PlayPriceHolder extends BaseViewHolder {

        RelativeLayout rlItem;
        TextView tvPrice;
        ImageView ivSelect;

        public PlayPriceHolder(View view) {
            super(view);
            rlItem = view.findViewById(R.id.rl_item);
            tvPrice = view.findViewById(R.id.tv_price);
            ivSelect = view.findViewById(R.id.iv_select);
        }
    }
}
