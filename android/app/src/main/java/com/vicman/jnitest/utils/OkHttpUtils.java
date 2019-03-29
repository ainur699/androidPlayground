package com.vicman.jnitest.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.moczul.ok2curl.CurlInterceptor;
import com.moczul.ok2curl.logger.Loggable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtils {

    private volatile static OkHttpClient sClient;

    public static @NonNull OkHttpClient getClient() {
        OkHttpClient instance = sClient;
        if (instance == null) {
            synchronized (OkHttpUtils.class) {
                instance = sClient;
                if (instance == null) {
                    sClient = instance = buildOkHttpClient();
                }
            }
        }
        return instance;
    }

    private static @NonNull OkHttpClient buildOkHttpClient() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);

        builder.addInterceptor(UserAgentInterceptor.getDefaultInstance());

        builder.addInterceptor(new CurlInterceptor(new Loggable() {
            @Override
            public void log(String message) {
                Log.d("Ok2Curl", message);
            }
        }));

        return builder.build();
    }

    public static class UserAgentInterceptor implements Interceptor {
        private volatile static UserAgentInterceptor sInstance;
        private final String mUserAgent;

        public static @NonNull UserAgentInterceptor getDefaultInstance() {
            UserAgentInterceptor instance = sInstance;
            if (instance == null) {
                synchronized (UserAgentInterceptor.class) {
                    instance = sInstance;
                    if (instance == null) {
                        sInstance = instance = new UserAgentInterceptor();
                    }
                }
            }
            return instance;
        }

        private UserAgentInterceptor() {
            this(Utils.getClientPlatformId());
        }

        @SuppressWarnings("WeakerAccess")
        public UserAgentInterceptor(final @NonNull String userAgent) {
            this.mUserAgent = userAgent;
        }

        @Override
        public Response intercept(final Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .header("User-Agent", mUserAgent)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }

}
