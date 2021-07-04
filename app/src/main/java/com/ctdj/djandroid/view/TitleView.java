package com.ctdj.djandroid.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.common.DisplayUtil;

/**
 * @ProjectName : 咪兔
 * @Author : Sun
 * @Time : 2021/4/9 14:43
 * @Description : 标题栏
 */
public class TitleView extends RelativeLayout {

    ImageView ivBack;
    ImageView ivRight;
    TextView tvTitle;
    TextView tvRight;
    View rightView;
    String title;
    String rightText;
    int rightSize;
    int titleColor;
    int rightTextColor;
    int leftSrc;
    int rightSrc;
    boolean showRightImg;

    public TitleView(Context context) {
        super(context);
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title_layout, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView, 0, 0);
        ivBack = findViewById(R.id.iv_back);
        ivRight = findViewById(R.id.iv_right);
        tvRight = findViewById(R.id.tv_right);
        tvTitle = findViewById(R.id.tv_title);
        rightView = findViewById(R.id.right_view);
        title = typedArray.getString(R.styleable.TitleView_titleText);
        rightText = typedArray.getString(R.styleable.TitleView_rightText);
        rightSize = typedArray.getDimensionPixelSize(R.styleable.TitleView_rightTextSize, 13);
        titleColor = typedArray.getColor(R.styleable.TitleView_titleColor, Color.parseColor("#E8E8E9"));
        rightTextColor = typedArray.getColor(R.styleable.TitleView_rightTextColor, Color.parseColor("#C9B1F5"));
        leftSrc = typedArray.getResourceId(R.styleable.TitleView_leftImgSrc, R.drawable.white_back_icon);
        rightSrc = typedArray.getResourceId(R.styleable.TitleView_rightImgSrc, R.drawable.three_dot_icon);
        showRightImg = typedArray.getBoolean(R.styleable.TitleView_showRightImg, false);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);
        ivRight.setVisibility(showRightImg ? VISIBLE : GONE);
        ivRight.setImageResource(rightSrc);
        ivBack.setImageResource(leftSrc);
        tvRight.setTextColor(rightTextColor);
        tvRight.setText(rightText);
        tvRight.setTextSize(DisplayUtil.px2sp(context, rightSize));

        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBtnListener != null) {
                    onBtnListener.onLeftClick();
                }
            }
        });

        rightView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBtnListener != null) {
                    onBtnListener.onRightClick();
                }
            }
        });
    }

    public void setTitle(String title) {
        this.title = title;
        tvTitle.setText(title);
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        tvTitle.setTextColor(titleColor);
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
        tvRight.setText(rightText);
    }

    public void setRightSize(int rightSize) {
        this.rightSize = rightSize;
        tvRight.setTextSize(rightSize);
    }

    public void setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
        tvRight.setTextColor(rightTextColor);
    }

    public void setLeftSrc(int leftSrc) {
        this.leftSrc = leftSrc;
        ivBack.setImageResource(leftSrc);
    }

    public void setRightSrc(int rightSrc) {
        this.rightSrc = rightSrc;
        ivRight.setImageResource(rightSrc);
    }

    public void setShowRightImg(boolean showRightImg) {
        this.showRightImg = showRightImg;
        ivRight.setVisibility(showRightImg ? VISIBLE : GONE);
    }

    private OnBtnListener onBtnListener;

    public void setOnBtnListener(OnBtnListener onBtnListener) {
        this.onBtnListener = onBtnListener;
    }

    public interface OnBtnListener {
        void onLeftClick();

        void onRightClick();
    }
}
