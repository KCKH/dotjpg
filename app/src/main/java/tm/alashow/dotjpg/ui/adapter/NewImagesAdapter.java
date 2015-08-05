/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.model.NewImage;
import tm.alashow.dotjpg.util.DotjpgUtils;
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

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_new_image, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DotjpgUtils.loadImage(viewHolder.imageView, Uri.fromFile(newImage.getFile()), 150, 150);

        String fileSize = U.humanReadableByteCount(newImage.getSize(), true);
        String fileType = U.getImageType(newImage.getFile());

        viewHolder.sizeView.setText(String.format(context.getString(R.string.image_new_size), fileSize));
        viewHolder.typeView.setText(String.format(context.getString(R.string.image_new_type), fileType));

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setDataAndType(Uri.fromFile(newImage.getFile()), "image/*");
                intent.setAction(Intent.ACTION_VIEW);
                context.startActivity(intent);
            }
        });

        viewHolder.compressView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

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
    }
}
