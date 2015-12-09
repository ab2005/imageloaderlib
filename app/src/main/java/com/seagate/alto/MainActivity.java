/*
 * Copyright (C) 2015 Seagate LLC
 */

package com.seagate.alto;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.seagate.alto.pages.CardContentFragment;
import com.seagate.alto.pages.ListContentFragment;
import com.seagate.alto.pages.TileContentFragment;
import com.seagate.imageadapter.adapters.Adapter;
import com.seagate.imageadapter.instrumentation.PerfListener;
import com.seagate.imageadapter.urlsfetcher.ImageFormat;
import com.seagate.imageadapter.urlsfetcher.ImageSize;
import com.seagate.imageadapter.urlsfetcher.ImageUrlsFetcher;
import com.seagate.imageadapter.urlsfetcher.ImageUrlsRequestBuilder;

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
    private static final String EXTRA_CURRENT_ADAPTER_INDEX = "current_adapter_index";
    private static final String EXTRA_CURRENT_SOURCE_ADAPTER_INDEX = "current_source_adapter_index";
    private static final String EXTRA_CURRENT_ADAPTER_NAME = "current_adapter_name";
    private static final String EXTRA_CURRENT_SOURCE_ADAPTER_NAME = "current_source_adapter_name";
    private static final String EXTRA_IMAGE_URL_LIST = "image_url_list";

    private DrawerLayout mDrawerLayout;
    private TextView mStatsDisplay;

    private int mCurrentLoaderAdapterIndex;
    private int mCurrentSourceAdapterIndex;

    private Adapter mCurrentAdapter;
    private RecyclerView mRecyclerView;

    private ArrayList<String> mImageUrls = new ArrayList<>();

    private boolean mUrlsLocal;

    private ViewPager mViewPager;
    private Handler mHandler;
    private Runnable mStatsClockTickRunnable;
    private String mCurrentAdapterName;
    private String mCurrentSourceName;

    private TextView mImageSourceLabel;

    private MyAdapterDelegate mAdapterDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mStatsDisplay = (TextView) findViewById(R.id.stats_display);

        mImageSourceLabel = (TextView) findViewById(R.id.main_image_source_label);

        {// Set ViewPager for each Tabs
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
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
            mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    setCurrentLoaderAdapter(mCurrentLoaderAdapterIndex);
                }
            });
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
                            switch (id) {
                                case R.id.main_action_image_source_local:
                                    setSourceAdapter(R.id.action_image_source_local);
                                    break;
                                case R.id.main_action_image_source_imgur:
                                    setSourceAdapter(R.id.action_image_source_network);
                                    break;
                                case R.id.action_settings:
                                    // TODO
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
                Log.d(TAG, "onCreate(): savedInstanceState");

                mCurrentLoaderAdapterIndex = savedInstanceState.getInt(EXTRA_CURRENT_ADAPTER_INDEX);
                mCurrentSourceAdapterIndex = savedInstanceState.getInt(EXTRA_CURRENT_SOURCE_ADAPTER_INDEX);
                mCurrentAdapterName = savedInstanceState.getString(EXTRA_CURRENT_ADAPTER_NAME);
                mCurrentSourceName = savedInstanceState.getString(EXTRA_CURRENT_SOURCE_ADAPTER_NAME);
                mImageUrls = savedInstanceState.getStringArrayList(EXTRA_IMAGE_URL_LIST);
            } else {
                mCurrentLoaderAdapterIndex = R.id.action_image_loader_fresco_okhttp;
                mCurrentSourceAdapterIndex = R.id.action_image_source_network;
                mCurrentAdapterName = "fresco + okhttp";
                mCurrentSourceName = "network";
            }
        }

        mHandler = new Handler(Looper.getMainLooper());
        mStatsClockTickRunnable = new Runnable() {
            @Override
            public void run() {
                updateStats();
                scheduleNextStatsClockTick();
            }
        };

        mImageSourceLabel.setText(mCurrentSourceName);

        mAdapterDelegate = new MyAdapterDelegate(this);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
        updateStats();
        scheduleNextStatsClockTick();
        loadUrls();
        setAdapter();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    protected void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
        cancelNextStatsClockTick();
        resetAdapter();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    private void scheduleNextStatsClockTick() {
        mHandler.postDelayed(mStatsClockTickRunnable, STATS_CLOCK_INTERVAL_MS);
    }

    private void cancelNextStatsClockTick() {
        mHandler.removeCallbacks(mStatsClockTickRunnable);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_CURRENT_ADAPTER_INDEX, mCurrentLoaderAdapterIndex);
        outState.putInt(EXTRA_CURRENT_SOURCE_ADAPTER_INDEX, mCurrentSourceAdapterIndex);
        outState.putString(EXTRA_CURRENT_ADAPTER_NAME, mCurrentAdapterName);
        outState.putString(EXTRA_CURRENT_SOURCE_ADAPTER_NAME, mCurrentSourceName);
        outState.putStringArrayList(EXTRA_IMAGE_URL_LIST, mImageUrls);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        } else if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    private Adapter getImageListAdapter(int pageIndex, int loaderId) {
        mAdapterDelegate.setLayout(pageIndex, loaderId);
        switch (loaderId) {
            case R.id.action_image_loader_fresco:
                return Adapter.buildAdapter(Adapter.FRESCO, mAdapterDelegate);
            case R.id.action_image_loader_fresco_okhttp:
                return Adapter.buildAdapter(Adapter.FRESCO_OKHTTP, mAdapterDelegate);
            case R.id.action_image_loader_glide:
                return Adapter.buildAdapter(Adapter.GLIDE, mAdapterDelegate);
            case R.id.action_image_loader_picasso:
                return Adapter.buildAdapter(Adapter.PICASSO, mAdapterDelegate);
            case R.id.action_image_loader_uil:
                return Adapter.buildAdapter(Adapter.UNIVERSAL_IMAGE_LIBRARY, mAdapterDelegate);
            case R.id.action_image_loader_volley:
                return Adapter.buildAdapter(Adapter.VOLLEY, mAdapterDelegate);
            case R.id.action_image_loader_volley_drawee:
                return Adapter.buildAdapter(Adapter.VOLLEY_DRAWEE, mAdapterDelegate);
            case R.id.action_image_loader_aquery:
                return Adapter.buildAdapter(Adapter.ANDROID_QUERY, mAdapterDelegate);
            default:
                throw new IllegalArgumentException("Invalid adaper type");
        }
    }

    /**
     * Enforce adapter setup
     */
    public void setAdapter() {
        Log.d(TAG, "setAdapter");
        setCurrentLoaderAdapter(mCurrentLoaderAdapterIndex);
    }

    private void setCurrentLoaderAdapter(int id) {
        Log.d(TAG, "setAdapter " + id + ", #urls = " + mImageUrls.size());
        mCurrentLoaderAdapterIndex = id;
        mMaxThrouput = 0;
        mPixelsLoaded = 0;
        resetAdapter();
        int pageIndex = mViewPager.getCurrentItem();
        mCurrentAdapter = getImageListAdapter(pageIndex, mCurrentLoaderAdapterIndex);
        updateAdapter(mImageUrls);
        updateStats();

        FragmentPagerAdapter a = (FragmentPagerAdapter) mViewPager.getAdapter();
        mRecyclerView = (RecyclerView) a.getItem(pageIndex).getView();
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCurrentAdapter);
        } else {
            Log.d(TAG, "setAdapter: mRecyclerView == null");
        }
    }

    private void resetAdapter() {
        if (mCurrentAdapter != null) {
            mCurrentAdapter.dispose();
            mCurrentAdapter = null;
            mAdapterDelegate.resetProfiler();
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
        if (mAdapterDelegate == null) return;

        PerfListener profiler = mAdapterDelegate.getPerformanceListener();
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
        appendTime(sb, "Avg wait time: ", profiler.getAverageWaitTime(), "\n");
        appendNumber(sb, "Requests: ", profiler.getOutstandingRequests(), " outsdng ");
        appendNumber(sb, "", profiler.getCancelledRequests(), " cncld\n");
        int n = profiler.getPixelsCount();
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

    private boolean checkRequestPermission(final String permission) {
        int permissionState = ActivityCompat.checkSelfPermission(this, permission);
        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, permission + " permission has NOT been granted.");
            boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            if (shouldProvideRationale) {
                View rootView = findViewById(R.id.main_content);
                String rationale = "TODO Permission rationale to request internet access";//R.string.camera_permission_rationale;
                Log.i(TAG, "Displaying " + permission + " permission rationale to provide additional context.");
                Snackbar.make(rootView, rationale, Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK" /*R.string.ok*/, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, 123);
                            }
                        })
                        .show();
            } else {
                Log.i(TAG, "Requesting " + permission + " permission");
                // Request permission. It's possible this can be auto answered if device policy
                // sets the permission in a given state or the user denied the permission
                // previously and checked "Never ask again".
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, 123);
            }
            return false;
        } else {
            Log.i(TAG, permission + " permission has already been granted.");
            return true;
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (grantResults.length <= 0) {
            // If user interaction was interrupted, the permission request is cancelled and you
            // receive empty arrays.
            Log.i(TAG, "Permission request was cancelled.");
        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission was granted.;
            if (Manifest.permission.INTERNET.equals(permissions[0])) {
                loadNetworkUrls();
            } else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[0])) {
                loadLocalUrls();
            }
        } else {
            // Permission denied.
            // In this Activity we've chosen to notify the user that they
            // have rejected a core permission for the app since it makes the Activity useless.
            // We're communicating this message in a Snackbar since this is a sample app, but
            // core permissions would typically be best requested during a welcome-screen flow.

            // Additionally, it is important to remember that a permission might have been
            // rejected without asking the user for permission (device policy or "Never ask
            // again" prompts). Therefore, a user interface affordance is typically implemented
            // when permissions are denied. Otherwise, your app could appear unresponsive to
            // touches or interactions which have required permissions.
            View rootView = findViewById(R.id.main_content);
            String deniedExplanation = "R.string.camera_permission_denied_explanation";//R.string.camera_permission_denied_explanation;
            Snackbar.make(rootView, deniedExplanation, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Settings"/*R.string.settings*/, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    private void loadNetworkUrls() {
        if (!checkRequestPermission(Manifest.permission.INTERNET)) {
            return;
        }

        String url = "https://api.imgur.com/3/gallery/hot/viral/0.json";
        ImageSize staticSize = chooseImageSize();
        ImageUrlsRequestBuilder builder = new ImageUrlsRequestBuilder(url)
                .addImageFormat(ImageFormat.JPEG, staticSize)
                .addImageFormat(ImageFormat.PNG, staticSize)
                .addImageFormat(ImageFormat.GIF, ImageSize.ORIGINAL_IMAGE);

        ImageUrlsFetcher.getImageUrls(
                builder.build(),
                new ImageUrlsFetcher.Callback() {
                    @Override
                    public void onFinish(List<String> result) {
                        // If the user changes to local images before the call comes back, then this should
                        // be ignored
                        if (!mUrlsLocal) {
                            mImageUrls = new ArrayList<String>(result);
                            updateAdapter(mImageUrls);
                        }
                    }
                });
    }

    private void loadLocalUrls() {
        if (!checkRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }

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
            Log.e(TAG, "" + t.getMessage());
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
        if (mViewPager == null) return ImageSize.LARGE_THUMBNAIL;
        int c = mViewPager.getCurrentItem();
        FragmentPagerAdapter a = (FragmentPagerAdapter) mViewPager.getAdapter();
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

    public RecyclerView.Adapter getCurrentAdaper() {
        return mCurrentAdapter;
    }
}
