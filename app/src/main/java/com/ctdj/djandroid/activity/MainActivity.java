package com.ctdj.djandroid.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.databinding.ActivityMainBinding;
import com.ctdj.djandroid.fragment.MessageFragment;
import com.ctdj.djandroid.fragment.MineFragment;
import com.ctdj.djandroid.fragment.PlayFragment;

public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;

    private FragmentManager fragmentManager;
    private MessageFragment messageFragment;
    private PlayFragment playFragment;
    private MineFragment mineFragment;
    private static final String MESSAGE_TAG = "message-page";
    private static final String PLAY_TAG = "play-page";
    private static final String MINE_TAG = "mine-tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

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
            playFragment = new PlayFragment();
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
}