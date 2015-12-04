#Image Loader Adapter Library

Image Loader Adapter Library is a wrapper on top of RecyclerView.Adapter
providing a simple unified interface to the most popular image loaders:
[Fresco](http://frescolib.org/index.html), [Picasso](http://square.github.io/picasso/),
[UniversalImageLoader](https://github.com/nostra13/Android-Universal-Image-Loader),
[Glide](https://github.com/bumptech/glide), [AQuery](https://code.google.com/p/android-query/wiki/ImageLoading)
and [Volley](http://developer.android.com/training/volley/index.html).

In addition to the option of dynamically selecting the underlying image loader
at the runtime the Image Loader Adapter Library allows:

* detailed performance profiling including native and java heap usage, average image load time,
number of successful, pending and canceled network requests
* custom debug painting
* convenient application debugging of the application using Chrome browser Developers Tools


## Requirements

Image Loader Adapter Library can be included in any Android application.
The library supports Android API Level: 16 Android 4.1 (JELLY_BEAN) and later.


## Setup

To use the Image Loader Adapter Library you need include the following into the repositories and dependencies sections
of your <code>build.graddle</code> file:

```javascript
repositories {
    maven {
        url 'https://dl.bintray.com/ab2005/maven/'
    }
}
dependencies {
    compile 'com.seagate.alto:imageadapter:+'
}
```

Alternatively you can simple add the following line to the dependencies section of your <code>build.gradle file:

```javascript
    compile 'com.seagate.alto:imageadapter:+'
```

## Getting Started

### Providing application permissions

For images from the network, you will need to request Internet permission
from your users. Add this line to your AndroidManifest.xml file:

```
  <uses-permission android:name="android.permission.INTERNET"/>
```

### Initilaizing the library

Near your application startup, before your app calls setContentView(),
initialize the Image Loader Adapter Library.
You should only call initialize once. The Application class would be a good place.

```java
import android.app.Application;
import com.seagate.imageadapter.Adapter;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Adapter.initializeWithDefaults(this);
    }
}
```

### Defining layout with instrumented image views

The library provides two customized image view classes to use in the xml layouts:

* InstrumentedDraweeView for use with Fresco library
* InstrumentedImageView for all other libraries

When defining view layout to use with the Fresco image loader you need to add a custom namespace to the
top-level element of the xml layout and define the attributes of the
`com.seagate.imageadapter.instrumentation.InstrumentedDraweeView` such as:

```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:fresco="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <com.seagate.imageadapter.instrumentation.InstrumentedDraweeView
        android:id="@+id/instr.image"
        android:layout_width="@dimen/avator_size"
        android:layout_height="@dimen/avator_size"
        fresco:fadeDuration="300"
        fresco:actualImageScaleType="focusCrop"
        fresco:placeholderImage="@color/image_placeholder"
        fresco:placeholderImageScaleType="fitCenter"
        fresco:failureImage="@color/image_error"
        fresco:failureImageScaleType="centerInside"
        fresco:retryImage="@color/image_retrying"
        fresco:retryImageScaleType="centerCrop"
        fresco:progressBarImage="@color/image_progress_bar"
        fresco:progressBarImageScaleType="centerInside"
        fresco:progressBarAutoRotateInterval="1000"
        fresco:pressedStateOverlayImage="@color/white"
        fresco:roundAsCircle="true"/>
```

To define layout for all other image loaders use `com.seagate.imageadapter.instrumentation.InstrumentedImageView` class:

```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <com.seagate.imageadapter.instrumentation.InstrumentedImageView
        android:id="@+id/instr.image"
        android:layout_width="@dimen/avator_size"
        android:layout_height="@dimen/avator_size"/>
```

### Creating Adapter Delegate

To obtain an instance of the image loader adapter you need to provide an instance of the
Adapter.Delegate:

```java
private final PerformanceListener profiler = new PerformanceListener();

Adapter.Delegate delegate = new Adapter.Delegate() {
      final int layoutId = R.layout.instr_item_tile_fresco;
      public ViewGroup getHolderView(ViewGroup parent, int viewType) {
          final View view = getLayoutInflater().inflate(layoutId, parent, false );
          // TODO: set the view's size, margins, paddings and layout parameters
          // Attach necessary event handlers...
          view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    String url = (String)view.getTag(getImageViewId());
                    intent.putExtra("EXTRA_KEY_IMAGE_URL", url);
                    context.startActivity(intent);
                }
          });
          return (ViewGroup) view;
      }

      @Override
      public int getImageViewId() {
          return R.id.instr_image;
      }

      @Override
      public void bind(View itemView, String url) {
          itemView.setTag(getImageViewId(), url);
      }

      @Override
      public Context getContext() {
          return MainActivity.this;
      }

      @Override
      public PerfListener getPerformanceListener() {
          return profiler;
      }
  };
```

It is a responsibility of the `delegate` to provide the application context,
performance listener, create instance of the item view and report id of the
image view. The delegate will be notified when the view is binded to the data,
so it could perform necessary UI updates.

The Performance Listener wil be called to collect performance stats.

### Building Image Loader Adapter

```java
  Adapter adapter = Adapter.build(Adapter.FRESCO, delegate);
```

### Collecting Performance Stats

You can query the instance of your PerformanceListener to obtain the image loader stats:

```java
private void updateStats() {
    final long heapMemory = Runtime.getRuntime().totalMemory() - runtime.freeMemory();
    final StringBuilder sb = new StringBuilder(DEFAULT_MESSAGE_SIZE);
    sb.append("Heap: ");
    appendSize(sb, heapMemory);
    sb.append(" java, ");
    appendSize(sb, Debug.getNativeHeapSize());
    sb.append(" native\n");
    appendTime(sb, "Avg wait time: ", profiler.getAverageWaitTime(), "\n");
    appendNumber(sb, "Requests: ", profiler.getOutstandingRequests(), " outsdng ");
    appendNumber(sb, "", profiler.getCancelledRequests(), " cncld\n");
    int n = profiler.getPixelsCount();
    sb.append("Pixels loaded: " + n);
    final String message = sb.toString();
    mStatsDisplay.setText(message);
    Log.i(TAG, message);
}
```

### Debugging with Chrome DevTools

The integration with the Chrome DevTools frontend is implemented using a
client/server protocol which the [Stetho](http://facebook.github.io/stetho/) software provides for your application.
Simply navigate to [chrome://inspect](chrome://inspect/#devices) and click "Inspect" to get started!

![alt text](http://facebook.github.io/stetho/static/images/inspector-discovery.png)

Http Network inspection is possible with the full spectrum of Chrome Developer Tools features, including image preview, JSON response helpers,
and exporting traces to the HAR format

![alt text](http://facebook.github.io/stetho/static/images/inspector-network.png)

You application view hierarchy is supported! Lots of goodies such as instances virtually placed
 in the hierarchy, view highlighting, and the ability to tap on a view to jump
 to its position in the hierarchy.


## Contributions
TODO:

## Changelog
TODO

## License
TODO:
