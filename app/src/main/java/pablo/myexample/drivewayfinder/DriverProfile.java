package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Driver;

import pablo.myexample.drivewayfindertwo.IconClick;
import pablo.myexample.drivewayfindertwo.RequestedOrAppointmentObject;
import pablo.myexample.drivewayfindertwo.SendPush;

public class DriverProfile extends AppCompatActivity {

    private Intent intent;
    private String result, date, time;
    private TextView name, phone, plate, model;
    private String ownerId, driverId;
    private Intent intentToHome;
    private RequestedOrAppointmentObject requestedOrAppointmentObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        setTitle("Client");

        intentToHome = new Intent(this, OwnerActivity.class);
        intentToHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent = getIntent();

        result = intent.getStringExtra("requested");

        /*
         if its an appointment,
         then hide 'deny' button,
         and change 'accept' button to 'cancel appointment' button
        */
        if (result.matches("no")) {
            //findViewById(R.id.dpacceptbutton).setVisibility(View.GONE);
            Button button = findViewById(R.id.dpacceptbutton);
            button.setText("End Appointment");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    endAppointment();
                }
            });
            findViewById(R.id.dpdenybutton).setVisibility(View.GONE);
        } else {
            findViewById(R.id.dpacceptbutton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptDriver();
                }
            });
            findViewById(R.id.dpdenybutton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    denyDriver();
                }
            });
        }

        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");

        name = findViewById(R.id.dpname);
        phone = findViewById(R.id.dpphone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone.getText().toString()));
                startActivity(intent);
            }
        });
        plate = findViewById(R.id.dpplate);
        model = findViewById(R.id.dpmodel);

        ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        retrieveDriverInfo();

    }

    private void endAppointment() {

        DatabaseReference deleteRequestedForOwner = FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerId).child("Appointments").child(date).child(time);
        deleteRequestedForOwner.removeValue();

        DatabaseReference deleteRequestedForDriver = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Appointments").child(date).child(time);
        deleteRequestedForDriver.removeValue();

        Snackbar.make(findViewById(R.id.driverprofileroot), "Ended Appointment", Snackbar.LENGTH_LONG).show();

        new SendPush().sendFCMPush(DriverProfile.this, "Drivers", driverId, date + " | " + time, "Appointment Ended For:");

        final Intent intentToHome = new Intent(getApplicationContext(), OwnerActivity.class);
        intentToHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3500);
                    startActivity(intentToHome);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    public void retrieveDriverInfo() {

        DatabaseReference databaseReference;

        if (result.matches("yes")) {

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerId).child("Requested").child(date).child(time);

        } else {

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerId).child("Appointments").child(date).child(time);

        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                requestedOrAppointmentObject = dataSnapshot.getValue(RequestedOrAppointmentObject.class);
                name.setText(requestedOrAppointmentObject.getDriverName());
                phone.setText(requestedOrAppointmentObject.getDriverPhoneNumber());
                plate.setText(requestedOrAppointmentObject.getDriverLicensePlates());
                model.setText(requestedOrAppointmentObject.getDriverCarModel());
                driverId = requestedOrAppointmentObject.getDriverId();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void acceptDriver() {

        //set appointment value for owner and driver

        DatabaseReference setAppointmentsForOwner = FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerId).child("Appointments").child(date).child(time);
        setAppointmentsForOwner.setValue(requestedOrAppointmentObject);

        DatabaseReference setAppointmentsForDriver = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Appointments").child(date).child(time);
        setAppointmentsForDriver.setValue(requestedOrAppointmentObject);

        //delete requested value for owner and driver

        DatabaseReference deleteRequestedForOwner = FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerId).child("Requested").child(date).child(time);
        deleteRequestedForOwner.removeValue();

        DatabaseReference deleteRequestedForDriver = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Requested").child(date).child(time);
        deleteRequestedForDriver.removeValue();

        Snackbar.make(findViewById(R.id.driverprofileroot), "Successfully Accepted Driver!", Snackbar.LENGTH_LONG).show();

        new SendPush().sendFCMPush(DriverProfile.this, "Drivers", driverId, date + " | " + time, "Request Accepted For:");

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3500);
                    startActivity(intentToHome);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    public void denyDriver() {

        //delete requested value for owner and driver

        DatabaseReference deleteRequestedForOwner = FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerId).child("Requested").child(date).child(time);
        deleteRequestedForOwner.removeValue();

        DatabaseReference deleteRequestedForDriver = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Requested").child(date).child(time);
        deleteRequestedForDriver.removeValue();

        Snackbar.make(findViewById(R.id.driverprofileroot), "Denied Driver", Snackbar.LENGTH_LONG).show();

        new SendPush().sendFCMPush(DriverProfile.this, "Drivers", driverId, date + " | " + time, "Request Denied For:");

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3500);
                    startActivity(intentToHome);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
