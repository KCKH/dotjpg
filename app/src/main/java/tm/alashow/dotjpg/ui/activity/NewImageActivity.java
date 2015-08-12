/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;

import butterknife.Bind;
import butterknife.ButterKnife;
import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.android.IntentManager;
import tm.alashow.dotjpg.model.ListItem;
import tm.alashow.dotjpg.model.NewImage;
import tm.alashow.dotjpg.ui.adapter.NewImagesAdapter;
import tm.alashow.dotjpg.ui.view.CircleProgress;
import tm.alashow.dotjpg.util.ApiClient;
import tm.alashow.dotjpg.util.U;

/**
 * Created by alashov on 04/08/15.
 */
public class NewImageActivity extends BaseActivity {

    private ArrayList<NewImage> images = new ArrayList<>();
    private NewImagesAdapter newImagesAdapter;

    private File NEW_CAPTURED_IMAGE;

    @Bind(R.id.upload) View uploadView;
    @Bind(R.id.listView) ListView mListView;
    @Bind(R.id.empty) View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();

        //Receiving data
        Intent intent = getIntent();
        String intentAction = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(intentAction) || Intent.ACTION_SEND_MULTIPLE.equals(intentAction) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    urlUploadDialog(sharedText);
                }
            } else if (type.startsWith("image/")) {
                if (Intent.ACTION_SEND.equals(intentAction)) {
                    Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    if (imageUri != null) {
                        addImage(imageUri);
                    }
                } else if (Intent.ACTION_SEND_MULTIPLE.equals(intentAction)) {
                    ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                    for(int i = 0; i < imageUris.size(); i++) {
                        if (isReachedMaxCount()) {
                            U.showError(emptyView, R.string.image_new_error_max_count);
                            break;
                        }
                        addImage(imageUris.get(i));
                    }
                }
            }
        }

        //if list is empty, show chooser
        if (images.size() == 0) {
            showAddImageDialog();
        }
    }

    private void initViews() {
        newImagesAdapter = new NewImagesAdapter(this, images);
        mListView.setAdapter(newImagesAdapter);

        mListView.setEmptyView(emptyView);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                alertDialogBuilder.setTitle(R.string.image_new_remove);
                alertDialogBuilder.setMessage(R.string.image_new_remove_description);
                alertDialogBuilder.setNegativeButton(R.string.no, null);
                alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (images.get(position).isCompressed()) {
                            images.get(position).getCompressedFile().delete();
                        }

                        images.remove(position);
                        newImagesAdapter.notifyDataSetChanged();
                    }
                });
                alertDialogBuilder.show();
                return true;
            }
        });

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddImageDialog();
            }
        });

        uploadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImages();
            }
        });
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
            case R.id.url:
                urlUploadDialog();
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
        if (isReachedMaxCount()) {
            U.showError(emptyView, R.string.image_new_error_max_count);
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
        if (isReachedMaxCount()) {
            U.showError(emptyView, R.string.image_new_error_max_count);
            return;
        }

        File image = new File(U.getRealPathFromURI(this, uriImage));

        if (image.exists()) {
            if (image.length() < Config.API_IMAGE_MAX_FILE_SIZE) {
                images.add(new NewImage().setOriginFile(image));
                newImagesAdapter.notifyDataSetChanged();
            } else {
                U.showError(emptyView, R.string.image_new_error_max_size);
            }
        } else {
            U.showError(emptyView, R.string.image_new_error_not_found);
        }
    }

    private boolean isReachedMaxCount() {
        return images.size() >= Config.API_IMAGE_MAX_FILE_COUNT;
    }

    private void shakeIt(View target) {
        if (target == null) {
            return;
        }
        AnimatorSet mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(ObjectAnimator.ofFloat(target, "translationX", 0, 25, - 25, 25, - 25, 15, - 15, 6, - 6, 0));
        mAnimatorSet.start();
    }

    private void uploadImages() {
        for(int i = 0; i < images.size(); i++) {
            NewImage newImage = images.get(i);
            if (! newImage.getFile().exists()) {
                images.remove(i);
            }
        }
        newImagesAdapter.notifyDataSetChanged();

        if (images.isEmpty()) {
            shakeIt(emptyView);
        } else { //upload 'em
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.upload_dialog, null);

            CircleProgress circleProgress = ButterKnife.findById(view, R.id.circleProgress);
            final TextView progressTextPercent = ButterKnife.findById(view, R.id.progressTextPercent);
            final TextView progressTextBytes = ButterKnife.findById(view, R.id.progressTextBytes);
            final ProgressBar progressBar = ButterKnife.findById(view, R.id.progressBar);

            circleProgress.startAnim();

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ApiClient.cancelAll();
                }
            });

            alertDialogBuilder.setView(view);
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);

            RequestParams requestParams = new RequestParams();
            requestParams.put(Config.API_CONTROLLER_PARAM, Config.API_CONTROLLER_IMAGE);
            requestParams.put(Config.API_ACTION_PARAM, Config.API_ACTION_UPLOAD_FILE);
            requestParams.put(Config.API_SESSION_ID_PARAM, preferencesManager.getSessionId());

            ArrayList<File> files = new ArrayList<>();
            for(NewImage newImage : images) {
                files.add(newImage.getFile());
            }

            try {
                requestParams.put(Config.API_ACTION_UPLOAD_FILE_PARAM, files.toArray(new File[files.size()]));
            } catch (Exception e) {
                U.showError(uploadView, R.string.image_new_error_not_found);
                return; //Why continue? Impossible exception
            }

            ApiClient.post(Config.API, requestParams, new JsonHttpResponseHandler() {
                int progress = 0;

                @Override
                public void onStart() {
                    alertDialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONObject data = response.getJSONObject("data");
                            if (data.has("gallery")) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        U.clearTempCompressedFiles();
                                    }
                                }).start();
                                String galleryId = data.getString("gallery");
                                IntentManager.with(getActivity()).openGallery(galleryId, true);
                            } else {
                                U.showError(uploadView, R.string.image_new_upload_fail);
                            }
                        } else {
                            U.showError(uploadView, R.string.error);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        U.showError(uploadView, R.string.exception);
                    }
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    progressTextBytes.setText(U.humanReadableByteCount(bytesWritten, true) + "/" + U.humanReadableByteCount(totalSize, true));

                    if (bytesWritten > totalSize) {
                        return;
                    }

                    int newProgress = (int) (100.0 / totalSize * bytesWritten);
                    if (newProgress > progress) {
                        progress = newProgress;
                        progressBar.setProgress(progress);
                        progressTextPercent.setText(progress + "%");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    U.showNetworkError(uploadView);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    U.showNetworkError(uploadView);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    U.showNetworkError(uploadView);
                }

                @Override
                public void onFinish() {
                    alertDialog.dismiss();
                }
            }, 1000 * 1000 /*1000 seconds limit*/);
        }
    }

    private void urlUploadDialog() {
        urlUploadDialog(null);
    }

    /**
     * @param images default text for urls input, for shared text
     */
    private void urlUploadDialog(String images) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        final MaterialEditText input = new MaterialEditText(getActivity());

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(30, 10, 30, 0);
        input.setMaxLines(4);
        input.setPrimaryColor(getResources().getColor(R.color.primary));
        input.setUnderlineColor(getResources().getColor(R.color.primary));

        if (images != null) {
            input.setText(images);
        }

        layout.addView(input, params);


        alertDialogBuilder.setView(layout).setTitle(R.string.image_new_url)
            .setMessage(R.string.image_new_url_hint)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.image_new_upload, null);


        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String images = input.getText().toString();
                        Matcher matcher = Patterns.WEB_URL.matcher(images);

                        if (! matcher.find()) { //if there no any urls
                            shakeIt(input);
                        } else {
                            urlUpload(images, alertDialog);
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    /**
     * @param images      string that contains url of images
     * @param alertDialog dialog with input, to dismiss after success uploading. Maybe user will come back
     */
    private void urlUpload(String images, final AlertDialog alertDialog) {

        final ProgressDialog progressDialog = U.createCancellableActionLoading(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ApiClient.cancelAll();
            }
        });

        RequestParams requestParams = new RequestParams();
        requestParams.put(Config.API_CONTROLLER_PARAM, Config.API_CONTROLLER_IMAGE);
        requestParams.put(Config.API_ACTION_PARAM, Config.API_ACTION_UPLOAD_URL);
        requestParams.put(Config.API_SESSION_ID_PARAM, preferencesManager.getSessionId());

        requestParams.put(Config.API_ACTION_UPLOAD_URL_PARAM, images);

        ApiClient.post(Config.API, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject data = response.getJSONObject("data");
                        if (data.has("gallery")) {
                            alertDialog.dismiss();
                            String galleryId = data.getString("gallery");
                            IntentManager.with(getActivity()).openGallery(galleryId, false);
                        } else {
                            U.showCenteredToast(getActivity(), R.string.image_new_url_fail);
                        }
                    } else {
                        U.showCenteredToast(getActivity(), R.string.error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    U.showCenteredToast(getActivity(), R.string.exception);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                U.showCenteredToast(getActivity(), R.string.network_error);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                U.showCenteredToast(getActivity(), R.string.network_error);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                U.showCenteredToast(getActivity(), R.string.network_error);
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
            }
        }, 60 * 1000);
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
