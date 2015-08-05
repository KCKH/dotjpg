/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Date;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.android.IntentManager;
import tm.alashow.dotjpg.model.Image;
import tm.alashow.dotjpg.util.DateUtil;
import tm.alashow.dotjpg.util.DotjpgUtils;
import tm.alashow.dotjpg.util.U;

/**
 * Created by alashov on 31/07/15.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {

    private Context mContext;
    private ArrayList<Image> images = new ArrayList<>();
    private int screenWidth;

    public ImagesAdapter(Context context, ArrayList<Image> images) {
        if (context != null) this.mContext = context;
        this.images = images;
        screenWidth = U.getScreenWidth(mContext);
    }

    @Override
    public ImagesAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImagesAdapter.ImageViewHolder holder, int position) {
        final Image image = images.get(position);

        DotjpgUtils.loadImage(holder.imageView, image.getImageUrl());
        float scaleFactor = ((float) screenWidth / image.getWidth());
        int calculatedHeight = (int) (image.getHeight() * scaleFactor);
        holder.imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, calculatedHeight));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentManager.with(mContext).openImageDetails(image.getImageFilename(), image);
            }
        });

        holder.nameView.setText(image.getImageId());
        holder.dateView.setText(DateUtil.getTimeAgo(new Date(image.getTimestamp())));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image) ImageView imageView;
        @Bind(R.id.name) TextView nameView;
        @Bind(R.id.date) TextView dateView;

        ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}