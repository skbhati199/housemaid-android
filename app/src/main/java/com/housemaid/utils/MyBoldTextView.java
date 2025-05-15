package com.housemaid.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by fluper on 1/5/18.
 */

@SuppressLint("AppCompatCustomView")
public class MyBoldTextView extends TextView {
    public MyBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBoldTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "font/sf_san_bold.otf");
            setTypeface(tf);
        }
    }
}