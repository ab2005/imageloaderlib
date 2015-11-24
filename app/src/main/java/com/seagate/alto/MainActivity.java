/*
 * Copyright (C) 2015 Seagate LLC
 */

package com.seagate.alto;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.seagate.imageadapter.Drawables;
import com.seagate.imageadapter.adapters.AQueryAdapter;
import com.seagate.imageadapter.adapters.AdapterDelegate;
import com.seagate.imageadapter.adapters.FrescoAdapter;
import com.seagate.imageadapter.adapters.GlideAdapter;
import com.seagate.imageadapter.adapters.ImageListAdapter;
import com.seagate.imageadapter.adapters.PicassoAdapter;
import com.seagate.imageadapter.adapters.UilAdapter;
import com.seagate.imageadapter.adapters.VolleyAdapter;
import com.seagate.imageadapter.adapters.VolleyDraweeAdapter;
import com.seagate.imageadapter.configs.imagepipeline.ImagePipelineConfigFactory;
import com.seagate.imageadapter.instrumentation.PerfListener;
import com.seagate.imageadapter.urlsfetcher.ImageFormat;
import com.seagate.imageadapter.urlsfetcher.ImageSize;
import com.seagate.imageadapter.urlsfetcher.ImageUrlsFetcher;
import com.seagate.imageadapter.urlsfetcher.ImageUrlsRequestBuilder;
import com.seagate.alto.pages.CardContentFragment;
import com.seagate.alto.pages.ListContentFragment;
import com.seagate.alto.pages.SettingsActivity;
import com.seagate.alto.pages.TileContentFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Provides UI for the main screen.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private static final int DEFAULT_MESSAGE_SIZE = 1024;
    private static final int BYTES_IN_MEGABYTE = 1024 * 1024;

    private static final long STATS_CLOCK_INTERVAL_MS = 1000;
    private static final String EXTRA_ALLOW_ANIMATIONS = "allow_animations";
    private static final String EXTRA_USE_DRAWEE = "use_drawee";
    private static final String EXTRA_CURRENT_ADAPTER_INDEX = "current_adapter_index";
    private static final String EXTRA_CURRENT_SOURCE_ADAPTER_INDEX = "current_source_adapter_index";
    private static final String EXTRA_CURRENT_ADAPTER_NAME = "current_adapter_name";
    private static final String EXTRA_CURRENT_SOURCE_ADAPTER_NAME = "current_source_adapter_name";

    private DrawerLayout mDrawerLayout;
    private TextView mStatsDisplay;

    private boolean mUseDrawee = true;
    private boolean mAllowAnimations;
    private int mCurrentLoaderAdapterIndex;
    private int mCurrentSourceAdapterIndex;

    private ImageListAdapter mCurrentAdapter;
    private RecyclerView mRecyclerView;

    private List<String> mImageUrls = new ArrayList<>();

    private boolean mUrlsLocal;

    private PerfListener mPerfListener;
    private ViewPager viewPager;
    private Handler mHandler;
    private Runnable mStatsClockTickRunnable;
    private String mCurrentAdapterName;
    private String mCurrentSourceName;

    private TextView mImageSourceLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Drawables.init(getResources());

        setContentView(R.layout.activity_main);

        mStatsDisplay = (TextView) findViewById(R.id.stats_display);

        mImageSourceLabel = (TextView) findViewById(R.id.main_image_source_label);

        {// Set ViewPager for each Tabs
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                private Fragment[] items = {
                        new TileContentFragment(),
                        new CardContentFragment(),
                        new ListContentFragment()
                };
                String[] titles = getResources().getStringArray(R.array.page_titles);

                @Override
                public Fragment getItem(int position) {
                    return items[position];
                }

                @Override
                public int getCount() {
                    return titles.length;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    return titles[position];
                }
            });
            viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    setCurrentLoaderAdapter(mCurrentLoaderAdapterIndex);
                }
            });
        }

        {// Set Tabs inside Toolbar
//            TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
//            tabs.setupWithViewPager(viewPager);
        }

        {// Add Toolbar to Main screen
            setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
            // Adding menu icon to Toolbar
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
                supportActionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        {// Create Navigation drawer and inlfate layout
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            // Set behavior of Navigation drawer
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        // This method will trigger on item Click of navigation menu
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            switch(id) {
                                case R.id.main_action_image_source_local:
                                    setSourceAdapter(R.id.action_image_source_local);
                                    break;
                                case R.id.main_action_image_source_imgur:
                                    setSourceAdapter(R.id.action_image_source_network);
                                    break;
                                case R.id.action_settings:
                                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                    MainActivity.this.startActivity(intent);
                                    break;
                            }

                            // Close drawer on item click
                            mDrawerLayout.closeDrawers();
                            return true;
                        }
                    });
        }

        {// Add Floating Action Button to bottom right of main view
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: handle actioon
                    Snackbar.make(v, "Hello Snackbar!",
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }

        {// Init image loaders settings
            if (savedInstanceState != null) {
                mAllowAnimations = savedInstanceState.getBoolean(EXTRA_ALLOW_ANIMATIONS);
                mUseDrawee = savedInstanceState.getBoolean(EXTRA_USE_DRAWEE);
                mCurrentLoaderAdapterIndex = savedInstanceState.getInt(EXTRA_CURRENT_ADAPTER_INDEX);
                mCurrentSourceAdapterIndex = savedInstanceState.getInt(EXTRA_CURRENT_SOURCE_ADAPTER_INDEX);
                mCurrentAdapterName = savedInstanceState.getString(EXTRA_CURRENT_ADAPTER_NAME);
                mCurrentSourceName = savedInstanceState.getString(EXTRA_CURRENT_SOURCE_ADAPTER_NAME);
            } else {
                mAllowAnimations = true;
                mCurrentLoaderAdapterIndex = R.id.action_image_loader_fresco_okhttp;
                mCurrentSourceAdapterIndex = R.id.action_image_source_network;
                mCurrentAdapterName = "fresco + okhttp";
                mCurrentSourceName = "network";
            }
        }

        mCurrentAdapter = null;

        mHandler = new Handler(Looper.getMainLooper());
        mStatsClockTickRunnable = new Runnable() {
            @Override
            public void run() {
                updateStats();
                scheduleNextStatsClockTick();
            }
        };

        mImageSourceLabel.setText(mCurrentSourceName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateStats();
        scheduleNextStatsClockTick();
        loadUrls();
        setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onStop() {
        super.onStop();
        cancelNextStatsClockTick();
        resetAdapter();
    }

    private void scheduleNextStatsClockTick() {
        mHandler.postDelayed(mStatsClockTickRunnable, STATS_CLOCK_INTERVAL_MS);
    }

    private void cancelNextStatsClockTick() {
        mHandler.removeCallbacks(mStatsClockTickRunnable);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_ALLOW_ANIMATIONS, mAllowAnimations);
        outState.putBoolean(EXTRA_USE_DRAWEE, mUseDrawee);
        outState.putInt(EXTRA_CURRENT_ADAPTER_INDEX, mCurrentLoaderAdapterIndex);
        outState.putInt(EXTRA_CURRENT_SOURCE_ADAPTER_INDEX, mCurrentSourceAdapterIndex);
        outState.putString(EXTRA_CURRENT_ADAPTER_NAME, mCurrentAdapterName);
        outState.putString(EXTRA_CURRENT_SOURCE_ADAPTER_NAME, mCurrentSourceName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Check current selection
        if (mCurrentLoaderAdapterIndex != 0) {
            menu.findItem(mCurrentLoaderAdapterIndex).setChecked(true);
        }
        if (mCurrentSourceAdapterIndex != 0) {
            menu.findItem(mCurrentSourceAdapterIndex).setChecked(true);
        }
//        menu.findItem(R.id.allow_animations).setChecked(mAllowAnimations);
//        menu.findItem(R.id.use_drawee).setChecked(mUseDrawee);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int group_id = item.getGroupId();

        if (group_id == R.id.group_image_loader) {
            mCurrentAdapterName = (String) item.getTitle();
            setCurrentLoaderAdapter(id);
        } else if (group_id == R.id.group_image_source) {
            mCurrentSourceName = (String) item.getTitle();
            setSourceAdapter(id);
        }

        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    private ImageListAdapter getImageListAdapter(int pageIndex, int loaderId, final PerfListener pl) {
        boolean useDrawee = (loaderId == R.id.action_image_loader_fresco || loaderId == R.id.action_image_loader_fresco_okhttp);
        int[] layouts = {R.layout.instr_item_tile, R.layout.instr_item_card, R.layout.instr_item_list};
        int[] frescoLayouts = {R.layout.instr_item_tile_fresco, R.layout.instr_item_card_fresco, R.layout.instr_item_list_fresco};
        int[] volleyLayouts = {R.layout.instr_item_tile_fresco, R.layout.instr_item_card_fresco, R.layout.instr_item_list_fresco};

        final int layoutId;
        if (loaderId == R.id.action_image_loader_volley) {
            layoutId = volleyLayouts[pageIndex];
        } else {
            layoutId = useDrawee ? frescoLayouts[pageIndex] : layouts[pageIndex];
        }

        AdapterDelegate ad = new AdapterDelegate() {
            public ViewGroup getHolderView(ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(layoutId, parent, false );
                // TODO: set the view's size, margins, paddings and layout parameters
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DetailActivity.class);
                        String url = v.getTag(AdapterDelegate.KEY_DATA).toString();
                        intent.putExtra(AdapterDelegate.EXTRA_KEY_DATA, url);
                        context.startActivity(intent);
                    }
                });
                return (ViewGroup) view;
            }
        };

        switch (loaderId) {
            case R.id.action_image_loader_fresco:
                return new FrescoAdapter(this, pl, ImagePipelineConfigFactory.getImagePipelineConfig(this), ad);
            case R.id.action_image_loader_fresco_okhttp:
                return new FrescoAdapter(this, pl, ImagePipelineConfigFactory.getOkHttpImagePipelineConfig(this), ad);
            case R.id.action_image_loader_glide:
                return new GlideAdapter(this, pl, ad);
            case R.id.action_image_loader_picasso:
                return new PicassoAdapter(this, pl, ad);
            case R.id.action_image_loader_uil:
                return new UilAdapter(this, pl, ad);
            case R.id.action_image_loader_volley:
                return mUseDrawee ?
                        new VolleyDraweeAdapter(this, pl, ad) :
                        new VolleyAdapter(this, pl, ad);
            case R.id.action_image_loader_aquery:
                return new AQueryAdapter(this, pl, ad);
            default:
                return null;
        }
    }

    /**
     * Enforce adapter setup
     */
    public void setAdapter() {
        setCurrentLoaderAdapter(mCurrentLoaderAdapterIndex);
    }

    private void setCurrentLoaderAdapter(int id) {
        mCurrentLoaderAdapterIndex = id;
        mMaxThrouput = 0;
        mPixelsLoaded = 0;
        resetAdapter();
        mPerfListener = new PerfListener();
        int pageIndex = viewPager.getCurrentItem();
        mCurrentAdapter = getImageListAdapter(pageIndex, mCurrentLoaderAdapterIndex, mPerfListener);
        updateAdapter(mImageUrls);
        updateStats();

        FragmentPagerAdapter a = (FragmentPagerAdapter) viewPager.getAdapter();
        mRecyclerView = (RecyclerView) a.getItem(pageIndex).getView();
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCurrentAdapter);
        }
    }

    private void resetAdapter() {
        if (mCurrentAdapter != null) {
            mCurrentAdapter.shutDown();
            mCurrentAdapter = null;
            System.gc();
            System.runFinalization();
        }
    }

    private void updateAdapter(List<String> urls) {
        if (mCurrentAdapter != null) {
            mCurrentAdapter.clear();
            if (urls != null) {
                for (String url : urls) {
                    mCurrentAdapter.addUrl(url);
                }
            }
            mCurrentAdapter.notifyDataSetChanged();
        }
    }

    private int mPixelsLoaded;
    private int mMaxThrouput = -1;

    private void updateStats() {
        if (mPerfListener == null) return;

        final Runtime runtime = Runtime.getRuntime();
        final long heapMemory = runtime.totalMemory() - runtime.freeMemory();
        final StringBuilder sb = new StringBuilder(DEFAULT_MESSAGE_SIZE);
        // When changing format of output below, make sure to sync "run_comparison.py" as well
        sb.append("Loader: ").append(mCurrentAdapterName).append("\n");
        sb.append("Heap: ");
        appendSize(sb, heapMemory);
        sb.append(" java, ");
        appendSize(sb, Debug.getNativeHeapSize());
        sb.append(" native\n");
        appendTime(sb, "Avg wait time: ", mPerfListener.getAverageWaitTime(), "\n");
        appendNumber(sb, "Requests: ", mPerfListener.getOutstandingRequests(), " outsdng ");
        appendNumber(sb, "", mPerfListener.getCancelledRequests(), " cncld\n");
        int n = mPerfListener.getPixelsCount();
        int tp = (n - mPixelsLoaded);
        if (mMaxThrouput < tp) {
            mMaxThrouput = tp;
        }
//        sb.append("Max throuput " + mMaxThrouput + " pixels/sec");
        mPixelsLoaded = n;
        final String message = sb.toString();
        mStatsDisplay.setText(message);
        FLog.i(TAG, message);
    }

    private static void appendSize(StringBuilder sb, long bytes) {
        String value = String.format(Locale.getDefault(), "%.1f MB", (float) bytes / BYTES_IN_MEGABYTE);
        sb.append(value);
    }

    private static void appendTime(StringBuilder sb, String prefix, long timeMs, String suffix) {
        appendValue(sb, prefix, timeMs + " ms", suffix);
    }

    private static void appendNumber(StringBuilder sb, String prefix, long number, String suffix) {
        appendValue(sb, prefix, number + "", suffix);
    }

    private static void appendValue(StringBuilder sb, String prefix, String value, String suffix) {
        sb.append(prefix).append(value).append(suffix);
    }

    private void setSourceAdapter(int index) {
        if (index == mCurrentSourceAdapterIndex) return;

        FLog.v(TAG, "onImageSourceSelect: %d", index);
        mCurrentSourceAdapterIndex = index;
        switch (index) {
            case R.id.action_image_source_local:
                mUrlsLocal = true;
                mCurrentSourceName = "Local Photo Library";
                break;
            case R.id.action_image_source_network:
                mUrlsLocal = false;
                mCurrentSourceName = "Network";
                break;
            default:
                resetAdapter();
                mImageUrls.clear();
                return;
        }

        loadUrls();
        setCurrentLoaderAdapter(mCurrentLoaderAdapterIndex);
        mImageSourceLabel.setText(mCurrentSourceName);
    }

    private void loadUrls() {
        // TODO:
        if (mUrlsLocal) {
            loadLocalUrls();
        } else {
            loadNetworkUrls();
        }
    }

    private void loadNetworkUrls() {
        String url = "https://api.imgur.com/3/gallery/hot/viral/0.json";
        ImageSize staticSize = chooseImageSize();
        ImageUrlsRequestBuilder builder = new ImageUrlsRequestBuilder(url)
                .addImageFormat(ImageFormat.JPEG, staticSize)
                .addImageFormat(ImageFormat.PNG, staticSize);
        if (mAllowAnimations) {
            builder.addImageFormat(ImageFormat.GIF, ImageSize.ORIGINAL_IMAGE);
        }
        ImageUrlsFetcher.getImageUrls(
                builder.build(),
                new ImageUrlsFetcher.Callback() {
                    @Override
                    public void onFinish(List<String> result) {
                        // If the user changes to local images before the call comes back, then this should
                        // be ignored
                        if (!mUrlsLocal) {
                            mImageUrls = result;
                            updateAdapter(mImageUrls);
                        }
                    }
                });
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    private void loadLocalUrls() {
        Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(externalContentUri, projection, null, null, null);

            mImageUrls.clear();

            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

            String imageId;
            Uri imageUri;
            while (cursor.moveToNext()) {
                imageId = cursor.getString(columnIndex);
                imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
                mImageUrls.add(imageUri.toString());
            }
        } catch (Throwable t) {
            FLog.e(TAG, "" + t.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static int calcDesiredSize(Context context, int parentWidth, int parentHeight) {
        if (parentHeight < 10 || parentWidth < 10) {
            return 640;
        }
        int orientation = context.getResources().getConfiguration().orientation;
        int desiredSize = (orientation == Configuration.ORIENTATION_LANDSCAPE) ?
                parentHeight / 2 : parentHeight / 3;
        return Math.min(desiredSize, parentWidth);
    }

    private ImageSize chooseImageSize() {
        if (viewPager == null) return ImageSize.LARGE_THUMBNAIL;
        int c = viewPager.getCurrentItem();
        FragmentPagerAdapter a = (FragmentPagerAdapter) viewPager.getAdapter();
        if (a == null) return ImageSize.LARGE_THUMBNAIL;
        mRecyclerView = (RecyclerView) a.getItem(c).getView();
        if (mRecyclerView == null) return ImageSize.LARGE_THUMBNAIL;
        ViewGroup.LayoutParams layoutParams = mRecyclerView.getLayoutParams();
        if (layoutParams == null) return ImageSize.LARGE_THUMBNAIL;
        int size = calcDesiredSize(this, layoutParams.width, layoutParams.height);
        if (size <= 90) {
            return ImageSize.SMALL_SQUARE;
        } else if (size <= 160) {
            return ImageSize.SMALL_THUMBNAIL;
        } else if (size <= 320) {
            return ImageSize.MEDIUM_THUMBNAIL;
        } else if (size <= 640) {
            return ImageSize.LARGE_THUMBNAIL;
        } else if (size <= 1024) {
            return ImageSize.HUGE_THUMBNAIL;
        } else {
            return ImageSize.ORIGINAL_IMAGE;
        }
    }
}
