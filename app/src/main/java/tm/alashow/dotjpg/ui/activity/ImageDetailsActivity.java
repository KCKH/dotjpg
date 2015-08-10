/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.android.IntentManager;
import tm.alashow.dotjpg.model.Image;
import tm.alashow.dotjpg.util.DateUtil;
import tm.alashow.dotjpg.util.DotjpgUtils;
import tm.alashow.dotjpg.util.U;

/**
 * Created by alashov on 01/08/15.
 */
public class ImageDetailsActivity extends BaseActivity {

    private String mImageFilename;
    private Image mImage;

    @Bind(R.id.wrapper) ViewGroup wrapperView;
    @Bind(R.id.imageView) ImageView imageView;
    @Bind(R.id.download) TextView downloadView;
    @Bind(R.id.date) TextView dateView;
    @Bind(R.id.share) View shareView;
    @Bind(R.id.collapsingToolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mCoordinatorLayout.setStatusBarBackgroundColor(getResources().getColor(android.R.color.transparent));

        init(getIntent());
    }

    private void init(Intent intent) {
        Bundle data = intent.getExtras();
        if (data != null) {
            mImageFilename = data.getString(Config.EXTRA_IMAGE_FILENAME);
            mImage = (Image) intent.getSerializableExtra(Config.EXTRA_IMAGE);
            if (mImage != null || U.getTrimmedString(mImageFilename).length() > 0) {
                initViews();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void initViews() {
        mCollapsingToolbar.setTitle(DotjpgUtils.trimImageExtension(mImageFilename));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentManager.with(getActivity()).openImage(getImageUrl());
            }
        });

        DotjpgUtils.loadImage(imageView, Uri.parse(getImageUrl()));

        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, getImageUrl());
                intent.setType("text/plain");
                startActivity(intent);
            }
        });

        downloadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                U.downloadImage(getActivity(), getImageUrl(), v);
            }
        });

        if (imageObject()) {
            dateView.setText(DateUtil.getTimeAgo(new Date(mImage.getTimestamp())));
        }

        //Adding info views
        addInfoView(R.string.image_direct, getImageUrl(), true);
        if (imageObject()) {
            if (mImage.hasDeleteToken()) {
                addInfoView(R.string.image_delete, DotjpgUtils.getImageDeleteUrl(mImage.getDeleteToken()), true);
            }
            if (mImage.hasGalleryId()) {
                addInfoView(R.string.image_gallery, DotjpgUtils.getImageGalleryUrl(mImage.getGalleryId()), true);
            }
        }

        addInfoView(R.string.image_page, DotjpgUtils.getImagePageUrl(DotjpgUtils.trimImageExtension(mImageFilename)), true);
        addInfoView(R.string.image_html, DotjpgUtils.getImageHtmlMarkup(mImageFilename), false);
        addInfoView(R.string.image_markdown, DotjpgUtils.getImageMarkdownMarkup(mImageFilename), false);
        addInfoView(R.string.image_bb, DotjpgUtils.getImageBbMarkup(mImageFilename), false);
    }

    private void addInfoView(@StringRes int label, final String text, boolean isLink) {
        addInfoView(getString(label), text, isLink);
    }

    private void addInfoView(String label, final String text, boolean isLink) {
        View view = LayoutInflater.from(this).inflate(R.layout.view_image_info, null);

        TextView labelView = ButterKnife.findById(view, R.id.label);
        final TextView textView = ButterKnife.findById(view, R.id.text);

        //Set Text
        labelView.setText(label);
        textView.setText(text);

        if (isLink) {
            labelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
                    startActivity(intent);
                }
            });
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                U.copyToClipboard(getActivity(), textView.getText().toString());
                U.showSnack(v, R.string.image_link_copied, U.SNACK_DEFAULT);
            }
        });

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                U.showCenteredToast(getActivity(), R.string.image_link_copy);
                return true;
            }
        });

        //Add to ui
        wrapperView.addView(view);
    }

    private String getImageUrl() {
        return (mImage != null) ? mImage.getImageUrl() : DotjpgUtils.getImageUrl(mImageFilename);
    }

    /**
     * @return is image object available
     */
    private boolean imageObject() {
        return mImage != null;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_image_details;
    }

    @Override
    protected Boolean isChildActivity() {
        return true;
    }

    @Override
    protected String getActivityTag() {
        return Config.ACTIVITY_TAG_IMAGE_DETAILS;
    }
}
