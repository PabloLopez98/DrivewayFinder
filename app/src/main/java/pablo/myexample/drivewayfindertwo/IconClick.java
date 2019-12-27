package pablo.myexample.drivewayfindertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Driver;
import java.util.ArrayList;

import pablo.myexample.drivewayfinder.MainActivity;
import pablo.myexample.drivewayfinder.R;

public class IconClick extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Intent intent;
    private TextView date, location, name, phone, rate;
    private ImageView imageView;
    private Spinner spinner;
    private ArrayList<String> arrayList;
    private String selectedTimeSlot;
    private String ownerId, driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_click);

        setTitle("Reservation Preview");

        intent = getIntent();

        driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ownerId = intent.getStringExtra("id");
        date = findViewById(R.id.iconClickDate);
        date.setText(intent.getStringExtra("date"));
        location = findViewById(R.id.iconClickLocation);
        location.setText(intent.getStringExtra("location"));
        name = findViewById(R.id.iconClickName);
        name.setText(intent.getStringExtra("name"));
        phone = findViewById(R.id.iconClickPhone);
        phone.setText(intent.getStringExtra("phone"));
        rate = findViewById(R.id.iconClickRate);
        rate.setText(intent.getStringExtra("rate"));
        imageView = findViewById(R.id.iconClickImage);
        Picasso.get().load(intent.getStringExtra("url")).into(imageView);

        retrieveTimeSlotsFromFirebase();

    }

    //fill spinner with time slots that are available
    public void retrieveTimeSlotsFromFirebase() {

        arrayList = intent.getStringArrayListExtra("slots");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerId).child("Appointments").child(date.getText().toString());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot TimeSlot : dataSnapshot.getChildren()) {
                        String time = TimeSlot.getKey();
                        if (arrayList.contains(time)) {
                            arrayList.remove(time);
                        }
                    }
                }

                final DatabaseReference databaseReferenceTwo = FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerId).child("Requested").child(date.getText().toString());
                databaseReferenceTwo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot TimeSlot : dataSnapshot.getChildren()) {
                                String time = TimeSlot.getKey();
                                if (arrayList.contains(time)) {
                                    arrayList.remove(time);
                                }
                            }
                        }

                        spinner = findViewById(R.id.iconClickSpinner);
                        spinner.setOnItemSelectedListener(IconClick.this);
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(IconClick.this, android.R.layout.simple_spinner_item, arrayList);
                        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //store the selected time slot after clicking spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedTimeSlot = arrayList.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void cancelButtonPressed(View view) {
        finish();
    }

    public void requestAppointment(View view) {

        DatabaseReference getDriverProfile = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("ProfileInfo");
        getDriverProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DriverProfileObject driverProfileObject = dataSnapshot.getValue(DriverProfileObject.class);

                //Create Requested Object to be used both in : requested and appointment for both (driver and owner)
                RequestedOrAppointmentObject requestedOrAppointmentObject = new RequestedOrAppointmentObject(date.getText().toString(), intent.getStringExtra("location"), intent.getStringExtra("url"), intent.getStringExtra("name"), ownerId, intent.getStringExtra("phone"), intent.getStringExtra("rate"), driverProfileObject.getDriverName(), driverProfileObject.getDriverCarModel(), driverProfileObject.getDriverLicensePlates(), driverProfileObject.getDriverPhoneNumber(), driverId, selectedTimeSlot);

                //for owner
                DatabaseReference ownerRef = FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerId).child("Requested").child(date.getText().toString()).child(selectedTimeSlot);
                ownerRef.setValue(requestedOrAppointmentObject);

                //for driver
                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Requested").child(date.getText().toString()).child(selectedTimeSlot);
                driverRef.setValue(requestedOrAppointmentObject);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        new SendPush().sendFCMPush(IconClick.this, "Owners", ownerId, date.getText().toString() + " | " + selectedTimeSlot, "Request For:");

        Snackbar.make(findViewById(R.id.iconClickRoot), "Successfully Requested Driveway Opening!", Snackbar.LENGTH_LONG).show();
        final Intent toTDA = new Intent(getApplicationContext(), TheDriverActivity.class);
        toTDA.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3500);
                    startActivity(toTDA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

}
