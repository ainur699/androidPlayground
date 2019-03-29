package com.vicman.jnitest.activities;

import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import com.vicman.jnitest.utils.Utils;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mButterKnifeUnbinder;

    @Override
    protected void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(Utils.setClassLoader(this, this.getIntent(), savedInstanceState));

        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    public boolean isDead() {
        return Utils.isDead(this);
    }

    @Override
    public void setContentView(final int layoutResID) {
        super.setContentView(layoutResID);
        this.bindButterKnife();
    }

    @Override
    public void setContentView(final View view) {
        super.setContentView(view);
        this.bindButterKnife();
    }

    @Override
    public void setContentView(final View view, final ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        this.bindButterKnife();
    }

    @Override
    public void onSaveInstanceState(final @NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        this.unbindButterKnife();

        super.onDestroy();
    }

    @Override
    public boolean isDestroyed() {
        return this.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED;
    }

    private void bindButterKnife() {
        this.unbindButterKnife();
        this.mButterKnifeUnbinder = ButterKnife.bind(this);
    }

    private void unbindButterKnife() {
        if (this.mButterKnifeUnbinder != null) {
            this.mButterKnifeUnbinder.unbind();
            this.mButterKnifeUnbinder = null;
        }
    }

}
