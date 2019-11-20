package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    private String userId;

    @Override
    public void onItemClick(View view, final int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Delete time slot: " + myRecyclerViewAdapter.getItem(position) + "?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timeSlots.remove(position);
                myRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
        alertDialog.show();
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
                        if (hourOfDay < 10) {
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
                        if (hourOfDay < 10) {
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

            String[] stringChunk = ownerProfileObject.getDrivewayLocation().split(" ");
            String state = stringChunk[4];
            String city = stringChunk[3].replace(",", "");

            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(userId).child("Spots").child(chosenDate);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //date already exists
                    if (dataSnapshot.exists()) {
                        Toast.makeText(getApplicationContext(), "The chosen date is already in place!", Toast.LENGTH_LONG).show();
                    }
                    //date doesn't exists, so add it
                    else {
                        SpotObjectClass spotObject = new SpotObjectClass(timeSlots, userId, ownerProfileObject.getFullName(), ownerProfileObject.getPhoneNumber(), ownerProfileObject.getDrivewayLocation(), ownerProfileObject.getDrivwayImageUrl(), rate.getText().toString(), chosenDate, "Inactive");
                        databaseReference.setValue(spotObject);
                        //go back to owner activity and erase back stacks
                        Intent intent = new Intent(getApplicationContext(), OwnerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_date);

        //change status bar color to dark
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));

        setTitle("Add A Driveway Opening");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        displayInfo();

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                int newMonth = month + 1;
                chosenDate = year + " " + newMonth + " " + dayOfMonth;
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
        recyclerView.setHasFixedSize(false);
        //allows recyclerview to expand completely
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(AddDate.this, timeSlots);
        myRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }

    public void divideTime(View view) {
        if ((startTime.getText().toString().contains("AM") || startTime.getText().toString().contains("PM")) && (endTime.getText().toString().contains("AM") || endTime.getText().toString().contains("PM"))) {
            try {
                int n = Integer.valueOf(dividingNumber.getText().toString());//30
                DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
                LocalTime startT = LocalTime.parse(startTime.getText().toString(), dtf);
                Log.i("startT", String.valueOf(startT));
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

    public void displayInfo() {
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
                //display name
                TextView name = findViewById(R.id.drivewayNameShown);
                String prename = name.getText().toString();
                String thename = ownerProfileObject.getFullName();
                String togethername = prename + " " + thename;
                name.setText(togethername);
                //display phoneNumber
                TextView phoneNumber = findViewById(R.id.drivewayPhoneNumberShown);
                String prePhone = phoneNumber.getText().toString();
                String phone = ownerProfileObject.getPhoneNumber();
                String togetherPhone = prePhone + " " + phone;
                phoneNumber.setText(togetherPhone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}