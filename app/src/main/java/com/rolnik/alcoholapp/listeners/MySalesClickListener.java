package com.rolnik.alcoholapp.listeners;

import android.view.View;

public interface MySalesClickListener {
    void onClick(View view, int position);
    void onDelete(int position);
}
