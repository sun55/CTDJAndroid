package com.ctdj.djandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.activity.MainActivity;
import com.ctdj.djandroid.audio.MediaManager;
import com.ctdj.djandroid.bean.SendMatchBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.event.CountDownEvent;
import com.ctdj.djandroid.event.MatchTimeEndEvent;
import com.ctdj.djandroid.event.StopRadarEvent;
import com.ctdj.djandroid.event.UpdateMatchUserEvent;
import com.ctdj.djandroid.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Random;

public class RadarScanDialog extends Dialog {
    private Context context;
    private TextView tvCountDown;
    private TextView tvPlayType;
    private ImageView ivRadarBg;
    private CircleImageView avatarMine;
    private CircleImageView avatar1;
    private CircleImageView avatar2;
    private CircleImageView avatar3;
    private CircleImageView avatar4;
    private CircleImageView avatar5;
    private Button btnCancelMatch;
    private int challengeType; // 1 金币挑战赛 2 赏金挑战赛
    private ArrayList<SendMatchBean.Data> avatarList1 = new ArrayList<>();
    private ArrayList<SendMatchBean.Data> avatarList2 = new ArrayList<>();
    private ArrayList<SendMatchBean.Data> avatarList3 = new ArrayList<>();
    private ArrayList<SendMatchBean.Data> avatarList4 = new ArrayList<>();
    private ArrayList<SendMatchBean.Data> avatarList5 = new ArrayList<>();

    public RadarScanDialog(@NonNull Context context, int challengeType) {
        super(context, R.style.dialog_style);
        this.context = context;
        getWindow().setGravity(Gravity.BOTTOM);
        this.setCanceledOnTouchOutside(false);
        this.challengeType = challengeType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.radar_scan_dialog, null);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = DisplayUtil.getScreenWidthPx(context);
        ivRadarBg = view.findViewById(R.id.iv_radar_bg);
        tvCountDown = view.findViewById(R.id.tv_count_down);
        tvPlayType = view.findViewById(R.id.tv_play_type);
        avatar1 = view.findViewById(R.id.avatar_1);
        avatar2 = view.findViewById(R.id.avatar_2);
        avatar3 = view.findViewById(R.id.avatar_3);
        avatar4 = view.findViewById(R.id.avatar_4);
        avatar5 = view.findViewById(R.id.avatar_5);
        avatarMine = view.findViewById(R.id.avatar_mine);
        btnCancelMatch = view.findViewById(R.id.btn_cancel_match);

