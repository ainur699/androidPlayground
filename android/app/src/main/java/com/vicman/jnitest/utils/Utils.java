package com.vicman.jnitest.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import com.vicman.jnitest.activities.BaseActivity;
import org.jetbrains.annotations.Contract;

import java.io.Closeable;
import java.io.File;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Map;

public class Utils {
    private static final int TAG_MAX_LEN = 23;

    private Utils() {}


    public static @NonNull String getTag(final @NonNull Class c) {
        final String tagFullName = c.getSimpleName();
        return tagFullName.length() > TAG_MAX_LEN ? tagFullName.substring(0, TAG_MAX_LEN) : tagFullName;
    }

    public static boolean isInternetConnectionAvailable(Context context) {
        try {
            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                final NetworkInfo netInfo = cm.getActiveNetworkInfo();
                return netInfo != null && netInfo.isConnected();
            } else {
                throw new IllegalStateException("getSystemService(Context.CONNECTIVITY_SERVICE) = null!");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        // something wrong, unknown connection state - return connected
        return true;
    }

//--------------------------------------------------------------------------------------------------

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Contract("null -> true")
    public static boolean isDead(@Nullable final Activity activity) {
        return activity == null || activity.isFinishing() || (Utils.hasJellyBeanMR1() ? activity.isDestroyed()
        : activity instanceof BaseActivity && ((BaseActivity) activity).isDestroyed());
    }

    @Contract("null -> true")
    public static boolean isDead(final @Nullable Fragment fragment) {
        return fragment == null || !fragment.isAdded() || isDead(fragment.getActivity());
    }

    @Contract("null -> true")
    public static boolean isDead(final @Nullable View view) {
        return view == null || isDead(view.getContext());
    }

    @Contract("null -> true")
    public static boolean isDead(final @Nullable Context context) {
        if (context == null) {
            return true;
        } else if (context instanceof Activity) {
            return isDead((Activity) context);
        } else if (context instanceof ContextWrapper) {
            return isDead(((ContextWrapper) context).getBaseContext());
        }

        return false;
    }

//--------------------------------------------------------------------------------------------------

    /**
     * API level 16, Android 4.1.2
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * API level 17, Android 4.2.2
     */
    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * API level 18, Android 4.3
     */
    public static boolean hasJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * API level 19, Android 4.4
     */
    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * API level 21, Android 5.0
     */
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * API level 22, Android 5.1
     */
    public static boolean hasLollipopMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    /**
     * API level 23, Android 6.0
     */
    public static boolean hasMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * API level 24, Android 7.0
     */
    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }


    /**
     * API level 26, Android 8.0
     */
    public static boolean hasOreo() {
        return Build.VERSION.SDK_INT >= 26;
    }

//--------------------------------------------------------------------------------------------------

    /**
     *
     * @param context - context for the class loader
     * @param args - fragment arguments
     * @param savedInstanceState - savedInstanceState
     * @return savedInstanceState
     */
    public static @Nullable Bundle setClassLoader(final @NonNull Context context, final @Nullable Bundle args, final @Nullable Bundle savedInstanceState) {
        final ClassLoader classLoader = context.getClassLoader();

        if (args != null) {
            args.setClassLoader(classLoader);
        }

        if (savedInstanceState != null) {
            savedInstanceState.setClassLoader(classLoader);
        }

        return savedInstanceState;
    }

    /**
     *
     * @param context - context for the class loader
     * @param intent - input intent
     * @param savedInstanceState - savedInstanceState
     * @return savedInstanceState
     */
    public static @Nullable Bundle setClassLoader(final @NonNull Context context, final @Nullable Intent intent, final @Nullable Bundle savedInstanceState) {
        final ClassLoader classLoader = context.getClassLoader();

        if (intent != null) {
            intent.setExtrasClassLoader(classLoader);
        }

        if (savedInstanceState != null) {
            savedInstanceState.setClassLoader(classLoader);
        }

        return savedInstanceState;
    }

