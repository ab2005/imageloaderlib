/*
 * Copyright (C) 2015 Seagate LLC
 */

package com.seagate.alto;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Provides UI for the Detail page with Collapsing Toolbar.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // Set title of Detail page
        collapsingToolbar.setTitle(getString(R.string.item_title));

        collapsingToolbar.setOnClickListener(new View.OnClickListener() {
            android.view.ActionMode actionMode;
            @Override
            public void onClick(View v) {
                if (actionMode == null) {
                    actionMode = startActionMode(new android.view.ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                            return false;
                        }

                        @Override
                        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                            return false;
                        }

                        @Override
                        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                            return false;
                        }

                        @Override
                        public void onDestroyActionMode(android.view.ActionMode mode) {

                        }
                    });
                    if (actionMode != null) {
                        actionMode.setTitle("Action Mode");
                    }
                } else {
                    actionMode.finish();
                    actionMode  = null;
                }
            }
        });
    }
}
