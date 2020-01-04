package pablo.myexample.drivewayfinder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import pablo.myexample.drivewayfindertwo.RequestedOrAppointmentObject;
import pablo.myexample.drivewayfindertwo.TheDriverActivity;

public class servicetwo extends Service {
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(userId);
    private ChildEventListener databaseReferenceListener;
    private boolean initialListen;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //only notify owner when a request is added, worry about time is up later.
    public void onStartCommandSecondMethod() {
        initialListen = true;
        databaseReferenceListener = databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getKey().matches("ProfileInfo")) {
                    //do nothing
                } else if (dataSnapshot.getKey().matches("Appointments")) {
                    //do nothing
                } else {
                    if (initialListen == true) {
                        initialListen = false;
                    } else {
                        notifyOwner("Check Request(s)");
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void notifyOwner(String s) {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        Intent resultIntent = new Intent(getApplicationContext(), TheDriverActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "channel").setContentTitle("Alert:").setContentText(s).setSmallIcon(R.drawable.ic_location_on_black_24dp).setAutoCancel(true).setContentIntent(resultPendingIntent);
        manager.notify(1, builder.build());
    }

    public void onStartCommandFirstMethod() {

        String[] strings = Calendar.getInstance().getTime().toString().split(" ");
        String year = strings[5];
        String day = strings[2];
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM").withLocale(Locale.ENGLISH);
        TemporalAccessor temporalAccessor = dateTimeFormatter.parse(strings[1]);
        String month = String.valueOf(temporalAccessor.get(ChronoField.MONTH_OF_YEAR));
        String currentDate = "2019 11 22";//year + " " + month + " " + day;

        DatabaseReference databaseReferenceTwo = FirebaseDatabase.getInstance().getReference().child("Drivers").child(userId).child("Appointments").child(currentDate);
        databaseReferenceTwo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    ArrayList<LocalTime> endTimes = new ArrayList<>();

                    for (DataSnapshot timeSlot : dataSnapshot.getChildren()) {

                        RequestedOrAppointmentObject data = timeSlot.getValue(RequestedOrAppointmentObject.class);

                        LocalTime endT = LocalTime.parse(data.getTimeSlot().substring(11, 19), new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter());
                        endTimes.add(endT);

                    }

                    //missing code here

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        onStartCommandSecondMethod();

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        databaseReference.removeEventListener(databaseReferenceListener);
        super.onDestroy();
    }
}
