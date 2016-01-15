package com.seagate.alto;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seagate.alto.ui.DigestListView;
import com.seagate.alto.utils.ScreenUtils;

public class DigestFragment extends Fragment implements IBackStackName {

    private static final String TAG = DigestFragment.class.getName();

    private DigestListView mDigestListView;

    public DigestFragment() {
        super();
        Log.d(TAG, "constructor");
    }

    @Override
    public String getBackStackName() {
        return null;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.digest_view_shelf, container, false);
        mDigestListView = (DigestListView) view.findViewById(R.id.digest_shelf);
        if (!ScreenUtils.isPortrait()) {
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mDigestListView.setLayoutManager(layoutManager);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
