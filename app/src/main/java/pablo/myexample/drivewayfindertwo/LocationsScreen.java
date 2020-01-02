package pablo.myexample.drivewayfindertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pablo.myexample.drivewayfinder.MyRecyclerViewAdapterForTimes;
import pablo.myexample.drivewayfinder.R;
import pablo.myexample.drivewayfinder.TransferObjectInterface;

public class LocationsScreen extends AppCompatActivity implements MyRecyclerViewAdapterForTimes.ItemClickListener {

    private MyRecyclerViewAdapterForTimes myRecyclerViewAdapterForTimes;
    private Intent intent;
    private ArrayList<String> displayLocations;
    private ArrayList<RequestedOrAppointmentObject> requestedOrAppointmentObjectArrayList;
    private String driverId;
    private RecyclerView recyclerView;
    private Intent toDetailsOfReservation;

    private void deleteOldAppointmentDriver(final String timeSlot, final int position, final String date) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Delete old appointment?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                displayLocations.remove(position);
                myRecyclerViewAdapterForTimes.notifyDataSetChanged();
                FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Appointments").child(date).child(timeSlot).removeValue();
                Snackbar.make(findViewById(R.id.locationScreenRoot), "Deleted Old Appointment", Snackbar.LENGTH_LONG).show();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onItemClick(View view, int position) {

        RequestedOrAppointmentObject reservationInfo = requestedOrAppointmentObjectArrayList.get(position);

        //new below

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        int theYear = Integer.parseInt(reservationInfo.getDate().substring(0, 4));
        int theMonth = Integer.parseInt(reservationInfo.getDate().substring(5, 7));
        int theDay = Integer.parseInt(reservationInfo.getDate().substring(8, 10));

       /* boolean theYear = year >= Integer.parseInt(reservationInfo.getDate().substring(0, 4));
        boolean theMonth = month >= Integer.parseInt(reservationInfo.getDate().substring(5, 7));*/

        DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        LocalTime currentT = LocalTime.parse(currentTime, dtf);
        LocalTime endT = LocalTime.parse(reservationInfo.getTimeSlot().substring(11, 19), dtf);

        if (year <= theYear) {
            if (month < theMonth) {
                toDetailsOfReservation.putExtra("ownerId", reservationInfo.getOwnerId());
                toDetailsOfReservation.putExtra("url", reservationInfo.getImageUrl());
                toDetailsOfReservation.putExtra("location", reservationInfo.getLocation());
                toDetailsOfReservation.putExtra("rate", reservationInfo.getRate());
                toDetailsOfReservation.putExtra("time", reservationInfo.getTimeSlot());
                toDetailsOfReservation.putExtra("date", reservationInfo.getDate());
                toDetailsOfReservation.putExtra("ownerName", reservationInfo.getOwnerName());
                toDetailsOfReservation.putExtra("ownerPhone", reservationInfo.getOwnerPhoneNumber());
                startActivity(toDetailsOfReservation);
            } else if (month == theMonth) {
                if (day < theDay) {
                    String timeSlot = String.valueOf(reservationInfo.getTimeSlot());
                    deleteOldAppointmentDriver(timeSlot, position, intent.getStringExtra("checkRequested"));
                } else if (day == theDay) {
                    if (currentT.isBefore(endT)) {
                        toDetailsOfReservation.putExtra("ownerId", reservationInfo.getOwnerId());
                        toDetailsOfReservation.putExtra("url", reservationInfo.getImageUrl());
                        toDetailsOfReservation.putExtra("location", reservationInfo.getLocation());
                        toDetailsOfReservation.putExtra("rate", reservationInfo.getRate());
                        toDetailsOfReservation.putExtra("time", reservationInfo.getTimeSlot());
                        toDetailsOfReservation.putExtra("date", reservationInfo.getDate());
                        toDetailsOfReservation.putExtra("ownerName", reservationInfo.getOwnerName());
                        toDetailsOfReservation.putExtra("ownerPhone", reservationInfo.getOwnerPhoneNumber());
                        startActivity(toDetailsOfReservation);
                    } else {
                        String timeSlot = String.valueOf(reservationInfo.getTimeSlot());
                        deleteOldAppointmentDriver(timeSlot, position, intent.getStringExtra("checkRequested"));
                    }
                } else {
                    String timeSlot = String.valueOf(reservationInfo.getTimeSlot());
                    deleteOldAppointmentDriver(timeSlot, position, intent.getStringExtra("checkRequested"));
                }
            } else {
                String timeSlot = String.valueOf(reservationInfo.getTimeSlot());
                deleteOldAppointmentDriver(timeSlot, position, intent.getStringExtra("checkRequested"));
            }
        } else {
            String timeSlot = String.valueOf(reservationInfo.getTimeSlot());
            deleteOldAppointmentDriver(timeSlot, position, intent.getStringExtra("checkRequested"));
        }

      /*  if (theMonth && theYear) {
            if (currentT.isAfter(endT) && (Integer.parseInt(reservationInfo.getDate().substring(8, 10)) == day)) {
                String timeSlot = String.valueOf(reservationInfo.getTimeSlot());
                deleteOldAppointmentDriver(timeSlot, position, intent.getStringExtra("checkRequested"));
            } else if (currentT.isBefore(endT) && (Integer.parseInt(reservationInfo.getDate().substring(8, 10)) == day)) {
                //do nothing
            } else {
                String timeSlot = String.valueOf(reservationInfo.getTimeSlot());
                deleteOldAppointmentDriver(timeSlot, position, intent.getStringExtra("checkRequested"));
            }
        } else {

            //new above

            toDetailsOfReservation.putExtra("ownerId", reservationInfo.getOwnerId());
            toDetailsOfReservation.putExtra("url", reservationInfo.getImageUrl());
            toDetailsOfReservation.putExtra("location", reservationInfo.getLocation());
            toDetailsOfReservation.putExtra("rate", reservationInfo.getRate());
            toDetailsOfReservation.putExtra("time", reservationInfo.getTimeSlot());
            toDetailsOfReservation.putExtra("date", reservationInfo.getDate());
            toDetailsOfReservation.putExtra("ownerName", reservationInfo.getOwnerName());
            toDetailsOfReservation.putExtra("ownerPhone", reservationInfo.getOwnerPhoneNumber());
            startActivity(toDetailsOfReservation);

        }*/

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_screen);