    public static @Nullable Intent setClassLoader(final @NonNull Context context, final @Nullable Intent intent) {
        if (intent != null) {
            intent.setExtrasClassLoader(context.getClassLoader());
        }

        return intent;
    }

//--------------------------------------------------------------------------------------------------

    @Contract("null -> true")
    public static boolean isEmpty(final @Nullable Uri uri) {
        return uri == null || Uri.EMPTY.equals(uri);
    }

    public static boolean isEmpty(final @Nullable Bundle o) {
        return o == null || o.isEmpty();
    }

    @Contract("null -> true")
    public static boolean isEmpty(final @Nullable CharSequence title) {
        return TextUtils.isEmpty(title);
    }

    @Contract("null -> true")
    public static boolean isEmpty(final @Nullable Collection o) {
        return o == null || o.isEmpty();
    }

    @Contract("null -> true")
    public static boolean isEmpty(final @Nullable Object[] o) {
        return o == null || o.length <= 0;
    }

    @Contract("null -> true")
    public static boolean isEmpty(final @Nullable int[] ints) {
        return ints == null || ints.length <= 0;
    }

    @Contract("null -> true")
    public static boolean isEmpty(final @Nullable long[] longs) {
        return longs == null || longs.length <= 0;
    }

    @Contract("null -> true")
    public static boolean isEmpty(final @Nullable Map map) {
        return map == null || map.size() <= 0;
    }

//--------------------------------------------------------------------------------------------------

    public static boolean contains(final @NonNull int[] array, final int target) {
        for (final int item : array) {
            if (item == target) {
                return true;
            }
        }

        return false;
    }

    public static boolean contains(final @Nullable String[] array, final @Nullable String target) {
        if (Utils.isEmpty(array) || target == null) {
            return false;
        }

        for (final String item : array) {
            if (target.equals(item)) {
                return true;
            }
        }

        return false;
    }

//--------------------------------------------------------------------------------------------------

    public static @NonNull String getDiskCacheDir(final @NonNull Context context) {
        return getDiskCacheDir(context, null);
    }

    public static @NonNull String getDiskCacheDir(final @NonNull Context context, final @Nullable String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final File cachePath = isExternalStorageAvailable() && context.getExternalCacheDir() != null
        ? context.getExternalCacheDir() : context.getCacheDir();

        String path = cachePath.getAbsolutePath();
        if (!isEmpty(uniqueName)) {
            path += File.separator + uniqueName;
        }

        return path;
    }

    public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable();
    }

//--------------------------------------------------------------------------------------------------

    public static void closeSilently(final @Nullable Closeable o) {
        if (o != null) {
            try {
                o.close();
            } catch (final Throwable t) {
                t.printStackTrace();
            }
        }
    }

    // old version of Cursor class does not implemented Closeable interface
    public static void closeSilently(final @Nullable Cursor c) {
        if (c != null) {
            try {
                c.close();
            } catch (final Throwable t) {
                t.printStackTrace();
            }
        }
    }

    // old version of ParcelFileDescriptor class does not implemented Closeable interface
    public static void closeSilently(final @Nullable ParcelFileDescriptor pfd) {
        if (pfd != null) {
            try {
                pfd.close();
            } catch (final Throwable t) {
                t.printStackTrace();
            }
        }
    }

    // old version of AssetFileDescriptor class does not implemented Closeable interface
    public static void closeSilently(final @Nullable AssetFileDescriptor afd) {
        if (afd != null) {
            try {
                afd.close();
            } catch (final Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static void closeSilently(final @Nullable HttpURLConnection connection) {
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (final Throwable t) {
                t.printStackTrace();
            }
        }
    }

//--------------------------------------------------------------------------------------------------

    public static @NonNull String getClientPlatformId() {
        return "photoLabFreeAndroid-v4637";
    }

}
