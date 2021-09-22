package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.NoticeCustomContent;
import com.ctdj.djandroid.bean.RegisterBean;
import com.ctdj.djandroid.bean.SendMatchBean;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityMainBinding;
import com.ctdj.djandroid.dialog.OpenNotifyDialog;
import com.ctdj.djandroid.dialog.RadarScanDialog;
import com.ctdj.djandroid.dialog.ReceiveInviteDialog;
import com.ctdj.djandroid.event.CountDownEvent;
import com.ctdj.djandroid.event.MatchTimeEndEvent;
import com.ctdj.djandroid.event.StopRadarEvent;
import com.ctdj.djandroid.event.UpdateMatchUserEvent;
import com.ctdj.djandroid.fragment.MessageFragment;
import com.ctdj.djandroid.fragment.MineFragment;
import com.ctdj.djandroid.fragment.HomeFragment;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.google.gson.Gson;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.tencent.imsdk.v2.V2TIMManager.V2TIM_STATUS_LOGINED;

public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;

    private FragmentManager fragmentManager;
    private MessageFragment messageFragment;
    private HomeFragment playFragment;
    private MineFragment mineFragment;
    private static final String MESSAGE_TAG = "message-page";
    private static final String PLAY_TAG = "play-page";
    private static final String MINE_TAG = "mine-tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        EventBus.getDefault().register(this);
        HttpClient.intoPersonal(this, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                RegisterBean bean = new Gson().fromJson(result, RegisterBean.class);
                MyApplication.getInstance().saveUserInfo(bean.data);
                Glide.with(MainActivity.this).load(bean.data.headimg).into(binding.playAvatar);
                TUIKit.login(MyApplication.getInstance().getMid(), MyApplication.getInstance().getUserInfo().userSig, new IUIKitCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        LogUtil.i("im login success");
                        V2TIMUserFullInfo imUserInfo = new V2TIMUserFullInfo();
                        imUserInfo.setFaceUrl(bean.data.headimg);
                        imUserInfo.setNickname(bean.data.mname);
                        imUserInfo.setGender(bean.data.sex);
                        V2TIMManager.getInstance().setSelfInfo(imUserInfo, new V2TIMCallback() {
                            @Override
                            public void onSuccess() {
                                LogUtil.d("设置im 用户信息 成功");
                            }

                            @Override
                            public void onError(int code, String desc) {
                                LogUtil.d("设置im 用户信息 失败 code:" + code + ", desc : " + desc);
                            }
                        });
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        LogUtil.e("im login error: module:" + module + ", errCode:" + errCode + ", errMsg:" + errMsg);
                    }
                });
            }

            @Override
            public void onFailure(String msg) {

            }
        });

        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment messageFragment = fragmentManager.findFragmentByTag(MESSAGE_TAG);
            if (messageFragment != null) {
                transaction.remove(messageFragment);
            }
            Fragment playFragment = fragmentManager.findFragmentByTag(PLAY_TAG);
            if (playFragment != null) {
                transaction.remove(playFragment);
            }
            Fragment mineFragment = fragmentManager.findFragmentByTag(MINE_TAG);
            if (mineFragment != null) {
                transaction.remove(mineFragment);
            }
            transaction.commitAllowingStateLoss();
        }
        onPlayTabClicked(null);

        V2TIMUserFullInfo imUserInfo = new V2TIMUserFullInfo();
        imUserInfo.setFaceUrl(MyApplication.getInstance().getUserInfo().headimg);
        imUserInfo.setNickname(MyApplication.getInstance().getUserInfo().mname);
        imUserInfo.setGender(MyApplication.getInstance().getUserInfo().sex);
        V2TIMManager.getInstance().setSelfInfo(imUserInfo, new V2TIMCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int code, String desc) {

            }
        });

        registerPush();
        Utils.parserSvgaAnim(this, "home_chat_room.svga", new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity svgaVideoEntity) {
                SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                binding.playAnim.setImageDrawable(drawable);
                binding.playAnim.startAnimation();
            }

            @Override
            public void onError() {
                LogUtil.e("动画资源解析失败");
            }
        });
        binding.rlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRadarDialog(challengeType);
            }
        });
        if (!Utils.checkNotifyPermission(this)) {
            OpenNotifyDialog dialog = new OpenNotifyDialog(this);
            dialog.show();
        }
    }

    private void registerPush() {
        XGPushManager.registerPush(MyApplication.getInstance(), new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                LogUtil.i("注册推送成功 token：" + o);
                List<XGPushManager.AccountInfo> accountInfoList = new ArrayList<>();
                accountInfoList.add(new XGPushManager.AccountInfo(XGPushManager.AccountType.UNKNOWN.getValue(), MyApplication.getInstance().getMid()));
                XGPushManager.upsertAccounts(MainActivity.this, accountInfoList, new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object o, int i) {
                        LogUtil.d("推送绑定账号成功");
                    }

                    @Override
                    public void onFail(Object o, int i, String s) {
                        LogUtil.e("推送绑定失败 o: " + o + ", i:" + i + ", s:" + s);
                    }
                });
            }

            @Override
            public void onFail(Object o, int i, String s) {
                LogUtil.e("注册推送失败 i:" + i + ", s:" + s);
            }
        });
    }

    public void onMessageTabClicked(View view) {
        resetTabStatus();
        binding.tvMessageTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_message_selected, 0, 0);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        if (messageFragment == null) {
            messageFragment = new MessageFragment();
            transaction.add(R.id.content_layout, messageFragment, MESSAGE_TAG);
        } else {
            transaction.show(messageFragment);
        }
        transaction.commitAllowingStateLoss();
    }

    public void onPlayTabClicked(View view) {
        resetTabStatus();
        binding.tvPlayTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_play_selected, 0, 0);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        if (playFragment == null) {
            playFragment = new HomeFragment();
            transaction.add(R.id.content_layout, playFragment, PLAY_TAG);
        } else {
            transaction.show(playFragment);
        }
        transaction.commitAllowingStateLoss();
    }

    public void onMineTabClicked(View view) {
        resetTabStatus();
        binding.tvMineTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_mine_selected, 0, 0);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        if (mineFragment == null) {
            mineFragment = new MineFragment();
            transaction.add(R.id.content_layout, mineFragment, MINE_TAG);
        } else {
            transaction.show(mineFragment);
        }
        transaction.commitAllowingStateLoss();
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if (messageFragment != null) {
            transaction.hide(messageFragment);
        }
        if (playFragment != null) {
            transaction.hide(playFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }

    private void resetTabStatus() {
        binding.tvMessageTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_message_normal, 0, 0);
        binding.tvPlayTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_play_normal, 0, 0);
        binding.tvMineTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_mine_normal, 0, 0);
    }

    public void showRadarDialog(int challengeType) {
        if (timer != null && this.challengeType != challengeType) {
            Utils.showToast(this, "当前存在匹配，请稍后再试");
            return;
        }
        if (binding.rlPlay.getVisibility() == View.VISIBLE) {
            binding.rlPlay.setVisibility(View.GONE);
        }
        this.challengeType = challengeType;
        RadarScanDialog dialog = new RadarScanDialog(this, challengeType);
        dialog.show();
        dialog.setListener(new RadarScanDialog.OnBtnClickListener() {
            @Override
            public void onDialogDismiss(boolean cancelMatch) {
                if (timer != null && !cancelMatch) {
                    binding.rlPlay.setVisibility(View.VISIBLE);
                } else {
                    stopCountDown();
                }
            }
        });
        if (timer == null) {
            countDown();
        }
    }

    CountDownTimer timer;
    int challengeType = 1;

    /**
     * 倒计时
     */
    private void countDown() {
        timer = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                EventBus.getDefault().post(new CountDownEvent((int) (millisUntilFinished / 1000)));
                if (millisUntilFinished / 1000 % 30 == 0) { // 30秒请求一次
                    sendMatchInvite();
                }
            }

            @Override
            public void onFinish() {
                LogUtil.e("on finish");
                stopCountDown();
                EventBus.getDefault().post(new MatchTimeEndEvent());
            }
        };
        timer.start();
        sendMatchInvite();
    }

    private void sendMatchInvite() {
        HttpClient.sendMatchInvite(this, challengeType, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                SendMatchBean bean = new Gson().fromJson(result, SendMatchBean.class);
                matchUsers.clear();
                matchUsers.addAll(bean.getData());
                EventBus.getDefault().post(new UpdateMatchUserEvent());
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(MainActivity.this, msg);
            }
        });
    }

    public void stopCountDown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (binding.rlPlay.getVisibility() == View.VISIBLE) {
            binding.rlPlay.setVisibility(View.GONE);
        }
    }

    public ArrayList<SendMatchBean.Data> matchUsers = new ArrayList<>();


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(Object event) {
        if (event instanceof NoticeCustomContent) {
            ReceiveInviteDialog dialog = new ReceiveInviteDialog(this,
                    ((NoticeCustomContent) event).getChallengeType(),
                    ((NoticeCustomContent) event).getSender(),
                    ((NoticeCustomContent) event).getMname(),
                    ((NoticeCustomContent) event).getHeadimg());
            dialog.show();
        } else if (event instanceof StopRadarEvent) {
            stopCountDown();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}