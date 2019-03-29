package com.vicman.jnitest.fragments;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Toast;
import android.widget.VideoView;
import com.bumptech.glide.load.HttpException;
import com.vicman.jnitest.R;
import com.vicman.jnitest.retrofit.Api;
import com.vicman.jnitest.retrofit.ProcessQueueResult;
import com.vicman.jnitest.retrofit.ProcessResult;
import com.vicman.jnitest.utils.OkHttpUtils;
import com.vicman.jnitest.utils.Utils;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class VideoFragment extends BaseFragment  {
    public static final @NonNull String TAG = Utils.getTag(VideoFragment.class);
    public static final @NonNull String PAGE_TITLE = "Video test";

    static {
        System.loadLibrary("jnitest");
    }
    private native byte[] getVideo(byte[] binFileBytes, Bitmap image);

    protected @BindView(R.id.video_view) VideoView mVideoView;

    public static @NonNull VideoFragment createInstance() {
        return new VideoFragment();
    }

    @Override
    public @Nullable View onCreateView(final @NonNull LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.video_fragment, container, false);
    }

    @Override
    public void onViewCreated(final @NonNull View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (this.mVideoView != null) {
            this.mVideoView.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (this.mVideoView != null && this.mVideoView.isPlaying()) {
            this.mVideoView.stopPlayback();
        }
    }

    @OnClick(R.id.button_run)
    public void onClick() {
        if (this.isDead()) {
            return;
        }

        final @NonNull String templateName = "2401";
        this.process(templateName);

        Toast.makeText(this.getContext(), "Generated animation", Toast.LENGTH_SHORT).show();
    }

    public void process(final @NonNull String templateName) {
        new ProcessAsyncTask().execute(templateName);
    }

    private class ProcessAsyncTask extends AsyncTask<String, Void, File> {
        private final @NonNull String QUEUE_BASE = "https://opeapi.ws.pho.to/";
        private static final @NonNull String APP_ID = "D21AB35BC5BEC7C6BC48797E33E3";
        private static final @NonNull String KEY_1 = "35F742B780489230278A4E2C27AA";
        private final String[] hexArray = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0a", "0b", "0c", "0d", "0e", "0f", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1a", "1b", "1c", "1d", "1e", "1f", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2a", "2b", "2c", "2d", "2e", "2f", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3a", "3b", "3c", "3d", "3e", "3f", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4a", "4b", "4c", "4d", "4e", "4f", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "5a", "5b", "5c", "5d", "5e", "5f", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6a", "6b", "6c", "6d", "6e", "6f", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "7a", "7b", "7c", "7d", "7e", "7f", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8a", "8b", "8c", "8d", "8e", "8f", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9a", "9b", "9c", "9d", "9e", "9f", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "aa", "ab", "ac", "ad", "ae", "af", "b0", "b1", "b2", "b3", "b4", "b5", "b6", "b7", "b8", "b9", "ba", "bb", "bc", "bd", "be", "bf", "c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "ca", "cb", "cc", "cd", "ce", "cf", "d0", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9", "da", "db", "dc", "dd", "de", "df", "e0", "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8", "e9", "ea", "eb", "ec", "ed", "ee", "ef", "f0", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "fa", "fb", "fc", "fd", "fe", "ff"};
        private final static @NonNull String BIN_REQUEST = "<image_process_call>" +
            "<image_url order=\"1\">%s</image_url>" +
            "<methods_list>" +
            "<method>" +
            "<name>face_reconstruct_data</name>" +
            "<params>template_name=%s;</params>" +
            "</method>" +
            "</methods_list>" +
            "</image_process_call>";

        private @Nullable Toast mToast;
        private @Nullable String mErrorMessage;
        private @Nullable Api mApi;

        @Override
        protected void onPreExecute() {
            if (isCancelled() || isDead()) {
                return;
            }

            final Context context = VideoFragment.this.getContext();
            if (context == null) {
                return;
            }

            (this.mToast = Toast.makeText(context, "process..", Toast.LENGTH_LONG)).show();
        }

        @Override
        protected @Nullable File doInBackground(String... templateName) {
            if (isCancelled() || VideoFragment.this.isDead()) {
                return null;
            }

            final Context context = VideoFragment.this.getContext();
            if (context == null) {
                return null;
            }

            try {
                final byte[] bin;
                final Bitmap bitmap;
                {
                    byte[] buf;
                    {
                        final InputStream is = context.getAssets().open("boy.jpg");
                        try {
                            buf = new byte[is.available()];
                            is.read(buf);
                        } finally {
                            Utils.closeSilently(is);
                        }
                    }

                    bin = this.getBin(context, templateName[0], buf);

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    bitmap = BitmapFactory.decodeByteArray(buf, 0, buf.length, options);
                }

                final byte[] videoBytes = getVideo(bin, bitmap);
                if (videoBytes == null || videoBytes.length <= 0) {
                    return null;
                }

                final String videoFilePath = Utils.getDiskCacheDir(context, "video.mp4");
                final File videoFile = new File(videoFilePath);
                final FileOutputStream fos = new FileOutputStream(videoFile);
                try {
                    fos.write(videoBytes);
                    fos.flush();
                } finally {
                    Utils.closeSilently(fos);
                }

                return videoFile;
            } catch (final Throwable t) {
                t.printStackTrace();
                this.mErrorMessage = t.getLocalizedMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final @Nullable File videoFile) {
            if (this.isCancelled() || VideoFragment.this.isDead()) {
                return;
            }

            if (this.mToast != null) {
                this.mToast.cancel();
                this.mToast = null;
            }

            if (videoFile == null) {
                Toast.makeText(VideoFragment.this.getContext(), Utils.isEmpty(this.mErrorMessage) ? "Error" : this.mErrorMessage, Toast.LENGTH_LONG).show();
                return;
            }

            mVideoView.setVideoPath(videoFile.getAbsolutePath());
        }

        @WorkerThread
        private @Nullable byte[] getBin(final @NonNull Context context, final @NonNull String templteName, final @NonNull byte[] imageBytes) throws IOException, InterruptedException, ExecutionException {
            final @NonNull String url = upload(imageBytes);

            final String requestXmlData = String.format(Locale.US, BIN_REQUEST, url, templteName);
            final @NonNull String requestId = this.pushToQueue(context, requestXmlData);

            String resultUrl;
            do {
                Thread.sleep(1000);
                resultUrl = this.getResultUri(context, requestId);
            } while (resultUrl == null);

            return downloadBin(context, resultUrl);
        }

        @WorkerThread
        private @NonNull String upload(final @NonNull byte[] imageBytes) throws IOException {
            Response response = null;
            ResponseBody responseBody = null;
            try {
                final RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("no_resize", "1")
                        .addFormDataPart("image", "cache.jpeg", RequestBody.create(MediaType.parse("image/"), imageBytes))
                        .build();

                final String baseUrl = "https://temp.ws.pho.to/upload.php";
                final Request request = new Request.Builder()
                        .url(baseUrl)
                        .post(body)
                        .build();

                response = OkHttpUtils.getClient().newCall(request).execute();

                responseBody = response.body();
                String responseBodyString = responseBody == null ? null : responseBody.string();
                if (!response.isSuccessful()) {
                    throw new IOException("Http error: " + response.code() + " " + responseBodyString);
                }

                if (Utils.isEmpty(responseBodyString) || !URLUtil.isValidUrl(responseBodyString)) {
                    throw new IOException("Invalid remote url: " + responseBodyString);
                }

                return responseBodyString;
            } finally {
                Utils.closeSilently(responseBody);
                Utils.closeSilently(response);
            }
        }

        @WorkerThread
        private @NonNull String pushToQueue(final @NonNull Context context, final @NonNull String dataXML) throws IOException {
            final String signData = hmacSha1(dataXML, KEY_1);

            final retrofit2.Response<ProcessQueueResult> response = this.getApi()
                    .pushToQueue(APP_ID, dataXML, signData, generateDigest(context))
                    .execute();

            if (!response.isSuccessful()) {
                throw new IOException(response.code() + " " + response.message());
            }

            final ProcessQueueResult queueResult = response.body();
            ProcessQueueResult.throwException(context, queueResult);
            return queueResult.getRequestId();
        }

        @WorkerThread
        public @Nullable String getResultUri(final @NonNull Context context, final @NonNull String requestId) throws IOException {
            final Call<ProcessResult> call = this.getApi().getResultUri(requestId);
            final retrofit2.Response<ProcessResult> response = call.execute();

            if (!response.isSuccessful()) {
                throw new IOException(response.code() + " " + response.message());
            }

            final ProcessResult processResult = response.body();

            if (processResult != null && ProcessResult.STATUS_IN_PROGRESS.equals(processResult.getStatus())) {
                return null;
            }

            ProcessResult.throwException(context, processResult);

            return processResult.getResultUrl();
        }

        @WorkerThread
        private @NonNull byte[] downloadBin(final @NonNull Context context, final @NonNull String resultUrl) throws IOException {
            okhttp3.Response response = null;
            ResponseBody responseBody = null;
            try {
                final Request request = new Request.Builder().url(resultUrl).build();
                response = OkHttpUtils.getClient().newCall(request).execute();

                final int responseCode = response.code();
                if (!response.isSuccessful()) {
                    throw new HttpException(response.message(), responseCode);
                }

                responseBody = response.body();
                final byte[] bytes = responseBody.bytes();
                if (bytes == null || bytes.length <= 0) {
                    throw new IllegalStateException("Empty bin file");
                }
                return bytes;
            } finally {
                Utils.closeSilently(responseBody);
                Utils.closeSilently(response);
            }
        }

        private @NonNull Api getApi() {
            if (this.mApi != null) {
                return this.mApi;
            }

            this.mApi = new Retrofit.Builder()
                    .baseUrl(QUEUE_BASE)
                    .client(OkHttpUtils.getClient())
                    .addConverterFactory(SimpleXmlConverterFactory.create(new Persister(new AnnotationStrategy())))
                    .build()
                    .create(Api.class);

            return this.mApi;
        }

        private String hmacSha1(String value, String key) {
            if (TextUtils.isEmpty(value)) throw new IllegalArgumentException("value");

            if (TextUtils.isEmpty(key)) throw new IllegalArgumentException("key");

            byte[] keyBytes = key.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
            try {
                Mac mac = Mac.getInstance("HmacSHA1");
                mac.init(signingKey);
                byte[] rawHmac = mac.doFinal(value.getBytes());

                StringBuilder sb = new StringBuilder(rawHmac.length * 2);
                for (byte b : rawHmac) {
                    sb.append(hexArray[0xFF & b]);
                }

                return sb.toString();
            } catch (java.security.GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private@NonNull String generateDigest(final @NonNull Context context) {
            String digest = "";
            try {
                final String getPM = new String(Base64.decode("Z2V0UGFja2FnZU1hbmFnZXI=", Base64.DEFAULT));
                final String getPI = new String(Base64.decode("Z2V0UGFja2FnZUluZm8=", Base64.DEFAULT));
                final Method gpmMethod = context.getClass().getMethod(getPM);
                final Object gpmObject = gpmMethod.invoke(context);
                final Method gpiMethod = gpmObject.getClass().getMethod(getPI, String.class, int.class);
                final Object pi = gpiMethod.invoke(gpmObject, context.getPackageName(), PackageManager.GET_SIGNATURES);
                final Signature sig2 = ((PackageInfo) pi).signatures[0];

                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(sig2.toByteArray());
                digest = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return digest;
        }

    }

    /* OLD */

    private @Nullable byte[] bytesFromResource(int id) {
        InputStream is = null;
        BufferedInputStream buf = null;
        try {
            is = getResources().openRawResource(id);
            buf = new BufferedInputStream(is);

            final int length = buf.available();
            final byte[] bytes = new byte[length];
            buf.read(bytes, 0, length);

            return bytes;
        } catch (final Throwable t) {
            t.printStackTrace();
        } finally {
            Utils.closeSilently(buf);
        }

        return null;
    }
}
