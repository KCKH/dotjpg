/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.android.IntentManager;
import tm.alashow.dotjpg.model.NewImage;
import tm.alashow.dotjpg.util.DotjpgUtils;
import tm.alashow.dotjpg.util.ImageCompress;
import tm.alashow.dotjpg.util.U;

/**
 * Created by alashov on 31/07/15.
 */
public class NewImagesAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<NewImage> images = new ArrayList<>();

    public NewImagesAdapter(Context context, ArrayList<NewImage> images) {
        this.images = images;
        if (context != null) {
            this.context = context;
            try {
                this.inflater = LayoutInflater.from(this.context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public NewImage getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final NewImage newImage = images.get(position);

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_new_image, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setViews(context, newImage.getFile());

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open image with system image viewer
                IntentManager.with(context).openLocalImage(newImage.getFile());
            }
        });

        //hide compress checkbox when file under 100kb or if gif. Don't check actual type by reading bytes
        if (newImage.getOriginFile().length() < Config.IMAGE_COMPRESS_MIN_BYTE || newImage.getOriginFile().getAbsolutePath().endsWith(".gif")) {
            U.hideView(viewHolder.compressView);
        } else {
            U.showView(viewHolder.compressView);
        }

        viewHolder.compressView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //we have old compressed file, don't compress file again
                    if (newImage.isCheckedCompressed()) {
                        newImage.setCompressed(true);
                        viewHolder.setViews(context, newImage.getFile());
                        return;
                    }

                    //generate new file name then execute compress task
                    final File compressedFile = U.generateCompressFilePath();
                    new ImageCompress(context, new ImageCompress.OnImageCompressListener() {
                        @Override
                        public void onSuccess(File output) {
                            newImage.setCompressedFile(compressedFile);
                            newImage.setCompressed(true);
                            viewHolder.setViews(context, newImage.getFile());
                        }

                        @Override
                        public void onError() {
                            U.showUnknownError(viewHolder.imageView);
                        }
                    }, compressedFile).execute(newImage.getOriginFile());
                } else {
                    newImage.setCompressed(false);
                    viewHolder.setViews(context, newImage.getFile());
                }
            }
        });

        //is that checked
        viewHolder.compressView.setChecked(newImage.isCheckedCompressed());

        return convertView;
    }

    public static class ViewHolder {
        @Bind(R.id.image) ImageView imageView;
        @Bind(R.id.size) TextView sizeView;
        @Bind(R.id.type) TextView typeView;
        @Bind(R.id.compress) CheckBox compressView;

        ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

        /**
         * Fill some views with data
         *
         * @param context context
         * @param file    image to show about
         */
        public void setViews(Context context, File file) {
            DotjpgUtils.loadImage(imageView, Uri.fromFile(file));

            String fileSize = U.humanReadableByteCount(file.length(), true);
            String fileType = U.getImageType(file);

            sizeView.setText(String.format(context.getString(R.string.image_new_size), fileSize));
            typeView.setText(String.format(context.getString(R.string.image_new_type), fileType));
        }
    }
}
