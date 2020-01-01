package pablo.myexample.drivewayfindertwo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import pablo.myexample.drivewayfinder.R;

public class Service extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "default_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("text");
        showNotification(getApplicationContext(), title, body);
    }

    private void showNotification(Context context, String title, String body) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.ic_location_on_black_24dp).setContentTitle(title).setContentText(body).setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_MESSAGE).build();
            notificationManagerCompat.notify(1, notification);

        }

    }
}