        ImageView ivClose = view.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnCancelMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NormalDialog dialog = new NormalDialog(context);
                dialog.show();
                dialog.setTitle("是否退出去当前约战匹配？")
                        .setLeftBtnText("取消")
                        .setRightBtnText("退出匹配")
                        .setBtnClickListener(new NormalDialog.OnBtnClickListener() {
                            @Override
                            public void onLeftBtnClick() {

                            }

                            @Override
                            public void onRightBtnClick() {
                                cancelMatch = true;
                                dismiss();
                            }
                        });
            }
        });
        tvPlayType.setText(challengeType == 1 ? "金币挑战赛" : "赏金挑战赛");
        Utils.playRotateAnim(ivRadarBg, 4000, -1);
        Glide.with(context).load(MyApplication.getInstance().getUserInfo().headimg).into(avatarMine);
        fillAvatars();
    }

    @Override
    public void show() {
        super.show();
        EventBus.getDefault().register(this);
        MediaManager.playLocalSound(context, "radar.wav", true, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        MediaManager.release();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(Object event) {
        if (event instanceof CountDownEvent) {
            tvCountDown.setText(((CountDownEvent) event).getSecond() + "");
        } else if (event instanceof UpdateMatchUserEvent) {
            fillAvatars();
        } else if (event instanceof MatchTimeEndEvent) {
            dismiss();
        } else if (event instanceof StopRadarEvent) {
            cancelMatch = true;
            dismiss();
        }
    }

    private void fillAvatars() {
        ArrayList<SendMatchBean.Data> matchUsers = ((MainActivity) context).matchUsers;
        if (matchUsers.size() == 0) {
            return;
        }
        avatar1.clearAnimation();
        avatar2.clearAnimation();
        avatar3.clearAnimation();
        avatar4.clearAnimation();
        avatar5.clearAnimation();
        for (int i = 0; i < matchUsers.size(); i++) {
            switch (i % 5) {
                case 0:
                    avatarList1.add(matchUsers.get(i));
                    break;
                case 1:
                    avatarList2.add(matchUsers.get(i));
                    break;
                case 2:
                    avatarList3.add(matchUsers.get(i));
                    break;
                case 3:
                    avatarList4.add(matchUsers.get(i));
                    break;
                case 4:
                    avatarList5.add(matchUsers.get(i));
                    break;
            }
        }
        if (avatarList1.size() > 0) {
            avatar1.setVisibility(View.VISIBLE);
            Glide.with(context).load(avatarList1.get(new Random().nextInt(avatarList1.size())).getHeadimg()).into(avatar1);
            Utils.playScaleAnim(avatar1, 1000, -1, 100, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Glide.with(context).load(avatarList1.get(new Random().nextInt(avatarList1.size())).getHeadimg()).into(avatar1);
                }
            });
        } else {
            avatar1.setVisibility(View.GONE);
        }

        if (avatarList2.size() > 0) {
            avatar2.setVisibility(View.VISIBLE);
            Glide.with(context).load(avatarList2.get(new Random().nextInt(avatarList2.size())).getHeadimg()).into(avatar2);
            Utils.playScaleAnim(avatar2, 1000, -1, 150, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Glide.with(context).load(avatarList2.get(new Random().nextInt(avatarList2.size())).getHeadimg()).into(avatar2);
                }
            });
        } else {
            avatar2.setVisibility(View.GONE);
        }

        if (avatarList3.size() > 0) {
            avatar3.setVisibility(View.VISIBLE);
            Glide.with(context).load(avatarList3.get(new Random().nextInt(avatarList3.size())).getHeadimg()).into(avatar3);
            Utils.playScaleAnim(avatar3, 1000, -1, 200, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Glide.with(context).load(avatarList3.get(new Random().nextInt(avatarList3.size())).getHeadimg()).into(avatar3);
                }
            });
        } else {
            avatar3.setVisibility(View.GONE);
        }

        if (avatarList4.size() > 0) {
            avatar4.setVisibility(View.VISIBLE);
            Glide.with(context).load(avatarList4.get(new Random().nextInt(avatarList4.size())).getHeadimg()).into(avatar4);
            Utils.playScaleAnim(avatar4, 1000, -1, 250, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Glide.with(context).load(avatarList4.get(new Random().nextInt(avatarList4.size())).getHeadimg()).into(avatar4);
                }
            });
        } else {
            avatar4.setVisibility(View.GONE);
        }

        if (avatarList5.size() > 0) {
            avatar5.setVisibility(View.VISIBLE);
            Glide.with(context).load(avatarList5.get(new Random().nextInt(avatarList5.size())).getHeadimg()).into(avatar5);
            Utils.playScaleAnim(avatar5, 1000, -1, 300, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Glide.with(context).load(avatarList5.get(new Random().nextInt(avatarList5.size())).getHeadimg()).into(avatar5);
                }
            });
        } else {
            avatar5.setVisibility(View.GONE);
        }
    }

    private boolean cancelMatch = false;

    @Override
    public void dismiss() {
        super.dismiss();
        if (listener != null) {
            listener.onDialogDismiss(cancelMatch);
        }
    }

    private OnBtnClickListener listener;

    public OnBtnClickListener getListener() {
        return listener;
    }

    public void setListener(OnBtnClickListener listener) {
        this.listener = listener;
    }

    public interface OnBtnClickListener {
        void onDialogDismiss(boolean cancelMatch);
    }
}
