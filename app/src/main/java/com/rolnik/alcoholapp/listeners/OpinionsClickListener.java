package com.rolnik.alcoholapp.listeners;

import android.view.View;

public interface OpinionsClickListener {
    void onLike(View view, int position);
    void onDislike(View view, int position);
}
