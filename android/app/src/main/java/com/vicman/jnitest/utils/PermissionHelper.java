package com.vicman.jnitest.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;

public class PermissionHelper {
    public static final String TAG = Utils.getTag(PermissionHelper.class);
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 31;

    private interface PermissionApi {
        Activity getActivity();
        Context getContext();
        int checkSelfPermission(@NonNull String permission);
        void requestPermissions(@NonNull String[] permissions, int requestCode);
    }


    private PermissionHelper() {}

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasWriteExternalStorage(final Context context) {
        return !Utils.hasMarshmallow() || context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /*
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasReadExternalStorage(final Context context) {
        return !Utils.hasMarshmallow() || context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasPermissions(final Context context, final @NonNull String... permissions) {
        if (!Utils.hasMarshmallow()) {
            return true;
        }

        if (permissions.length <= 0) {
            throw new IllegalStateException("No permission to check!");
        }

        for (final String p : permissions) {
            if (context.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
    */

    public static boolean getPermissions(final Fragment fragment, final int requestCode, final @NonNull String... permissions) {
        if (!Utils.hasMarshmallow()) {
            return true;
        }

        final PermissionApi permissionApi = new PermissionApi() {
            @Override
            public @Nullable Context getContext() {
                return fragment.getContext();
            }

            @Override
            public @Nullable Activity getActivity() {
                return fragment.getActivity();
            }

            @Override
            public int checkSelfPermission(final @NonNull String permission) {
                final @Nullable Context context = this.getContext();
                return context == null ? PackageManager.PERMISSION_DENIED
                        : ContextCompat.checkSelfPermission(context, permission);
            }

            @Override
            public void requestPermissions(final @NonNull String[] permissions, final int requestCode) {
                fragment.requestPermissions(permissions, requestCode);
            }
        };

        return getPermissions(permissionApi, requestCode, permissions);
    }

    public static boolean getPermissions(final Activity activity, final int requestCode, final @NonNull String... permissions) {
        if (!Utils.hasMarshmallow()) {
            return true;
        }

        final PermissionApi  permissionApi = new PermissionApi() {
            @Override
            public Context getContext() {
                return activity.getApplicationContext();
            }

            @Override
            public Activity getActivity() {
                return activity;
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public int checkSelfPermission(final @NonNull String permission) {
                return activity.checkSelfPermission(permission);
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void requestPermissions(final @NonNull String[] permissions, final int requestCode) {
                activity.requestPermissions(permissions, requestCode);
            }
        };

        return getPermissions(permissionApi, requestCode, permissions);
    }

    private static boolean getPermissions(final @NonNull PermissionApi permissionApi, final int requestCode, final @NonNull String... permissions) {
        if (!Utils.hasMarshmallow()) {
            return true;
        } else if (permissions.length <= 0) {
            throw new IllegalStateException("No permission to check!");
        }

        final ArrayList<String> requiredPermissions = new ArrayList<>(permissions.length);
        for (final String p : permissions) {
            if (permissionApi.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(p);
            }
        }

        if (requiredPermissions.size() == 0) {
            return true;
        }

        permissionApi.requestPermissions(requiredPermissions.toArray(new String[requiredPermissions.size()]), requestCode);
        return false;
    }

}
