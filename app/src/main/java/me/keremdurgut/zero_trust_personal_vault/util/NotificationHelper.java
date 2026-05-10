package me.keremdurgut.zero_trust_personal_vault.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import me.keremdurgut.zero_trust_personal_vault.R;

/**
 * Bildirim yönetim sınıfı.
 * Yeni parola kaydedildiğinde sistem bildirimi gönderir.
 */
public class NotificationHelper {

    private static final String CHANNEL_ID = "vault_notifications";
    private static final int NOTIFICATION_ID = 1001;

    /**
     * Bildirim kanalı oluşturur (Android 8.0+ için zorunlu).
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.notification_channel_name);
            String description = context.getString(R.string.notification_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Yeni parola kaydedildiğinde bildirim gösterir.
     */
    public static void showPasswordSavedNotification(Context context, String itemTitle) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_lock)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText("\"" + itemTitle + "\" kasaya eklendi.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(NOTIFICATION_ID + (int) System.currentTimeMillis() % 10000, builder.build());
        } catch (SecurityException e) {
            // Bildirim izni yoksa sessizce devam et
            e.printStackTrace();
        }
    }
}
