package com.ctdj.djandroid.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.ctdj.djandroid.R;

public class DrawableCenterTextView extends AppCompatTextView {
    private int drawableLeftWidth;
    private int drawableTopWidth;
    private int drawableRightWidth;
    private int drawableBottomWidth;
    private int drawableLeftHeight;
    private int drawableTopHeight;
    private int drawableRightHeight;
    private int drawableBottomHeight;
    private boolean isAliganCenter;
    private boolean isDwMath_content;
    private int mWidth;
    private int mHeight;

    public DrawableCenterTextView(Context context) {
        this(context, (AttributeSet)null);
    }

    public DrawableCenterTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableCenterTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isAliganCenter = true;
        this.isDwMath_content = false;
        this.initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawableCenterTextView);
        this.drawableLeftWidth = typedArray.getDimensionPixelSize(R.styleable.DrawableCenterTextView_drawableLeftWidth, 0);
        this.drawableTopWidth = typedArray.getDimensionPixelSize(R.styleable.DrawableCenterTextView_drawableTopWidth, 0);
        this.drawableRightWidth = typedArray.getDimensionPixelSize(R.styleable.DrawableCenterTextView_drawableRightWidth, 0);
        this.drawableBottomWidth = typedArray.getDimensionPixelSize(R.styleable.DrawableCenterTextView_drawableBottomWidth, 0);
        this.drawableLeftHeight = typedArray.getDimensionPixelSize(R.styleable.DrawableCenterTextView_drawableLeftHeight, 0);
        this.drawableTopHeight = typedArray.getDimensionPixelSize(R.styleable.DrawableCenterTextView_drawableTopHeight, 0);
        this.drawableRightHeight = typedArray.getDimensionPixelSize(R.styleable.DrawableCenterTextView_drawableRightHeight, 0);
        this.drawableBottomHeight = typedArray.getDimensionPixelSize(R.styleable.DrawableCenterTextView_drawableBottomHeight, 0);
        this.isAliganCenter = typedArray.getBoolean(R.styleable.DrawableCenterTextView_isAliganCenter, true);
        typedArray.recycle();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        Drawable[] drawables = this.getCompoundDrawables();
        Drawable drawableLeft = drawables[0];
        Drawable drawableTop = drawables[1];
        Drawable drawableRight = drawables[2];
        Drawable drawableBottom = drawables[3];
        if (drawableLeft != null) {
            this.setDrawable(drawableLeft, 0, this.drawableLeftWidth, this.drawableLeftHeight);
        }

        if (drawableTop != null) {
            this.setDrawable(drawableTop, 1, this.drawableTopWidth, this.drawableTopHeight);
        }

        if (drawableRight != null) {
            this.setDrawable(drawableRight, 2, this.drawableRightWidth, this.drawableRightHeight);
        }

        if (drawableBottom != null) {
            this.setDrawable(drawableBottom, 3, this.drawableBottomWidth, this.drawableBottomHeight);
        }

        this.setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    private void setDrawable(Drawable drawable, int tag, int drawableWidth, int drawableHeight) {
        int width = drawableWidth == 0 ? drawable.getIntrinsicWidth() : drawableWidth;
        int height = drawableHeight == 0 ? drawable.getIntrinsicHeight() : drawableHeight;
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        switch(tag) {
            case 0:
            case 2:
                left = 0;
                top = this.isAliganCenter ? 0 : -this.getLineCount() * this.getLineHeight() / 2 + this.getLineHeight() / 2;
                right = width;
                bottom = top + height;
                break;
            case 1:
                left = this.isAliganCenter ? 0 : -this.mWidth / 2 + width / 2;
                top = 0;
                right = left + width;
                bottom = top + height;
        }

        drawable.setBounds(left, top, right, bottom);
    }
}

