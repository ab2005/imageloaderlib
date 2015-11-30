#Image Loader Adapter Library

Image Loader Adapter Library is a wrapper on top of RecyclerView.Adapter
providing a simple unified interface to the most popular image loaders:
Fresco, Picasso, Universal Image Loader, Glide, AQuery and Volley.

In addition to the option of dynamically selecting the underlying image loader
at the runtime the Image Loader Adapter Library allows:
- detailed performance profiling including native and java heap usage, average image load time,
number of successful, pending and canceled network requests
- custom debug painting
- convenient application debugging of the application using Chrome browser Developers Tools

## Requirements
Image Loader Adapter Library can be included in any Android application.
The library supports Android API Level: 16 Android 4.1 (JELLY_BEAN) and later.
## Setup
To use Image Loader Library copy the archive file to the libs folder
and include the following into the repositories and dependencies sections
of your <code>build.graddle</code> file:

```
repositories {
...
    flatDir {
        dirs 'libs'
    }
}
dependencies {
...
    compile (name:'imageadapterlib', ext:'aar')
}
```

Alternatively you can simple add the following line to the dependencies section of your <code>build.gradle file:

```
    compile 'com.seagate.alto.imageloaderlib:+'
```

## Getting Started

### Provide permissions
For images from the network, you will need to request Internet permission
from your users. Add this line to your AndroidManifest.xml file:
```Android
  <uses-permission android:name="android.permission.INTERNET"/>
```

### Initilaize the library
Near your application startup, before your app calls setContentView(),
initialize the Image Loader Adapter Library.
You should only call initialize once. Your Application class would be a good place.


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

### Use custom image views
The library provides two custom image view classes to use in the xml layouts:
- InstrumentedDraweeView for use with Fresco library
- InstrumentedImageView for all other libraries

To use Fresco add a custom namespace to the top-level element of the xml layout:
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

For other libraries:

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

### Define Adapter Delegate

To build an image loader adapter you have to provide an instance of the
Adapter.Delegate:
```java
private final PerformanceListener profiler = new PerformanceListener();

Adapter.Delegate delegate = new Adapter.Delegate() {
      final int layoutId = R.layout.instr_item_tile_fresco;
      public ViewGroup getHolderView(ViewGroup parent, int viewType) {
          final View view = getLayoutInflater().inflate(layoutId, parent, false );
          // TODO: set the view's size, margins, paddings and layout parameters
          // attach necessary event handlers...
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
### Build Image Loader Adapter

```java
  Adapter adapter = Adapter.build(Adapter.FRESCO);
```

### Measure Performance

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

 ### Debug with Chrome Dev Tools
The integration with the Chrome DevTools frontend is implemented using a
client/server protocol which the Stetho software provides for your application.
Simply navigate to chrome://inspect and click "Inspect" to get started!

Http Network inspection is possible with the full spectrum of Chrome Developer Tools features, including image preview, JSON response helpers,
and exporting traces to the HAR format

![alt text](http://facebook.github.io/stetho/static/images/inspector-network.png)

View hierarchy is supported! Lots of goodies such as instances virtually placed
 in the hierarchy, view highlighting, and the ability to tap on a view to jump
 to its position in the hierarchy.

## Contributions
TODO:
## Changelog
TODO
## License
TODO:
