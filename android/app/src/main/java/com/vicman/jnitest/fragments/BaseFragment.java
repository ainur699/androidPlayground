package com.vicman.jnitest.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.vicman.jnitest.utils.Utils;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;

public abstract class BaseFragment extends Fragment {

    private @Nullable Unbinder mButterKnifeUnbinder;

    @Override
    public void onCreate(final @Nullable Bundle savedInstanceState) {
        //noinspection ConstantConditions
        super.onCreate(Utils.setClassLoader(this.getContext(), this.getArguments(), savedInstanceState));

        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (this.mButterKnifeUnbinder != null) {
            this.mButterKnifeUnbinder.unbind();
        }
        this.mButterKnifeUnbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onSaveInstanceState(final @NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onDestroyView() {
        if (this.mButterKnifeUnbinder != null) {
            this.mButterKnifeUnbinder.unbind();
            this.mButterKnifeUnbinder = null;
        }

        super.onDestroyView();
    }

    public boolean isDead() {
        return Utils.isDead(this);
    }

    @Override
    @Deprecated
    public void setHasOptionsMenu(boolean hasMenu) {
        super.setHasOptionsMenu(hasMenu);
    }

    @Override
    @Deprecated
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    @Override
    @Deprecated
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    @Deprecated
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    @Deprecated
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
    }

    @Override
    @Deprecated
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    @Deprecated
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

}
