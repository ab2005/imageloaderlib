package com.seagate.alto;

import android.app.Application;

import com.seagate.imageadapter.Drawees;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Drawees.initializeWithDefaults(this);
    }
}

