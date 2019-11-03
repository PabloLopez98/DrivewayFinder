package pablo.myexample.drivewayfinder;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.LocalTime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;

public class AddDate extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter myRecyclerViewAdapter;
    TextView startTime, endTime;
    EditText dividingNumber;

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "Delete time slot " + myRecyclerViewAdapter.getItem(position) + " on row number " + position + "?", Toast.LENGTH_LONG).show();
    }

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_date);

        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        dividingNumber = findViewById(R.id.dividingNumber);

        // data to populate the RecyclerView with
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("12:00pm - 1:00pm");
        animalNames.add("01:00pm - 2:00pm");
        animalNames.add("02:00pm - 3:00pm");
        animalNames.add("03:00pm - 4:00pm");
        animalNames.add("04:00pm - 5:00pm");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.timeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, animalNames);
        myRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }

    public void divideTime(View view) {

        if ((startTime.getText().toString().contains("AM") || startTime.getText().toString().contains("PM")) && (endTime.getText().toString().contains("AM") || endTime.getText().toString().contains("PM"))) {

            String n = dividingNumber.getText().toString();

            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();

            LocalTime startT = LocalTime.parse(startTime.getText().toString(), dtf);
            LocalTime endT = LocalTime.parse(endTime.getText().toString(), dtf);

            ArrayList<String> timeSlots = new ArrayList<>();

            while (startT.isBefore(endT.minusMinutes(30))) {
                timeSlots.add(dtf.format(startT));
                startT = startT.plusMinutes(n);
            }
        }

    }
}