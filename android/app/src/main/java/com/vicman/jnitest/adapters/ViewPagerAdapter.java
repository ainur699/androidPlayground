package com.vicman.jnitest.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.vicman.jnitest.fragments.ImageFragment;
import com.vicman.jnitest.fragments.InfoFragment;
import com.vicman.jnitest.fragments.VideoFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final int INFO_FRAGMENT_POS = 0;
    private static final int IMAGE_FRAGMENT_POS = 1;
    private static final int VIDEO_FRAGMENT_POS = 2;
    private static final int FRAGMENTS_COUNT = 3;

    public ViewPagerAdapter(final @NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return FRAGMENTS_COUNT;
    }

    @Override
    public long getItemId(final int position) {
        if (position >= 0 && position < FRAGMENTS_COUNT) {
            return position;
        }

        throw new IllegalStateException("Invalid cow page position: " + position);
    }

    @Override
    public @NonNull CharSequence getPageTitle(final int position) {
        switch (position) {
            case INFO_FRAGMENT_POS:
                return InfoFragment.PAGE_TITLE;
            case IMAGE_FRAGMENT_POS:
                return ImageFragment.PAGE_TITLE;
            case VIDEO_FRAGMENT_POS:
                return VideoFragment.PAGE_TITLE;
            default:
                throw new IllegalStateException("Invalid cow page position: " + position);
        }
    }

    @Override
    public @NonNull Fragment getItem(final int position) {
        switch (position) {
            case INFO_FRAGMENT_POS:
                return InfoFragment.createInstance();
            case IMAGE_FRAGMENT_POS:
                return ImageFragment.createInstance();
            case VIDEO_FRAGMENT_POS:
                return VideoFragment.createInstance();
            default:
                throw new IllegalStateException("Invalid cow page position: " + position);
        }
    }
}
