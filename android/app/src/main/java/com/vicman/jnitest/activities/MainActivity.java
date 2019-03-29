package com.vicman.jnitest.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.vicman.jnitest.R;
import com.vicman.jnitest.adapters.ViewPagerAdapter;
import com.vicman.jnitest.utils.Utils;
import butterknife.BindView;

public class MainActivity extends BaseActivity {
    public static final @NonNull String TAG = Utils.getTag(MainActivity.class);
    public static final @NonNull String PREFS_LAST_ACTIVE_PAGE_POSITION = "last_active_page_position";
    public static final int NO_LAST_ACTIVE_PAGE_POSITION = -1;

    protected @BindView(R.id.toolbar) Toolbar mToolbar;
    protected @BindView(R.id.view_pager) ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);

        this.setSupportActionBar(this.mToolbar);
        this.setTitle("Photo Lab " + this.getString(R.string.app_name));

        this.mViewPager.setAdapter(new ViewPagerAdapter(this.getSupportFragmentManager()));
        new LastPageRestorerAsyncTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        final int activePosition = this.mViewPager.getCurrentItem();
        this.getSharedPreferences(MainActivity.TAG, MODE_PRIVATE).edit().putInt(PREFS_LAST_ACTIVE_PAGE_POSITION, activePosition).apply();

        super.onPause();
    }

    private class LastPageRestorerAsyncTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected @Nullable Integer doInBackground(Void... voids) {
            if (isCancelled() || isDead()) {
                return null;
            }

            return getSharedPreferences(MainActivity.TAG, MODE_PRIVATE).getInt(PREFS_LAST_ACTIVE_PAGE_POSITION, NO_LAST_ACTIVE_PAGE_POSITION);
        }

        @Override
        protected void onPostExecute(final @Nullable Integer integer) {
            if (isCancelled() || isDead() || integer == null || integer < 0) {
                return;
            }

            final PagerAdapter pagerAdapter = mViewPager.getAdapter();
            if (pagerAdapter != null && integer < pagerAdapter.getCount()) {
                mViewPager.setCurrentItem(integer);
            }
        }
    }

}
