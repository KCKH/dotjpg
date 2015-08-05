/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.ui.view.HackyViewPager;
import tm.alashow.dotjpg.util.U;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ViewImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_view_image);

        //Hiding System UI
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
                actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_transculate));
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        ArrayList<String> imageList = getIntent().getStringArrayListExtra(Config.EXTRA_URLS);
        int position = getIntent().getIntExtra(Config.EXTRA_POSITION, 0);

        HackyViewPager pager = (HackyViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new ImagesFragmentAdapter(getSupportFragmentManager(), imageList));
        pager.setCurrentItem(position);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public static class ViewImageFragment extends Fragment {
        private PhotoViewAttacher mAttacher;
        private String mImageUrl;

        @Bind(R.id.image) ImageView mImageView;
        @Bind(R.id.progress) ProgressWheel progressBar;
        @Bind(R.id.retry) Button retryButton;

        public static ViewImageFragment newInstance(String url) {
            ViewImageFragment fragment = new ViewImageFragment();
            Bundle args = new Bundle();
            args.putString(Config.EXTRA_URL, url);
            fragment.setArguments(args);
            fragment.setHasOptionsMenu(true);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_view_image, container, false);
            ButterKnife.bind(this, view);

            mImageUrl = getArguments() != null ? getArguments().getString(Config.EXTRA_URL) : "";
            retryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImage();
                }
            });

            showImage();
            return view;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.view_image_activity, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    getActivity().finish();
                    return true;
                case R.id.download:
                    if (mImageUrl != null && ! mImageUrl.equals("")) {
                        U.downloadImage(getActivity(), mImageUrl);
                    }
                    return true;
                case R.id.copy:
                    if (mImageUrl != null && ! mImageUrl.equals("")) {
                        U.copyToClipboard(getActivity(), mImageUrl);
                        U.showCenteredToast(getActivity(), R.string.image_link_copied);
                    }
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

        /**
         * Downloading image and setting calback
         */
        private void showImage() {
            U.hideView(retryButton);
            U.showView(progressBar);

            Picasso.with(getActivity()).load(mImageUrl).into(mImageView, new Callback() {
                @Override
                public void onSuccess() {
                    //Attaching or updating photo view on success downloading image
                    if (mAttacher != null) {
                        mAttacher.update();
                    } else {
                        if (getActivity() != null) {
                            final ActionBar actionBar = ((ViewImageActivity) getActivity()).getSupportActionBar();

                            mAttacher = new PhotoViewAttacher(mImageView);
                            if (actionBar != null) {
                                actionBar.hide();
                            }

                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                                mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                                    @Override
                                    public void onViewTap(View view, float x, float y) {
                                        if (actionBar != null) {
                                            if (actionBar.isShowing()) {
                                                actionBar.hide();
                                            } else {
                                                actionBar.show();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                    U.hideView(progressBar);
                    U.hideView(retryButton);
                }

                @Override
                public void onError() {
                    U.showNetworkError(retryButton);
                    U.hideView(progressBar);
                    U.showView(retryButton);
                }
            });
        }
    }

    public class ImagesFragmentAdapter extends FragmentPagerAdapter {
        private ArrayList<String> imagesList;

        public ImagesFragmentAdapter(FragmentManager fragmentManager, ArrayList<String> imageslist) {
            super(fragmentManager);
            this.imagesList = imageslist;
        }

        @Override
        public Fragment getItem(int i) {
            return ViewImageFragment.newInstance(imagesList.get(i));
        }

        @Override
        public int getCount() {
            return imagesList.size();
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {

        public void transformPage(View view, float position) {
            view.setTranslationX(position < 0 ? 0f : - view.getWidth() * position);
        }
    }
}