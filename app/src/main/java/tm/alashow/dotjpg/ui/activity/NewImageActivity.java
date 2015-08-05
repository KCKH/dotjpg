/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.model.ListItem;
import tm.alashow.dotjpg.model.NewImage;
import tm.alashow.dotjpg.ui.adapter.NewImagesAdapter;
import tm.alashow.dotjpg.util.U;

/**
 * Created by alashov on 04/08/15.
 */
public class NewImageActivity extends BaseActivity {

    private ArrayList<NewImage> images = new ArrayList<>();
    private NewImagesAdapter newImagesAdapter;

    private File NEW_CAPTURED_IMAGE;

    @Bind(R.id.listView) ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showAddImageDialog();
        newImagesAdapter = new NewImagesAdapter(this, images);
        mListView.setAdapter(newImagesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                showAddImageDialog();
                return true;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == U.RESULT_GALLERY) {
                addImage(data.getData());
            } else if (requestCode == U.RESULT_CAMERA) {
                if (NEW_CAPTURED_IMAGE.exists()) {
                    U.addMediaToGallery(NEW_CAPTURED_IMAGE.getAbsolutePath());
                    addImage(Uri.fromFile(NEW_CAPTURED_IMAGE));
                }
            }
        }
    }

    private void showAddImageDialog() {
        if (images.size() >= Config.API_IMAGE_MAX_FILE_COUNT) {
            //reached limit, show message
            return;
        }

        final ListItem[] listItems = {
            new ListItem(getString(R.string.image_new_add_camera), R.drawable.ic_camera_grey),
            new ListItem(getString(R.string.image_new_add_gallery), R.drawable.ic_images_grey)
        };

        ListAdapter adapter = new ArrayAdapter<ListItem>(this, android.R.layout.select_dialog_item, android.R.id.text1, listItems) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_large));
                textView.setCompoundDrawablesWithIntrinsicBounds(listItems[position].icon, 0, 0, 0);
                textView.setCompoundDrawablePadding((int) (5 * getResources().getDisplayMetrics().density + 0.5f));
                return view;
            }
        };

        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.image_new_add))
            .setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0:
                            NEW_CAPTURED_IMAGE = U.generatePicturePath();
                            U.imageCapture(getActivity(), NEW_CAPTURED_IMAGE);
                            break;
                        case 1:
                            U.imageChoose(getActivity());
                            break;
                    }
                }
            }).show();
    }

    private void addImage(Uri uriImage) {
        if (images.size() >= Config.API_IMAGE_MAX_FILE_COUNT) {
            //reached limit, show message
            return;
        }

        File image = new File(U.getRealPathFromURI(this, uriImage));

        if (image.exists()) {
            if (image.length() < Config.API_IMAGE_MAX_FILE_SIZE) {
                images.add(new NewImage().setOriginFile(image));
                newImagesAdapter.notifyDataSetChanged();
            } else {
                //image too big
            }
        } else {
            //image not found
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_new_image;
    }

    @Override
    protected Boolean isChildActivity() {
        return true;
    }

    @Override
    protected String getActivityTag() {
        return Config.ACTIVITY_TAG_IMAGE_NEW;
    }
}
