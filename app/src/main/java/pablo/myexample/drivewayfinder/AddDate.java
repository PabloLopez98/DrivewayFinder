package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;

public class AddDate extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private TextView startTime, endTime;
    private EditText dividingNumber;
    private ArrayList<String> timeSlots;
    private OwnerProfileObject ownerProfileObject;
    private String chosenDate;
    private EditText rate;

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "Delete time slot " + myRecyclerViewAdapter.getItem(position) + " on row number " + position + "?", Toast.LENGTH_LONG).show();
    }

    //show time picker in a dialog
    public void showTimePicker(final View v) {
        TimePickerDialog Tp = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String M;
                if (hourOfDay >= 0 && hourOfDay <= 11) {
                    M = "AM";
                } else {
                    M = "PM";
                }
                if (hourOfDay == 0) {
                    hourOfDay = 12;
                }
                if (hourOfDay > 12) {
                    hourOfDay = hourOfDay - 12;
                    if (hourOfDay == 0) {
                        hourOfDay = 12;
                    }
                }
                switch (v.getId()) {
                    case R.id.startTime:
                        if (hourOfDay < 9) {
                            if (minute < 10) {
                                startTime.setText("0" + hourOfDay + ":0" + minute + " " + M);
                            } else {
                                startTime.setText("0" + hourOfDay + ":" + minute + " " + M);
                            }
                        } else {
                            if (minute < 10) {
                                startTime.setText(hourOfDay + ":0" + minute + " " + M);
                            } else {
                                startTime.setText(hourOfDay + ":" + minute + " " + M);
                            }
                        }
                        break;
                    case R.id.endTime:
                        if (hourOfDay < 9) {
                            if (minute < 10) {
                                endTime.setText("0" + hourOfDay + ":0" + minute + " " + M);
                            } else {
                                endTime.setText("0" + hourOfDay + ":" + minute + " " + M);
                            }
                        } else {
                            if (minute < 10) {
                                endTime.setText(hourOfDay + ":0" + minute + " " + M);
                            } else {
                                endTime.setText(hourOfDay + ":" + minute + " " + M);
                            }
                        }
                }
            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
        Tp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Tp.show();
    }

    //'Finish button' uses this method
    public void addDate(View view) {
        if (chosenDate.matches("") || rate.getText().toString().matches("") || timeSlots.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Incomplete", Toast.LENGTH_SHORT).show();
        } else {
           /*
           Must Do:
            - Create 'SpotObjectClass' to create object to setValue in firebase
            - Check if chosen Date exists within 'Spots', 'City', loop through 'date0 -> daten' branch
            */
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_date);

        displayImageAndLocation();

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                chosenDate = year + " " + month + " " + dayOfMonth;
            }
        });

        rate = findViewById(R.id.inputRate);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        dividingNumber = findViewById(R.id.dividingNumber);

        //arraylist to populate the RecyclerView with
        timeSlots = new ArrayList<>();

        // set up the RecyclerView
        recyclerView = findViewById(R.id.timeRecyclerView);
        recyclerView.setHasFixedSize(true);
        //allows recyclerview to expand completely
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, timeSlots);
        myRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }

    //for time slots
    public void divideTime(View view) {
        if ((startTime.getText().toString().contains("AM") || startTime.getText().toString().contains("PM")) && (endTime.getText().toString().contains("AM") || endTime.getText().toString().contains("PM"))) {
            try {
                int n = Integer.valueOf(dividingNumber.getText().toString());
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
                LocalTime startT = LocalTime.parse(startTime.getText().toString(), dtf);
                LocalTime endT = LocalTime.parse(endTime.getText().toString(), dtf);
                while (startT.isBefore(endT)) {
                    String s = dtf.format(startT);
                    startT = startT.plusMinutes(n);
                    String e = dtf.format(startT);
                    if (startT.isBefore(endT.plusMinutes(1))) {
                        String aSlot = s + " - " + e;
                        timeSlots.add(aSlot);
                    }
                }
                myRecyclerViewAdapter.notifyDataSetChanged();
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    public void displayImageAndLocation() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(userId).child("ProfileInfo");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //use this object when setting open spot appointment
                ownerProfileObject = dataSnapshot.getValue(OwnerProfileObject.class);
                //display image
                ImageView imageView = findViewById(R.id.drivewayImageShown);
                Picasso.get().load(ownerProfileObject.getDrivwayImageUrl()).into(imageView);
                //display location
                TextView textView = findViewById(R.id.drivewayLocationShown);
                String l = textView.getText().toString();
                String location = ownerProfileObject.getDrivewayLocation();
                String together = l + " " + location;
                textView.setText(together);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}