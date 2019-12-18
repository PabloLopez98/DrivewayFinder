package pablo.myexample.drivewayfindertwo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import pablo.myexample.drivewayfinder.OwnerActivity;
import pablo.myexample.drivewayfinder.R;

public class ReservationDetailsForDriver extends AppCompatActivity {

    private Intent intent;
    private Intent backTODisplayLocations;
    private ImageView image;
    private TextView location, rate, time, date, ownerName, ownerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_details_for_driver);

        setTitle("Reservation Details");

        //for getting info
        intent = getIntent();
        //for going back
        backTODisplayLocations = new Intent(this, LocationsScreen.class);
        backTODisplayLocations.putExtra("checkRequested", intent.getStringExtra("checkRequested"));
        //for showing info
        image = findViewById(R.id.imageDetail);
        Picasso.get().load(intent.getStringExtra("url")).into(image, new Callback() {
            @Override
            public void onSuccess() {
                //hide progress circle, show layout
                findViewById(R.id.resdetfordrivercircle).setVisibility(View.INVISIBLE);
                findViewById(R.id.resdetfordriverlayout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        location = findViewById(R.id.locationDetail);
        location.setText(intent.getStringExtra("location"));
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] strings = location.getText().toString().split(" ");
                String address = strings[0];
                String street = strings[1] + " " + strings[2];
                String addyStrt = address + " " + street;
                String city = strings[3];
                String state = strings[4];
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + addyStrt + " " + city + " " + state);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        rate = findViewById(R.id.rateDetail);
        rate.setText(intent.getStringExtra("rate"));
        time = findViewById(R.id.timeDetail);
        time.setText(intent.getStringExtra("time"));
        date = findViewById(R.id.dateDetail);
        date.setText(intent.getStringExtra("date"));
        ownerName = findViewById(R.id.ownerNameDetail);
        ownerName.setText(intent.getStringExtra("ownerName"));
        ownerPhone = findViewById(R.id.ownerPhoneDetail);
        ownerPhone.setText(intent.getStringExtra("ownerPhone"));
        ownerPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + ownerPhone.getText().toString()));
                startActivity(intent);
            }
        });

        //if 'yes' requested
        if (intent.getStringExtra("checkRequested").matches("yes")) {
            Button cancelButton = findViewById(R.id.cancelRequestButton);
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelRequest();
                }
            });
        }
    }

    private void cancelRequest() {

        String ownerId = intent.getStringExtra("ownerId");
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String dte = date.getText().toString();
        String tme = time.getText().toString();

        DatabaseReference deleteRequestedForOwner = FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerId).child("Requested").child(dte).child(tme);
        deleteRequestedForOwner.removeValue();

        DatabaseReference deleteRequestedForDriver = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Requested").child(dte).child(tme);
        deleteRequestedForDriver.removeValue();

        Snackbar.make(findViewById(R.id.resdetfordriverlayout), "Cancelled Request", Snackbar.LENGTH_LONG).show();

        final Intent intentToHome = new Intent(this, OwnerActivity.class);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(backTODisplayLocations);
        return true;
    }

}