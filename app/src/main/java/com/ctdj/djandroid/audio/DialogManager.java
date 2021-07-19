package com.ctdj.djandroid.audio;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctdj.djandroid.R;

/**
 * Created by lt on 2016/8/6.
 */
public class DialogManager {

    private Dialog mDialog;

    private ImageView mIcon;
    //    private ImageView mVoice;
    private TextView mLabel;

    private Context mContext;

    public DialogManager(Context context) {
        mContext = context;
    }

    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_main, null);
        mDialog.setContentView(view);

        mIcon = (ImageView) mDialog.findViewById(R.id.id_recorder_dialog_icon);
        mLabel = (TextView) mDialog.findViewById(R.id.id_recorder_dialog_label);
        mLabel.setBackgroundResource(0);
        mDialog.show();
    }

    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
//            mVoice.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);

//            mIcon.setImageResource(R.drawable.dialog_record_icon);
            mLabel.setText("手指上滑，取消发送");
            mLabel.setBackgroundColor(0);
        }
    }

    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
//            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);

//            mIcon.setImageResource(R.drawable.dialog_record_cancel_icon);
            mLabel.setText("松开手指，取消发送");
            mLabel.setBackgroundColor(Color.parseColor("#FD4D6A"));
        }

    }

    public void toShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
//            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);
//            mIcon.setImageResource(R.drawable.voice_to_short);
            mLabel.setText("录音时间过短");
        }

    }

    public void dimissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 通过level更新声音级别图片
     *
     * @param level
     */
    public void updateVoiceLevel(int level) {
        int resId = mContext.getResources().getIdentifier("v" + level, "drawable", mContext.getPackageName());
//        mVoice.setImageResource(resId);
    }
}
