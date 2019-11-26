package pablo.myexample.drivewayfindertwo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import pablo.myexample.drivewayfinder.R;

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        displayNotification();
        return null;
    }

    private void displayNotification() {

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        Intent resultIntent = new Intent(getApplicationContext(), TheDriverActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "channel").setContentTitle("Reminder:").setContentText("Check Time Slot!").setSmallIcon(R.drawable.ic_location_on_black_24dp).setAutoCancel(true).setContentIntent(resultPendingIntent);
        manager.notify(1, builder.build());

    }

}