        setTitle("Time Slots");

        toDetailsOfReservation = new Intent(this, ReservationDetailsForDriver.class);

        driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        displayLocations = new ArrayList<>();
        requestedOrAppointmentObjectArrayList = new ArrayList<>();

        intent = getIntent();

        if (intent.getStringExtra("checkRequested").matches("yes")) {
            //simply check all of requested
            toDetailsOfReservation.putExtra("checkRequested", intent.getStringExtra("checkRequested"));
            listenForRequested();
        } else {
            //find out how to listen for locations at specified date
            toDetailsOfReservation.putExtra("checkRequested", intent.getStringExtra("checkRequested"));
            listenForAppointments(intent.getStringExtra("checkRequested"));
        }

        recyclerView = findViewById(R.id.locationsScreenRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void listenForRequested() {
        DatabaseReference refRequested = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Requested");
        refRequested.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot date : dataSnapshot.getChildren()) {
                    for (DataSnapshot time : date.getChildren()) {
                        RequestedOrAppointmentObject requestedOrAppointmentObject = time.getValue(RequestedOrAppointmentObject.class);
                        displayLocations.add(requestedOrAppointmentObject.getLocation() + "\n" + requestedOrAppointmentObject.getTimeSlot());
                        requestedOrAppointmentObjectArrayList.add(requestedOrAppointmentObject);
                    }
                }
                myRecyclerViewAdapterForTimes = new MyRecyclerViewAdapterForTimes(getApplicationContext(), displayLocations);
                myRecyclerViewAdapterForTimes.setClickListener(LocationsScreen.this);
                recyclerView.setAdapter(myRecyclerViewAdapterForTimes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void listenForAppointments(String date) {
        DatabaseReference refAppointments = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Appointments").child(date);
        refAppointments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot time : dataSnapshot.getChildren()) {
                    RequestedOrAppointmentObject requestedOrAppointmentObject = time.getValue(RequestedOrAppointmentObject.class);
                    displayLocations.add(requestedOrAppointmentObject.getLocation() + "\n" + requestedOrAppointmentObject.getTimeSlot());
                    requestedOrAppointmentObjectArrayList.add(requestedOrAppointmentObject);
                }
                myRecyclerViewAdapterForTimes = new MyRecyclerViewAdapterForTimes(getApplicationContext(), displayLocations);
                myRecyclerViewAdapterForTimes.setClickListener(LocationsScreen.this);
                recyclerView.setAdapter(myRecyclerViewAdapterForTimes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
