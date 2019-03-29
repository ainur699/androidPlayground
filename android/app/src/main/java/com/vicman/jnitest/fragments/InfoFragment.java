package com.vicman.jnitest.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.vicman.jnitest.R;
import com.vicman.jnitest.utils.Utils;
import org.opencv.android.OpenCVLoader;
import butterknife.BindView;
import butterknife.OnClick;

public class InfoFragment extends BaseFragment {
    @SuppressWarnings("unused")
    public static final @NonNull String TAG = Utils.getTag(InfoFragment.class);
    public static final @NonNull String PAGE_TITLE = "Device Info";
    private static final int COLOR_DARK_GREEN = Color.rgb(0x00, 0x88, 0x00);
    private static final int COLOR_DARK_RED = Color.rgb(0x88, 0x00, 0x00);

    static {
        System.loadLibrary("jnitest");
    }
    private native String testJniCall();

    protected @BindView(R.id.text_view) TextView mTextView;

    public static @NonNull InfoFragment createInstance() {
        return new InfoFragment();
    }

    @Override
    public @Nullable View onCreateView(final @NonNull LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.info_fragment, container, false);
    }

    @Override
    public void onViewCreated(final @NonNull View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final StringBuilder sb = new StringBuilder();
        sb.append("Device: ").append(Build.MODEL.startsWith(Build.MANUFACTURER) ? Build.MODEL : (Build.MANUFACTURER + ' ' + Build.MODEL));
        sb.append("\nAndroid ").append(Build.VERSION.RELEASE).append(" (api lvl ").append(Build.VERSION.SDK_INT).append(")");

        final @ColorInt int openCvSpanColor;
        final int openCvSpanStart, openCvSpanEnd;
        sb.append("\n\nOpenCV v3.4.3 ");
        if (OpenCVLoader.initDebug()) {
            openCvSpanColor = COLOR_DARK_GREEN;
            openCvSpanStart = sb.length();
            sb.append("loaded");
            openCvSpanEnd = sb.length();
        } else {
            openCvSpanColor = COLOR_DARK_RED;
            openCvSpanStart = sb.length();
            sb.append("not");
            openCvSpanEnd = sb.length();
            sb.append(" loaded!");
        }

        final @ColorInt int openGlSpanColor;
        final int openGlSpanStart, openGlSpanEnd;
        sb.append("\n\nOpenGL ");
        final ActivityManager activityManager = (ActivityManager) this.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        openGlSpanColor = configurationInfo.reqGlEsVersion >= 0x30000 ? COLOR_DARK_GREEN : COLOR_DARK_RED;
        openGlSpanStart = sb.length();
        sb.append(configurationInfo.reqGlEsVersion>>16).append('.').append(configurationInfo.reqGlEsVersion & 0xFFFF);
        openGlSpanEnd = sb.length();

        final SpannableString text = new SpannableString(sb);
        text.setSpan(new ForegroundColorSpan(openCvSpanColor), openCvSpanStart, openCvSpanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new ForegroundColorSpan(openGlSpanColor), openGlSpanStart, openGlSpanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        this.mTextView.setText(text);
    }

    @OnClick(R.id.button_test)
    protected void testJni() {
        if (this.isDead()) {
            return;
        }

        final String jniText = this.testJniCall();
        Toast.makeText(this.getContext(), jniText, Toast.LENGTH_LONG).show();
    }
}
