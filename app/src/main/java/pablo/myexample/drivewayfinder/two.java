package pablo.myexample.drivewayfinder;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pablo.myexample.drivewayfindertwo.RequestedOrAppointmentObject;

public class two extends Fragment {

    private TextView name, date, phone, location, timeRemaining, timeStartEnd;
    private String ownerId;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, container, false);
        ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        name = view.findViewById(R.id.twoClientName);
        date = view.findViewById(R.id.twoDate);
        phone = view.findViewById(R.id.twoPhoneNumber);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().toString().matches("")) {
                    //do nothing
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone.getText().toString()));
                    startActivity(intent);
                }
            }
        });
        location = view.findViewById(R.id.twoDrivewayLocation);
        timeRemaining = view.findViewById(R.id.twoTimeRemaining);
        timeStartEnd = view.findViewById(R.id.twoTimeStartEnd);
        fillInFragment();
        return view;
    }

    public void fillInFragment() {

        String[] strings = Calendar.getInstance().getTime().toString().split(" ");
        String year = strings[5];
        String day = strings[2];
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM").withLocale(Locale.ENGLISH);
        TemporalAccessor temporalAccessor = dateTimeFormatter.parse(strings[1]);
        String month = String.valueOf(temporalAccessor.get(ChronoField.MONTH_OF_YEAR));

        /*if (Integer.parseInt(day) < 10) {
            day = "0" + day;
        }*/

        if (Integer.parseInt(month) < 10) {
            month = "0" + month;
        }

        String currentDate = year + " " + month + " " + day;
        Log.i("currentDate", currentDate);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerId).child("Appointments").child(currentDate);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot timeSlot : dataSnapshot.getChildren()) {

                        RequestedOrAppointmentObject data = timeSlot.getValue(RequestedOrAppointmentObject.class);

                        DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
                        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                        LocalTime currentT = LocalTime.parse(currentTime, dtf);
                        LocalTime startT = LocalTime.parse(data.getTimeSlot().substring(0, 8), dtf);
                        LocalTime endT = LocalTime.parse(data.getTimeSlot().substring(11, 19), dtf);

                        if (currentT.isBefore(endT.plusMinutes(1)) && currentT.isAfter(startT.minusMinutes(1))) {
                            name.setText(data.getDriverName());
                            date.setText(data.getDate());
                            phone.setText(data.getDriverPhoneNumber());
                            location.setText(data.getLocation());
                            timeStartEnd.setText(data.getTimeSlot());
                            //hide progress circle, show layout
                            view.findViewById(R.id.fragtwocircle).setVisibility(View.INVISIBLE);
                            view.findViewById(R.id.fragtwolayout).setVisibility(View.VISIBLE);
                            countDownTimer(currentT, endT);
                            break;
                        }

                    }
                    //hide progress circle, show layout
                    view.findViewById(R.id.fragtwocircle).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.fragtwolayout).setVisibility(View.VISIBLE);
                } else {
                    //hide progress circle, show layout
                    view.findViewById(R.id.fragtwocircle).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.fragtwolayout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void countDownTimer(LocalTime currentT, LocalTime endT) {

        long milliSecOfCurrentSec = LocalDateTime.now().getSecond() * 1000;//testing

        long milliSecOfCurrentMinute = currentT.getMinute() * 60 * 1000;//1minute * 60seconds * 1000milliseconds = 60000
        long milliSecOfCurrentHour = currentT.getHour() * 60 * 60 * 1000;//1hour * 60minutes * 60seconds * 1000milliseconds = 3600000
        //long totalMilliSecOfCurrentTime = milliSecOfCurrentMinute + milliSecOfCurrentHour;
        long totalMilliSecOfCurrentTime = milliSecOfCurrentHour + milliSecOfCurrentMinute + milliSecOfCurrentSec;

        long milliSecOfEndMinute = endT.getMinute() * 60 * 1000;//1minute * 60seconds * 1000milliseconds = 60000
        long milliSecOfEndHour = endT.getHour() * 60 * 60 * 1000;//1hour * 60minutes * 60seconds * 1000milliseconds = 3600000
        long totalMilliSecOfEndTime = milliSecOfEndMinute + milliSecOfEndHour;

        long total = totalMilliSecOfEndTime - totalMilliSecOfCurrentTime;

        new CountDownTimer(total, 1000) {

            public void onTick(long millisUntilFinished) {

                long totalSeconds = millisUntilFinished / 1000;

                long hours = totalSeconds / 3600;
                long secondsLeft = totalSeconds - hours * 3600;//this is not displayed
                long minutes = secondsLeft / 60;
                long seconds = secondsLeft - minutes * 60;

                timeRemaining.setText(String.valueOf(hours + " : " + minutes + " : " + seconds));
            }

            public void onFinish() {
                timeStartEnd.setText("done!");
            }
        }.start();
    }

}
