package com.ctdj.djandroid.audio;

import android.Manifest;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;

/**
 * Created by lt on 2016/8/6.
 */
public class AudioRecorderButton extends androidx.appcompat.widget.AppCompatTextView implements AudioManager.AudioStateListener {

    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;

    private int mCurSate = STATE_NORMAL;
    //已经开始录音
    private boolean isRecording = false;
    private static final int DISANCE_Y_CANCEL = 50;

//    private DialogManager mDialogManager;

    private AudioManager mAudioManager;

    //记时
    private float mTime;
    //是否触发长按事件
    private boolean mReady;

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);

//        mDialogManager = new DialogManager(getContext());

        mAudioManager = AudioManager.getInstance();
        mAudioManager.setOnAduioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Utils.checkPermissions(getContext(), new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                    mReady = true;
                    mAudioManager.prepareAudio();
                    Utils.vibrator();
                } else {
                    if (audioRecordStateListener != null) {
                        audioRecordStateListener.onNoPermission();
                    }
                    Utils.showToast(getContext(), "请开启录音和读写权限");
                }
                //触发点击事件
                return false;
            }
        });
    }

    public interface AudioRecordStateListener {
        void onFinish(float seconds, String filePath); // 录音完成

        void onNormal();

        void onShort(); // 时间太短

        void onRecording(); // 录音中

        void onCountTime(int second); // 计时

        void onWantCancel(); // 松手取消发送

        void onNoPermission(); // 没有权限
    }

    private AudioRecordStateListener audioRecordStateListener;

    public void setAudioRecordStateListener(AudioRecordStateListener audioRecordStateListener) {
        this.audioRecordStateListener = audioRecordStateListener;
    }

    /**
     * 音量大小Runnable
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                mTime += 0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                    if (audioRecordStateListener != null) {
                        audioRecordStateListener.onCountTime((int) mTime);
                    }
                    postDelayed(mGetVoiceLevelRunnable, 100);
            }
//            while (isRecording) {
//                try {
//                    Thread.sleep(100);
//                    mTime += 0.1f;
//                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
//                    if (audioRecordStateListener != null && isRecording) {
//                        audioRecordStateListener.onCountTime((int) mTime);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    };

    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGED = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    //显示在aduio end process后
//                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    postDelayed(mGetVoiceLevelRunnable, 100);
//                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
//                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
//                    mDialogManager.dimissDialog();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_UP:
                //未触发onlongclick,此时默认为不处理
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }

                if (!isRecording || mTime < 0.6f) {
//                    mDialogManager.toShort();
                    mAudioManager.cancel();
                    if (audioRecordStateListener != null) {
                        audioRecordStateListener.onShort();
                    }
                    //延时消失
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                } else if (mCurSate == STATE_RECORDING) {//正常录制结束
//                    mDialogManager.dimissDialog();
                    mAudioManager.release();

                    //监听录音完成接口
                    if (audioRecordStateListener != null) {
                        audioRecordStateListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    }

                    //release
                    //callbackToAct
                } else if (mCurSate == STATE_WANT_TO_CANCEL) {
//                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                    //cancel
                }

                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                //已经开始录音
                if (isRecording) {
                    //根据x,y坐标，判断是否想要取消
                    if (wantToCanel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 恢复状态及标志位
     */
    private void reset() {
        LogUtil.e("reset-----------------------------------");
        isRecording = false;
        mTime = 0;
        mReady = false;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCanel(int x, int y) {
        //横坐标在按钮外，到达按钮右侧
        if (x < 0 || x > getWidth()) {
            return true;
        }

        //手指横坐标在内部，y轴超过指定范围
        if (y < -DISANCE_Y_CANCEL || y > getWidth() + DISANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    private void changeState(int stateRecording) {
        if (mCurSate != stateRecording) {
            mCurSate = stateRecording;
            switch (stateRecording) {
                case STATE_NORMAL:
//                    setBackgroundResource(R.drawable.btn_recorder);
//                    setText(R.string.str_recorder_noraml);
                    if (audioRecordStateListener != null) {
                        audioRecordStateListener.onNormal();
                        removeCallbacks(mGetVoiceLevelRunnable);
                    }
                    break;
                case STATE_RECORDING:
//                    setBackgroundResource(R.drawable.btn_recording);
//                    setText(R.string.str_recorder_recording);
                    if (isRecording) {
//                        mDialogManager.recording();
                        if (audioRecordStateListener != null) {
                            audioRecordStateListener.onRecording();
                        }
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    if (audioRecordStateListener != null) {
                        audioRecordStateListener.onWantCancel();
                    }
//                    setBackgroundResource(R.drawable.btn_recording);
//                    setText(R.string.str_recorder_want_cancel);
//                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }


}
