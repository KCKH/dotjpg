/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import tm.alashow.dotjpg.App;
import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;

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
        if (_view != null) if (_view.getVisibility() == View.VISIBLE) hideView(_view);
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
            if (_view.getVisibility() == View.VISIBLE) hideView(_view);
            else showView(_view);
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
        if (_activity.findViewById(R.id.container) != null)
            _activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, newFragment).commit();
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
        if (! Config.DEBUG) return;
        if (message != null && message.equals("")) return;
        Log.d(Config.LOG_APP_NAME, message);
    }

    public static void e(String message) {
        Log.e(Config.LOG_APP_NAME, message);
    }

    /**
     * Changes title and divider color of alert dialog
     *
     * @param alertDialog AlertDialog object for change
     * @param color       Color for change
     */
    public static void customAlertDialog(AlertDialog alertDialog, int color) {
        try {
            int textViewId = alertDialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
            if (textViewId != 0) {
                TextView tv = (TextView) alertDialog.findViewById(textViewId);
                tv.setTextColor(color);
            }
            int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
            if (dividerId != 0) {
                View divider = alertDialog.findViewById(dividerId);
                if (divider != null) {
                    divider.setBackgroundColor(color);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRealPathFromURI(Activity context, Uri contentUri) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    /**
     * For picasso image loadings
     *
     * @return #f2f2f2 colored drawable
     */
    public static Drawable imagePlaceholder() {
        return new ColorDrawable(Color.parseColor("#d9d9d9"));
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
        if (show) showKeyboard(view);
        else hideKeyboard(view);
    }

    public static void showKeyboard(View view) {
        if (view == null) return;
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

        ((InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, 0);
    }

    public static void hideKeyboard(View view) {
        if (view == null) return;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (! imm.isActive()) return;
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
        if (context == null) return;
        if (string == null || string.equals("")) string = "null";
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
                    if (style == SNACK_SUCCESS)
                        snackText.setTextColor(view.getContext().getResources().getColor(R.color.green));
                    if (style == SNACK_ERROR)
                        snackText.setTextColor(view.getContext().getResources().getColor(R.color.red_light));
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

    /**
     * Downloads given url to app folder with random name
     *
     * @param context  context
     * @param imageUrl image for download
     */
    public static void downloadImage(Context context, String imageUrl) {
        Uri downloadUri = Uri.parse(imageUrl);

        String extension = imageUrl;
        int i = extension.lastIndexOf('.');
        if (i >= 0) extension = extension.substring(i);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
            .setDestinationInExternalPublicDir("/" + Config.LOCAL_IMAGES_FOLDER, UUID.randomUUID().toString().replace("-", "").substring(0, 10) + extension);

        downloadManager.enqueue(request);
        U.showCenteredToast(context, R.string.image_downloading);
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
     * @return 110 kB like size
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
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

    private static File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new File(Environment.getExternalStorageDirectory(), Config.LOCAL_IMAGES_FOLDER);
            if (! storageDir.mkdirs()) {
                if (! storageDir.exists()) {
                    U.l("failed to create directory");
                    return null;
                }
            }
        } else {
            U.l("External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    public static File generatePicturePath() {
        try {
            File storageDir = getAlbumDir();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
            return new File(storageDir, "IMG_" + timestamp + ".jpg");
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
