package com.ctdj.djandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.adapter.PlayPriceAdapter;
import com.ctdj.djandroid.common.DisplayUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author : Sun
 * @Time : 2021/7/21 14:58
 * @Description :
 */
public class InvitePlayDialog extends Dialog {
    private Context context;
    private RecyclerView rcvPrice;
    private Button btnInvite;
    private TextView tvPlayType;
    private TextView tvPlayArea;
    private TextView tvPlayArea2;
    PlayPriceAdapter adapter;
    String[] prices1 = new String[]{"100", "200", "300", "500", "800", "1000"};
    String[] prices2 = new String[]{"50", "100", "200", "500", "1000", "2000"};
    private String mPlayType = "金币挑战赛"; // 金币挑战赛 赏金挑战赛
    private String mPlayArea = "微信区服";

    public InvitePlayDialog(@NonNull Context context) {
        super(context, R.style.dialog_style);
        this.context = context;
        getWindow().setGravity(Gravity.BOTTOM);
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.invite_play_layout, null);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = DisplayUtil.getScreenWidthPx(context);
        ImageView ivClose = view.findViewById(R.id.iv_close);
        rcvPrice = view.findViewById(R.id.rcv_price);
        btnInvite = view.findViewById(R.id.btn_invite);
        tvPlayType = view.findViewById(R.id.tv_play_type);
        tvPlayArea = view.findViewById(R.id.tv_play_area);
        tvPlayArea2 = view.findViewById(R.id.tv_play_area2);
        RelativeLayout rlTypeArea = view.findViewById(R.id.rl_type_area);
        tvPlayType.setText(mPlayType);
        tvPlayArea.setText(mPlayArea);
        tvPlayArea2.setText(mPlayArea);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rcvPrice.setLayoutManager(new GridLayoutManager(context, 3));
        adapter = new PlayPriceAdapter(new ArrayList<>());
        rcvPrice.setAdapter(adapter);
        adapter.setPlayType(mPlayType);
        adapter.setNewData(Arrays.asList("金币挑战赛".equals(mPlayType) ? prices1 : prices2));
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rlTypeArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPlayInfoDialog dialog = new MyPlayInfoDialog(context);
                dialog.show();
                dialog.selectType("金币挑战赛".equals(mPlayType) ? 1 : 2);
                dialog.selectArea("微信区服".equals(mPlayArea) ? 1 : 2);
                dialog.setListener(new MyPlayInfoDialog.OnBtnClickListener() {
                    @Override
                    public void onConfirmClick(String playType, String playArea) {
                        mPlayType = playType;
                        mPlayArea = playArea;
                        tvPlayType.setText(mPlayType);
                        tvPlayArea.setText(mPlayArea);
                        tvPlayArea2.setText(mPlayArea);
                        adapter.setPlayType(mPlayType);
                    }
                });
            }
        });
    }
}
