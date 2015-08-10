/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.android.IntentManager;
import tm.alashow.dotjpg.android.PreferencesManager;
import tm.alashow.dotjpg.interfaces.OnTitleClickedListener;
import tm.alashow.dotjpg.ui.fragment.BaseFragment;
import tm.alashow.dotjpg.util.U;

/**
 * Created by alashov on 30/07/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private Handler mHandler;

    private boolean shouldGoInvisible;
    private String mOldTitle;
    private String mOldSubtitle;

    public Toolbar mToolbar;
    public PreferencesManager preferencesManager;
    public HashSet<OnTitleClickedListener> onTitleClickedListeners = new HashSet<>();

    @Bind(R.id.drawer) DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);

        preferencesManager = PreferencesManager.getInstance(this);
        mHandler = new Handler();

        initToolbar();
        initAddImageButton();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for(int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(! shouldGoInvisible);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        //Close MenuDrawer if opened
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    private void initToolbar() {
        mToolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View arg0) {
                shouldGoInvisible = false;
                if (mOldTitle != null) {
                    mToolbar.setTitle(mOldTitle);
                }
                if (mOldSubtitle != null) {
                    mToolbar.setSubtitle(mOldSubtitle);
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View arg0) {
                shouldGoInvisible = true;
                mOldTitle = mToolbar.getTitle().toString();
                mOldSubtitle = (mToolbar.getSubtitle() != null) ? mToolbar.getSubtitle().toString() : null;
                mToolbar.setTitle(R.string.app_name);
                mToolbar.setSubtitle(null);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(! isChildActivity());
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawers();
                } else {
                    onBackPressed();
                }
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark_material_dark));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onNavigationClicked(menuItem);
                    }
                }, 100);

                return true;
            }
        });

        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(OnTitleClickedListener onTitleClickedListener : onTitleClickedListeners)
                    onTitleClickedListener.scrollUp();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initAddImageButton() {
        View addImageView = ButterKnife.findById(this, getAddImageButtonId());
        if (addImageView != null) { //maybe custom activity? or you are wizard?
            if (isAddImageButtonEnabled()) {
                addImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentManager.with(getActivity()).newImage();
                    }
                });
            } else {
                U.hideView(addImageView);
            }
        }
    }

    /**
     * Starts activity if activity tag handled, else close menu
     *
     * @param menuItem clicked menuItem
     */
    private void onNavigationClicked(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main:
                if (! getActivityTag().equals(Config.ACTIVITY_TAG_MAIN)) {
                    IntentManager.with(this).openMain();
                }
                break;
            case R.id.my:
                if (! getActivityTag().equals(Config.ACTIVITY_TAG_IMAGES)) {
                    IntentManager.with(this).openMyImages();
                }
                break;
            case R.id.addImage:
                if (! getActivityTag().equals(Config.ACTIVITY_TAG_IMAGE_NEW)) {
                    IntentManager.with(getActivity()).newImage();
                }
                break;
            case R.id.preferences:
                if (! getActivityTag().equals(Config.ACTIVITY_TAG_PREFERENCES)) {
                    IntentManager.with(this).openPreferences();
                }
                break;
        }
    }

    private void setNewRootFragment(BaseFragment fragment) {
        U.attachFragment(this, fragment);
        mDrawerLayout.closeDrawers();
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.hide();
    }

    private void showActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.show();
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }


    protected AppCompatActivity getActivity() {
        return this;
    }

    /**
     * Call when activity childness changed
     */
    protected void onActivityStatusChanged() {
        try {
            mDrawerToggle.setDrawerIndicatorEnabled(! isChildActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Layout resource for activity content.
     *
     * @return int, layout resource
     */
    protected abstract
    @LayoutRes
    int getLayoutResourceId();

    /**
     * If true sets action bar title click callback finish activity,
     * else open menu drawer.
     *
     * @return boolean is child or main activity
     */
    protected abstract Boolean isChildActivity();

    /**
     * Tag of activity. For not opening same activity
     * twice after click in menu drawer.
     *
     * @return String activity tag
     */
    protected abstract String getActivityTag();

    /**
     * Show or hide add image fab button
     *
     * @return show or hide
     */
    protected boolean isAddImageButtonEnabled() {
        return false;
    }

    /**
     * Id of add image fab button, override if have custom id
     *
     * @return view id
     */
    protected int getAddImageButtonId() {
        return R.id.addImage;
    }
}
