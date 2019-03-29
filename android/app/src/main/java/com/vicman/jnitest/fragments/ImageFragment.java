package com.vicman.jnitest.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.vicman.jnitest.R;
import com.vicman.jnitest.utils.PermissionHelper;
import com.vicman.jnitest.utils.Utils;
import org.opencv.core.Mat;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import butterknife.BindView;
import butterknife.OnClick;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class ImageFragment extends BaseFragment {
    public static final @NonNull String TAG = Utils.getTag(ImageFragment.class);
    public static final @NonNull String PAGE_TITLE = "Image test";
    private static final int GALLERY_REQUEST_CODE = 10_001;

    protected @BindView(R.id.image_view) ImageView mImageView;

    static { System.loadLibrary("jnitest"); }

    private native void ProcessImage(Bitmap inputBitmap, long outputMatAddr);
    public static @NonNull ImageFragment createInstance() {
        return new ImageFragment();
    }

    @Override
    public @Nullable View onCreateView(final @NonNull LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.image_fragment, container, false);
        testImage();
        return returnView;
    }

    @OnClick(R.id.button_open_image)
    public void openImage() {
        if (this.isDead()) {
            return;
        }

        this.pickFromGallery();
    }

    @OnClick(R.id.button_test_image)
    public void testImage() {
        if (this.isDead()) {
            return;
        }

        new JniAsyncTask().execute();
    }

    private void pickFromGallery() {
        if (!PermissionHelper.getPermissions(this, PermissionHelper.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }

        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        if (Utils.hasJellyBean()) {
            final String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }

        this.startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        if (!isDead() && requestCode == PermissionHelper.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
        && Utils.contains(permissions, Manifest.permission.READ_EXTERNAL_STORAGE)
        && Utils.contains(grantResults, PackageManager.PERMISSION_GRANTED)) {
            this.pickFromGallery();
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {
            final Uri selectedImage = data.getData();
            if (!Utils.isEmpty(selectedImage)) {
                new JniAsyncTask().execute(selectedImage);
            }
        }
    }


    public static Bitmap bitMapFromImage(Uri uri) {
//            final Bitmap bitmap;
//            try {
//                if (uri.toString().startsWith(ANDROID_RES_PATH)) {
//                    final BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inMutable = true;
//                    bitmap = BitmapFactory.decodeResource(context.getResources(), Integer.parseInt(uri.getLastPathSegment()), options);
//                } else {
//                    FutureTarget<Bitmap> target = Glide.with(context).asBitmap().load(uris[0]).submit(720, 720);
//                    bitmap = target.get();
//                }
//            } catch (final Throwable t) {
//                t.printStackTrace();
//                return null;
//            }
        return null;
    }

    private class JniAsyncTask extends AsyncTask<Uri, Void, Pair<Bitmap, Long>> {
        private @Nullable Toast mToast;

        @Override
        protected void onPreExecute() {
            if (isCancelled() || isDead()) {
                return;
            }

            final Context context = ImageFragment.this.getContext();
            if (context == null) {
                return;
            }

            (this.mToast = Toast.makeText(context, "process..", Toast.LENGTH_LONG)).show();
        }


        @Override
        protected @Nullable Pair<Bitmap, Long> doInBackground(Uri... uris) {
            if (isCancelled() || ImageFragment.this.isDead()) {
                return null;
            }

            final Context context = ImageFragment.this.getContext();
            if (context == null) {
                return null;
            }

            // load mutable bitmap
            final Bitmap bitmap;
            try {
                Uri uri;
                if (Utils.isEmpty(uris) || Utils.isEmpty(uri = uris[0])) {
                    final InputStream is = context.getAssets().open("boy.png");
                    BufferedInputStream bis = null;
                    try {
                        bis = new BufferedInputStream(is);

                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        bitmap = BitmapFactory.decodeStream(bis, null, options);
                        if (bitmap == null) {
                            throw new IOException("Image not loaded");
                        }
                    } finally {
                        Utils.closeSilently(bis);
                        Utils.closeSilently(is);
                    }
                } else {
                    FutureTarget<Bitmap> target = Glide.with(context).asBitmap().load(uri).submit(720, 720);
                    bitmap = target.get();
                }
            } catch (final Throwable t) {
                t.printStackTrace();
                return null;
            }

            Mat outputMap = new Mat();
            long nativeObjAddr = outputMap.getNativeObjAddr();
            ProcessImage(bitmap, nativeObjAddr);

            Bitmap dst_bitmap = Bitmap.createBitmap(512, 700, ARGB_8888 );
            org.opencv.android.Utils.matToBitmap(outputMap, dst_bitmap);

            return Pair.create(dst_bitmap, 0L);
        }

        @Override
        protected void onPostExecute(final @Nullable Pair<Bitmap, Long> pairBitmapTime) {
            if (this.isCancelled() || ImageFragment.this.isDead()) {
                return;
            }

            if (this.mToast != null) {
                this.mToast.cancel();
            }

            if (pairBitmapTime == null || pairBitmapTime.first == null || pairBitmapTime.first.isRecycled()) {
                return;
            }

            if (pairBitmapTime.second != null) {
                Toast.makeText(ImageFragment.this.getContext(), "Processing time: " + pairBitmapTime.second + "ms", Toast.LENGTH_LONG).show();
            }

            mImageView.setImageBitmap(pairBitmapTime.first);
        }
    }

}
