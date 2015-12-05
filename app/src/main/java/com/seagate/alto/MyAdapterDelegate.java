package com.seagate.alto;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.seagate.imageadapter.adapters.Adapter;
import com.seagate.imageadapter.instrumentation.PerfListener;

/**
 *
 */
public class MyAdapterDelegate implements Adapter.Delegate {
    private final int[] LAYOUTS = {
            R.layout.instr_item_tile,
            R.layout.instr_item_card,
            R.layout.instr_item_list
    };
    private final int[] FRESCO_LAYOUTS = {
            R.layout.instr_item_tile_fresco,
            R.layout.instr_item_card_fresco,
            R.layout.instr_item_list_fresco
    };
    private final int[] VOLLEY_LAYOUTS = {
            R.layout.instr_item_tile_volley,
            R.layout.instr_item_card_volley,
            R.layout.instr_item_list_volley
    };

    private PerfListener profiler;

    private final AppCompatActivity activity;
    private int layoutId;

    public MyAdapterDelegate(AppCompatActivity activity) {
        this.activity = activity;
        profiler = new PerfListener();
    }

    public void setLayout(int pageIndex, int loaderId) {
        boolean useDrawee = (loaderId == R.id.action_image_loader_fresco || loaderId == R.id.action_image_loader_fresco_okhttp);
        if (loaderId == R.id.action_image_loader_volley) {
            layoutId = VOLLEY_LAYOUTS[pageIndex];
        } else {
            layoutId = useDrawee ? FRESCO_LAYOUTS[pageIndex] : LAYOUTS[pageIndex];
        }
    }

    public void resetProfiler() {
        profiler = new PerfListener();
    }

    @Override
    public Context getContext() {
        return activity;
    }

    @Override
    public PerfListener getPerformanceListener() {
        return profiler;
    }

    @Override
    public View getHolderView(ViewGroup parent, int viewType) {
        final View view = activity.getLayoutInflater().inflate(layoutId, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DetailActivity.class);
                String url = view.getTag(getImageViewId()) + "";
                intent.putExtra("EXTRA_KEY_URL", url);
                context.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public int getImageViewId() {
        return R.id.instr_image;
    }

    @Override
    public void bind(View itemView, String url) {
        itemView.setTag(getImageViewId(), url);
    }
}
