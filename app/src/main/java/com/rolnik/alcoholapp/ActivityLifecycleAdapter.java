package com.rolnik.alcoholapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public abstract class ActivityLifecycleAdapter implements Application.ActivityLifecycleCallbacks {
    @Override
    public abstract void onActivityCreated(Activity activity, Bundle bundle);

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
