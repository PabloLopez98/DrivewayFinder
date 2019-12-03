package pablo.myexample.drivewayfindertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
import java.util.Date;
import java.util.Locale;

import pablo.myexample.drivewayfinder.R;
import pablo.myexample.drivewayfinder.TransferObjectInterface;

public class LocationsScreen extends AppCompatActivity implements MyRecyclerViewAdapterDriver.ItemClickListener {

    private MyRecyclerViewAdapterDriver myRecyclerViewAdapterDriver;
    private Intent intent;
    private ArrayList<String> displayLocations;
    private ArrayList<RequestedOrAppointmentObject> requestedOrAppointmentObjectArrayList;
    private String driverId;
    private RecyclerView recyclerView;
    private Intent toDetailsOfReservation;

    @Override
    public void onItemClick(View view, int position) {

        //new below
        RequestedOrAppointmentObject reservationInfo = requestedOrAppointmentObjectArrayList.get(position);

        DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        LocalTime currentT = LocalTime.parse(currentTime, dtf);
        LocalTime endT = LocalTime.parse(reservationInfo.getTimeSlot().substring(11, 19), dtf);

        if (currentT.isAfter(endT)) {

            displayLocations.remove(position);
            myRecyclerViewAdapterDriver.notifyDataSetChanged();

        } else {

            //new above

            toDetailsOfReservation.putExtra("url", reservationInfo.getImageUrl());
            toDetailsOfReservation.putExtra("location", reservationInfo.getLocation());
            toDetailsOfReservation.putExtra("rate", reservationInfo.getRate());
            toDetailsOfReservation.putExtra("time", reservationInfo.getTimeSlot());
            toDetailsOfReservation.putExtra("date", reservationInfo.getDate());
            toDetailsOfReservation.putExtra("ownerName", reservationInfo.getOwnerName());
            toDetailsOfReservation.putExtra("ownerPhone", reservationInfo.getOwnerPhoneNumber());
            startActivity(toDetailsOfReservation);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_screen);

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
                myRecyclerViewAdapterDriver = new MyRecyclerViewAdapterDriver(getApplicationContext(), displayLocations);
                myRecyclerViewAdapterDriver.setClickListener(LocationsScreen.this);
                recyclerView.setAdapter(myRecyclerViewAdapterDriver);
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
                myRecyclerViewAdapterDriver = new MyRecyclerViewAdapterDriver(getApplicationContext(), displayLocations);
                myRecyclerViewAdapterDriver.setClickListener(LocationsScreen.this);
                recyclerView.setAdapter(myRecyclerViewAdapterDriver);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
