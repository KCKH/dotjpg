/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.android.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.android.PreferencesManager;
import tm.alashow.dotjpg.ui.activity.ImagesActivity;
import tm.alashow.dotjpg.ui.activity.MainActivity;
import tm.alashow.dotjpg.util.U;

public class PushNotificationsService extends IntentService {

    public PushNotificationsService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (! extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                U.l("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                U.l("Message is deleted");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                U.l("Normal message received! Showing notification..");
                showNotification(getApplicationContext(), intent);
            }
        }
        GCMReceiver.completeWakefulIntent(intent);
    }

    private void showNotification(Context context, Intent intent) {
        try {
            PreferencesManager preferencesManager = PreferencesManager.getInstance(context);
            String type = intent.getStringExtra("type");
            String value = intent.getStringExtra("value");
            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("message");

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notificationIntent = null;

            switch (type) {
                case "text":
                    notificationIntent = new Intent(context, MainActivity.class);
                    notificationIntent.setAction(type + "_" + value);
                    break;
                case "new_photos":
                    if (preferencesManager.getPreferences().getBoolean("notifications", true)) {
                        notificationIntent = new Intent(context, MainActivity.class);
                        notificationIntent.setAction(type + "_" + value);
                    } else {
                        return;
                    }
                    break;
                case "gallery":
                    notificationIntent = new Intent(context, ImagesActivity.class);
                    notificationIntent.setAction(type + "_" + value.hashCode());
                    notificationIntent.putExtra(Config.EXTRA_IMAGES_TYPE, Config.API_ACTION_GET_GALLERY);
                    notificationIntent.putExtra(Config.EXTRA_GALLERY_ID, value);
                    break;
                case "url":
                    notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(value));
                    break;
                default:
                    return;
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).
                setContentTitle(title).
                setContentText(message).
                setStyle(new NotificationCompat.BigTextStyle().bigText(message)).
                setSmallIcon(R.drawable.notification).
                setColor(context.getResources().getColor(R.color.primary)).
                setContentIntent(pendingIntent).setAutoCancel(true);
            mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
            notificationManager.notify(type.hashCode(), mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
