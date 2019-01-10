package com.rolnik.alcoholapp.utils;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor

public class CustomItemDecorator extends RecyclerView.ItemDecoration {

    private int bottomOffset = 10;
    private int topOffset = 10;


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state){
        outRect.bottom = bottomOffset;
        outRect.top = topOffset;
    }
}
