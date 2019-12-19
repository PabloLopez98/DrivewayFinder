package pablo.myexample.drivewayfindertwo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import pablo.myexample.drivewayfinder.MainActivity;
import pablo.myexample.drivewayfinder.R;

public class SmsListener extends BroadcastReceiver {

    private static final String CHANNEL_ID = "default_channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {

            Bundle bundle = intent.getExtras();

            if (bundle != null) {

                showNotification(context, "elTitle", "elText");

            }

        }

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
