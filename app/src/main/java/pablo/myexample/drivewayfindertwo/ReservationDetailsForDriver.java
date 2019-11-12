package pablo.myexample.drivewayfindertwo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import pablo.myexample.drivewayfinder.R;

public class ReservationDetailsForDriver extends AppCompatActivity {

    private Intent intent;
    private Intent backTODisplayLocations;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_details_for_driver);

        //for getting info
        intent = getIntent();
        //for going back
        backTODisplayLocations = new Intent(this, LocationsScreen.class);
        backTODisplayLocations.putExtra("checkRequested", intent.getStringExtra("checkRequested"));
        //for showing info
        image = findViewById(R.id.imageDetail);
        Picasso.get().load(intent.getStringExtra("url")).into(image);


/*
        toDetailsOfReservation.putExtra("url", reservationInfo.getImageUrl());
        toDetailsOfReservation.putExtra("location", reservationInfo.getLocation());
        toDetailsOfReservation.putExtra("rate", reservationInfo.getRate());
        toDetailsOfReservation.putExtra("time", reservationInfo.getTimeSlot());
        toDetailsOfReservation.putExtra("date", reservationInfo.getDate());
        toDetailsOfReservation.putExtra("ownerName", reservationInfo.getOwnerName());
        toDetailsOfReservation.putExtra("ownerPhone", reservationInfo.getOwnerPhoneNumber());
 */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(backTODisplayLocations);
        return true;
    }

}