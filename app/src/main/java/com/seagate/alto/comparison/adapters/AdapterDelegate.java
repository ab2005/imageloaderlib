package com.seagate.alto.comparison.adapters;

import android.view.ViewGroup;

import com.seagate.alto.R;

/**
 *
 */
public interface AdapterDelegate {
    int KEY_DATA = R.string.adapter_delegate_key_url;
    String EXTRA_KEY_DATA = AdapterDelegate.class.getName() + ".EXTRA_KEY_DATA";

    ViewGroup getHolderView(ViewGroup parent, int viewType);
}
