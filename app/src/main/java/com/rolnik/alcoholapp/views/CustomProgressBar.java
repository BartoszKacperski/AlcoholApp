package com.rolnik.alcoholapp.views;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.rolnik.alcoholapp.R;

import lombok.Getter;
import lombok.Setter;

public class CustomProgressBar extends FrameLayout {
    private static final int MAX_LVL = 10000;
    private static final int DELAY = 25;

    @Getter
    @Setter
    private int difference = 25;

    private Context mContext;
    private ClipDrawable clipDrawable;
    private Handler progressHandler;
    private Runnable animateUp;
    private int currentLevel = 0;


    public CustomProgressBar(@NonNull Context context) {
        super(context);
        this.mContext = context;
        initLayout();
        initAnimation();
    }

    public CustomProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
        initAnimation();
    }

    public CustomProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
        initAnimation();
    }

    private void initLayout(){
        View v = LayoutInflater.from(mContext).inflate(R.layout.custom_progress_bar, this, true);

        ImageView emptyImage = v.findViewById(R.id.emptyImage);
        ImageView fullImage = v.findViewById(R.id.fullImage);

        Drawable empty = mContext.getDrawable(R.drawable.empty_beer);
        Drawable full = mContext.getDrawable(R.drawable.full_beer);

        emptyImage.setImageDrawable(empty);

        clipDrawable = new ClipDrawable(full, Gravity.BOTTOM, ClipDrawable.VERTICAL);
        fullImage.setImageDrawable(clipDrawable);
    }

    private void initAnimation(){
        progressHandler = new Handler();

        animateUp = new Runnable() {
            @Override
            public void run() {
                doUpAnimation();
            }
        };
    }

    private void doUpAnimation(){
        currentLevel += difference;
        if(currentLevel > MAX_LVL){
            currentLevel = 0;
            clipDrawable.setLevel(currentLevel);
        } else {
            clipDrawable.setLevel(currentLevel);
        }

        progressHandler.postDelayed(animateUp, DELAY);
    }

    public void startAnimation(){
        progressHandler.postDelayed(animateUp, DELAY);
    }

    public void stopAnimation(){
        progressHandler.removeCallbacks(animateUp);
    }

    public void endAnimation(){
        progressHandler.removeCallbacks(animateUp);
        clipDrawable.setLevel(MAX_LVL);
    }
}


