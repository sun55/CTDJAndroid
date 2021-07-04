package com.ctdj.djandroid.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctdj.djandroid.R;

public class CommonItemView extends RelativeLayout {

    String title;
    int titleColor;
    String rightText;
    int rightTextColor;
    TextView tvTitle;
    TextView tvRight;
    View underLine;
    boolean showUnderLine;
    boolean showRightImg;

    public CommonItemView(Context context) {
        super(context);
    }

    public CommonItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.common_item_layout, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonItemView, 0, 0);
        title = typedArray.getString(R.styleable.CommonItemView_itemTitleText);
        rightText = typedArray.getString(R.styleable.CommonItemView_itemRightText);
        titleColor = typedArray.getColor(R.styleable.CommonItemView_itemTitleColor, Color.parseColor("#E8E8E9"));
        rightTextColor = typedArray.getColor(R.styleable.CommonItemView_itemRightTextColor, Color.parseColor("#80E8E8E9"));
        showUnderLine = typedArray.getBoolean(R.styleable.CommonItemView_showUnderLine, true);
        showRightImg = typedArray.getBoolean(R.styleable.CommonItemView_showRightArrow, true);
        tvTitle = findViewById(R.id.tv_title);
        tvRight = findViewById(R.id.tv_right);
        underLine = findViewById(R.id.under_line);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);
        tvRight.setText(rightText);
        tvRight.setTextColor(rightTextColor);
        underLine.setVisibility(showUnderLine ? VISIBLE : GONE);
        tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, showRightImg ? R.drawable.right_more_icon : 0, 0);
    }

    public void setTitle(String title) {
        this.title = title;
        tvTitle.setText(title);
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        tvTitle.setTextColor(titleColor);
    }

    public void setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
        tvRight.setTextColor(rightTextColor);
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
        tvRight.setText(rightText);
    }

    public String getRightText() {
        return rightText;
    }

    public void setShowUnderLine(boolean showUnderLine) {
        this.showUnderLine = showUnderLine;
        underLine.setVisibility(showUnderLine ? VISIBLE : GONE);
    }
}