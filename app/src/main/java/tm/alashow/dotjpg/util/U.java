/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;

import tm.alashow.dotjpg.App;
import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.android.IntentManager;

public class U {
    public static int RESULT_GALLERY = 0xea;
    public static final int RESULT_CAMERA = 0x84;

    public static final int SNACK_DEFAULT = 0;
    public static final int SNACK_SUCCESS = 1;
    public static final int SNACK_ERROR = 2;

    /**
     * Hide Given view, by setting visibility View.GONE
     *
     * @param _view view for hide
     */
    public static void hideView(View _view) {
        if (_view != null) {
            _view.setVisibility(View.GONE);
        }
    }

    /**
     * Hide view if it's visibility equals android.view.View.VISIBLE
     */
    public static void hideViewIfVisible(View _view) {
        if (_view != null) {
            if (_view.getVisibility() == View.VISIBLE) {
                hideView(_view);
            }
        }
    }

    /**
     * Show given view, by setting visibility View.VISIBLE
     *
     * @param _view view for show
     */
    public static void showView(View _view) {
        if (_view != null) {
            _view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides if visible, show if hidden given view
     *
     * @param _view view for toggle visibility
     */
    public static void toggleView(View _view) {
        if (_view != null) {
            if (_view.getVisibility() == View.VISIBLE) {
                hideView(_view);
            } else {
                showView(_view);
            }
        }
    }

    /**
     * Shows aplication error alert dialog
     *
     * @param _activity activity of caller
     */
    public static void applicationError(Activity _activity) {
        try {
            if (_activity != null) {
                showCenteredToast(_activity, R.string.exception);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attach fragment to custom container
     *
     * @param _activity   BaseActivity activity of caller
     * @param newFragment new fragment
     */
    public static void attachFragment(AppCompatActivity _activity, Fragment newFragment) {
        if (_activity.findViewById(R.id.container) != null) {
            _activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, newFragment).commit();
        }
    }

    /**
     * Attach fragment to custom container
     *
     * @param _activity   BaseActivity activity of caller
     * @param newFragment new Fragment
     * @param _container  placeholder for fragment
     */
    public static void attachFragment(AppCompatActivity _activity, Fragment newFragment, int _container) {
        _activity.getSupportFragmentManager().beginTransaction().replace(_container, newFragment).commit();
    }

    public static void l(String message) {
        if (! Config.DEBUG) {
            return;
        }
        if (message != null && message.equals("")) {
            return;
        }
        Log.d(Config.LOG_APP_NAME, message);
    }

    public static void e(String message) {
        Log.e(Config.LOG_APP_NAME, message);
    }

    public static String getPath(final Uri uri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(App.c(), uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(App.c(), contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    switch (type) {
                        case "image":
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "video":
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "audio":
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            break;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                        split[1]
                    };

                    return getDataColumn(App.c(), contentUri, selection, selectionArgs);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(App.c(), uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
            column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * For picasso image loadings
     *
     * @return #f2f2f2 colored drawable
     */
    public static Drawable imagePlaceholder() {
        return new ColorDrawable(Color.parseColor("#e6e6e6"));
    }

    /**
     * Constructing progress dialog for action loading
     *
     * @param _context context
     * @return configured progress dialog
     */
    public static ProgressDialog createActionLoading(Context _context) {
        ProgressDialog dialog = new ProgressDialog(_context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setMessage(_context.getString(R.string.loading));
        return dialog;
    }

    /**
     * Constructing progress dialog for action loading
     *
     * @param _context context
     * @return configured progress dialog
     */
    public static ProgressDialog createProgressActionLoading(Context _context) {
        ProgressDialog dialog = new ProgressDialog(_context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle(R.string.loading);
        return dialog;
    }

    public static ProgressDialog createCancellableActionLoading(Context _context, DialogInterface.OnClickListener onClickListener) {
        ProgressDialog dialog = new ProgressDialog(_context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, _context.getString(R.string.cancel), onClickListener);
        dialog.setMessage(_context.getString(R.string.loading));
        return dialog;
    }

    public static ProgressDialog createCancellableProgressActionLoading(Context _context, DialogInterface.OnClickListener onClickListener) {
        ProgressDialog dialog = new ProgressDialog(_context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, _context.getString(R.string.cancel), onClickListener);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle(R.string.loading);
        return dialog;
    }

    /**
     * Setting colors to swiperefreshlayout
     *
     * @param _s swprl
     */
    public static void setColorScheme(SwipeRefreshLayout _s) {
        _s.setColorSchemeResources(R.color.primary, R.color.blue, R.color.red, R.color.yellow);
    }

    /**
     * Showing or hiding keyboard for editText
     *
     * @param view view of edittext
     * @param show show or hide
     */
    public static void keyboard(View view, boolean show) {
        if (show) {
            showKeyboard(view);
        } else {
            hideKeyboard(view);
        }
    }

    public static void showKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

        ((InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, 0);
    }

    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (! imm.isActive()) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Copy given text to clipboard
     *
     * @param context context for access system services
     * @param string  text to copy
     */
    public static void copyToClipboard(Context context, String string) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Link", string);
        clipboard.setPrimaryClip(clip);
    }

    public static boolean copyFile(File sourceFile, File destFile) throws IOException {
        if (! destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
        return true;
    }

    public static String getTrimmedString(String src) {
        String result = src.trim();
        if (result.length() == 0) {
            return result;
        }
        while (src.startsWith("\n")) {
            src = src.substring(1);
        }
        while (src.endsWith("\n")) {
            src = src.substring(0, src.length() - 1);
        }
        return src;
    }

    public static boolean isStringEmpty(String value) {
        return value == null || value.equals("null") || getTrimmedString(value).length() <= 0;
    }

    public static void showCenteredToast(Context context, String string) {
        if (context == null) {
            return;
        }
        if (string == null || string.equals("")) {
            string = "null";
        }
        Toast toast = Toast.makeText(context, string, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showCenteredToast(Context context, @StringRes int stringRes) {
        showCenteredToast(context, context.getString(stringRes));
    }

    /**
     * Show {@link Snackbar}
     *
     * @param view           The view to find a parent from.
     * @param stringResource The resource id of the string resource to use. Can be formatted text. Lower than 0 if not specified
     * @param stringMessage  The text to show. Can be formatted text. null if not specified
     * @param style          color of snack text DEFAULT, SUCCESS, ERROR
     * @return showed instance of snackbar. Set actions or hide.
     */
    public static Snackbar showSnack(View view, @StringRes
    int stringResource, String stringMessage, int style) {
        Snackbar snackbar = null;
        if (view != null && (stringMessage != null || stringResource > 0)) {
            if (stringResource > 0) {
                snackbar = Snackbar.make(view, stringResource, Snackbar.LENGTH_LONG);
            } else if (stringMessage != null) {
                snackbar = Snackbar.make(view, stringMessage, Snackbar.LENGTH_LONG);
            }

            if (snackbar != null) {
                if (style > SNACK_DEFAULT) {
                    TextView snackText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    if (style == SNACK_SUCCESS) {
                        snackText.setTextColor(view.getContext().getResources().getColor(R.color.green));
                    }
                    if (style == SNACK_ERROR) {
                        snackText.setTextColor(view.getContext().getResources().getColor(R.color.red_light));
                    }
                }
                snackbar.show();
            }
        }
        return snackbar;
    }

    public static Snackbar showSnack(View view, @StringRes int stringResource, int style) {
        return showSnack(view, stringResource, null, style);
    }

    public static Snackbar showSnack(View view, String stringMessage, int style) {
        return showSnack(view, - 1, stringMessage, style);
    }

    public static Snackbar showError(View view, @StringRes int stringResource) {
        return showSnack(view, stringResource, null, SNACK_ERROR);
    }

    public static Snackbar showError(View view, String stringMessage) {
        return showSnack(view, - 1, stringMessage, SNACK_ERROR);
    }

    public static Snackbar showNetworkError(View view) {
        return showSnack(view, R.string.network_error, SNACK_ERROR);
    }

    public static Snackbar showUnknownError(View view) {
        return showSnack(view, R.string.error, SNACK_ERROR);
    }

    /**
     * Returns true if {@param view} is contained within {@param container}'s bounds.
     */
    public static boolean isViewInBounds(@NonNull View container, @NonNull View view) {
        Rect containerBounds = new Rect();
        container.getHitRect(containerBounds);
        return view.getLocalVisibleRect(containerBounds);
    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
            new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    /**
     * Secure random (?) string for session. takes ~20 ms time on nexus 5, on genymotion emulator ~1 ms
     *
     * @return 50 length random string
     */
    public static String randomSessionId() {
        return new BigInteger(250, new SecureRandom()).toString(32);
    }

    public static String randomFileName() {
        return new BigInteger(50, new SecureRandom()).toString(32);
    }

    /**
     * Get screen width in pixels
     *
     * @param context context
     * @return x sizem width
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static void downloadImage(final Context context, final String imageUrl) {
        downloadImage(context, imageUrl, null);
    }

    /**
     * Downloads given url to app base folder
     *
     * @param imageUrl image for download
     * @param view     view for snackbar
     */
    public static void downloadImage(final Context context, final String imageUrl, final View view) {
//        Uri downloadUri = Uri.parse(imageUrl);
//
//        String extension = imageUrl;
//        int i = extension.lastIndexOf('.');
//        if (i >= 0) {
//            extension = extension.substring(i);
//        }
//
//        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
//            .setDestinationInExternalPublicDir("/" + Config.LOCAL_IMAGES_FOLDER, randomFileName() + extension);
//
//        downloadManager.enqueue(request);
        if (view != null && view.isShown()) {
            showSnack(view, R.string.image_downloading, SNACK_DEFAULT);
        } else {
            showCenteredToast(App.c(), R.string.image_downloading);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1); //get unique dotjpg filename
                final File file = new File(getBaseFolder(), fileName);
                FileOutputStream out = null;

                try {
                    Bitmap bitmap = Glide.with(App.c()).load(imageUrl)
                        .asBitmap()
                        .priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(- 1, - 1) //full size
                        .get();

                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (view != null && view.isShown()) {
                                Snackbar snackbar = showSnack(view, R.string.image_download_success, SNACK_SUCCESS);
                                //Open image
                                if (file.exists()) {
                                    snackbar.setAction(R.string.image_download_success_view, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            IntentManager.with(context).openLocalImage(file);
                                        }
                                    });
                                }
                            } else {
                                showCenteredToast(App.c(), R.string.image_download_success);
                            }
                            U.l("Downloaded image with glide: " + imageUrl);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (view != null && view.isShown()) {
                                showSnack(view, R.string.image_download_failed, SNACK_ERROR);
                            } else {
                                showCenteredToast(App.c(), R.string.image_download_failed);
                            }
                            U.l("Download image failed with glide: " + imageUrl);
                        }
                    });
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        U.l("Download image failed with glide: " + imageUrl);
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * Get type of image with reading mimeType of image
     *
     * @param file image
     * @return image format
     */
    public static String getImageType(File file) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            return (options.outMimeType != null) ? options.outMimeType.replace("image/", "") : null;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    /**
     * Human readable file size
     *
     * @param bytes size of file in bytes
     * @param si    is si unit
     * @return "110 kB" like size
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Open Image Choose Intent
     *
     * @param activity Current activity
     */
    public static void imageChoose(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, RESULT_GALLERY);
    }

    /**
     * Open Image Capture Intent with custom output file
     *
     * @param activity Current activity
     */
    public static void imageCapture(Activity activity, File output) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        activity.startActivityForResult(intent, RESULT_CAMERA);
    }

    public static File getBaseFolder() {
        File baseFolder = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            baseFolder = new File(Environment.getExternalStorageDirectory(), Config.LOCAL_IMAGES_FOLDER);
            if (! baseFolder.mkdirs()) {
                if (! baseFolder.exists()) {
                    U.l("failed to create directory");
                    return null;
                }
            }
        } else {
            U.l("External storage is not mounted READ/WRITE.");
        }

        return baseFolder;
    }

    public static File getCompressedFileFolder() {
        File baseFolder = getBaseFolder();
        File tempFolder = new File(baseFolder, Config.LOCAL_IMAGES_COMPRESSED_TEMP);
        tempFolder.mkdirs();
        return tempFolder;
    }

    public static File generateCompressFilePath() {
        File tempFolder = getCompressedFileFolder();
        return new File(tempFolder, randomFileName() + ".jpg");
    }

    public static void clearTempCompressedFiles() {
        File tempFolder = getCompressedFileFolder();
        if (tempFolder.isDirectory()) {
            String[] files = tempFolder.list();
            for(int i = 0; i < files.length; i++)
                new File(tempFolder, files[i]).delete();
        }
    }

    public static File generatePicturePath() {
        try {
            File baseFolder = getBaseFolder();
            return new File(baseFolder, randomFileName() + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addMediaToGallery(String fromPath) {
        if (fromPath == null) {
            return;
        }
        File f = new File(fromPath);
        Uri contentUri = Uri.fromFile(f);
        addMediaToGallery(contentUri);
    }

    public static void addMediaToGallery(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        App.applicationContext.sendBroadcast(mediaScanIntent);
    }
}
