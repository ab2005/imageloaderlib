package com.seagate.alto.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seagate.alto.DetailActivity;
import com.seagate.alto.MainActivity;
import com.seagate.alto.R;

/**
 * Provides UI for the view with Tile.
 */
public class TileContentFragment extends Fragment {
    private static final String TAG = TileContentFragment.class.getName();
    int layoutId = R.layout.instr_item_tile;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "OnCreateView");
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "OnViewCreated");
        // Set padding for Tiles
        int tilePadding = getResources().getDimensionPixelSize(R.dimen.tile_padding);
        recyclerView.setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        ((MainActivity)getActivity()).setAdapter();

        if (recyclerView.getAdapter() == null) {
            Log.d(TAG, "OnViewCreated: recyclerView.getAdapter() == null");
            RecyclerView.Adapter adapter = ((MainActivity)getActivity()).getCurrentAdaper();
            if (adapter == null) {
                adapter = new ContentAdapter();
            }
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        ((MainActivity)getActivity()).setAdapter();
        super.onResume();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_tile, parent, false));
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

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of Tiles in RecyclerView.
        private static final int LENGTH = 18;

        public ContentAdapter() {
            // no-op
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
}
