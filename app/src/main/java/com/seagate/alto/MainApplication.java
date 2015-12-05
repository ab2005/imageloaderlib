package com.seagate.alto;

import android.app.Application;

import com.seagate.imageadapter.adapters.Adapter;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize adapter library
        Adapter.initializeWithDefaults(this);
    }
}

