/*
 * Copyright (C) 2015 Seagate LLC
 */

package com.seagate.alto.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seagate.alto.DetailActivity;
import com.seagate.alto.MainActivity;
import com.seagate.alto.R;

/**
 * Provides UI for the view with List.
 */
public class ListContentFragment extends Fragment {

    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ((MainActivity)getActivity()).setAdapter();

        if (recyclerView.getAdapter() == null) {
            ContentAdapter adapter = new ContentAdapter();
            recyclerView.setAdapter(adapter);
        }
        return recyclerView;
    }

    /**
     * Adapter to display recycler view.
     */
    static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.
        private static final int LENGTH = 18;
        public ContentAdapter() {
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // no-op
        }
        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }

}
