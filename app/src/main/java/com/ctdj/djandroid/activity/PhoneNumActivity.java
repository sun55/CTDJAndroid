package com.ctdj.djandroid.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityPhoneNumBinding;
import com.ctdj.djandroid.view.TitleView;

public class PhoneNumActivity extends BaseActivity {

    ActivityPhoneNumBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) binding.titleView.getLayoutParams();
        l.topMargin = DisplayUtil.getStatusBarHeight(this);
        binding.titleView.setOnBtnListener(new TitleView.OnBtnListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });

        binding.titleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.showSoftKeyboard(PhoneNumActivity.this, binding.etPhoneNum);
            }
        }, 100);
        binding.etPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                        continue;
                    } else {
                        sb.append(s.charAt(i));
                        if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }
                if (!sb.toString().equals(s.toString())) {
                    int index = start + 1;
                    if (sb.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    binding.etPhoneNum.setText(sb.toString());
                    binding.etPhoneNum.setSelection(index);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtil.e("-------" + s.toString().trim().length());
                binding.btnSendCode.setEnabled(s.toString().trim().length() == 13);
            }
        });
    }

    public void sendCode(View view) {
        String phoneNum = binding.etPhoneNum.getText().toString();
        Intent intent = new Intent(PhoneNumActivity.this, SmsCodeActivity.class);
        intent.putExtra("phone_num", phoneNum);
        startActivity(intent);

    }
}