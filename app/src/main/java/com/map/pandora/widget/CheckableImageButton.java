package com.map.pandora.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageButton;

import com.map.pandora.R;

/**
 * @author jayce
 * @date 2015/4/16
 */
public class CheckableImageButton extends ImageButton implements Checkable{

    private boolean mChecked;
    private static final int[] CHECKED_STATE_SET = {R.attr.checked };

    public CheckableImageButton(Context context) {
        this(context, null);
    }

    public CheckableImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取自定义属性的值
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.checkableImageButton);
        mChecked = a.getBoolean(R.styleable.checkableImageButton_checked, false);
        setClickable(true);
        setChecked(mChecked);
        // Give back a previously retrieved StyledAttributes, for later re-use.
        a.recycle();
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            // 状态改变刷新视图
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] states = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(states, CHECKED_STATE_SET);
        }
        return states;
    }
}
