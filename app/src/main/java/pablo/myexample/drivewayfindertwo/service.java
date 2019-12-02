package pablo.myexample.drivewayfindertwo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import pablo.myexample.drivewayfinder.MainActivity;

/*
    If any data changes under driver id, then a notification will be displayed.
    This works as long as driver is logged in to app.

    The ending times are retrieved and notifications are scheduled to be set off accordingly.
 */

public class service extends Service {

    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Drivers").child(userId);
    private ChildEventListener databaseReferenceListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onStartCommandSecondMethod() {
        databaseReferenceListener = databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //to create the notification
                OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(MyWorker.class).setInitialDelay(1, TimeUnit.SECONDS).build();
                WorkManager.getInstance(getApplicationContext()).enqueue(request);
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