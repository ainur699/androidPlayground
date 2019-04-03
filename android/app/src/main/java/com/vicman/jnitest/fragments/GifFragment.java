package com.vicman.jnitest.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.vicman.jnitest.R;
import com.vicman.jnitest.utils.Utils;
import java.io.InputStream;
import butterknife.BindView;
import butterknife.OnClick;

public class GifFragment extends BaseFragment  {
    public static final @NonNull String TAG = Utils.getTag(GifFragment.class);
    public static final @NonNull String PAGE_TITLE = "Gif test";

    static {
        System.loadLibrary("jnitest");
    }
    private native byte[] addTextToGif(byte[] gifFileBytes);

    protected @BindView(R.id.image_view) ImageView mImageView;

    public static @NonNull GifFragment createInstance() {
        return new GifFragment();
    }

    @Override
    public @Nullable View onCreateView(final @NonNull LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gif_fragment, container, false);
    }

    @OnClick(R.id.button_run)
    public void onClick() {
        if (this.isDead()) {
            return;
        }

        new ProcessAsyncTask().execute();
    }

    public void process() {
        new ProcessAsyncTask().execute();
    }

    private class ProcessAsyncTask extends AsyncTask<Void, Void, byte[]> {
        private @Nullable Toast mToast;
        private @Nullable String mErrorMessage;

        @Override
        protected @Nullable byte[] doInBackground(Void... unused) {
            if (isCancelled() || GifFragment.this.isDead()) {
                return null;
            }

            final Context context = GifFragment.this.getContext();
            if (context == null) {
                return null;
            }

            try {
                byte[] sourceGifBuf;
                {
                    final InputStream is = context.getAssets().open("gif_512x512.gif");
                    try {
                        sourceGifBuf = new byte[is.available()];
                        is.read(sourceGifBuf);
                    } finally {
                        Utils.closeSilently(is);
                    }
                }

                final byte[] resultGifBytes = addTextToGif(sourceGifBuf);
                if (resultGifBytes == null || resultGifBytes.length <= 0) {
                    return null;
                }

                return resultGifBytes;
            } catch (final Throwable t) {
                t.printStackTrace();
                this.mErrorMessage = t.getLocalizedMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final @Nullable byte[] gif) {
            if (this.isCancelled() || GifFragment.this.isDead()) {
                return;
            }

            if (this.mToast != null) {
                this.mToast.cancel();
                this.mToast = null;
            }

            //noinspection ConstantConditions
            final @NonNull Context context = GifFragment.this.getContext();

            if (gif == null || gif.length <= 0) {
                Toast.makeText(context, Utils.isEmpty(this.mErrorMessage) ? "Error" : this.mErrorMessage, Toast.LENGTH_LONG).show();
                return;
            }

            Glide.with(context).asGif().load(gif).into(mImageView);
        }
    }

}
