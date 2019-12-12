package pablo.myexample.drivewayfindertwo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(backTODisplayLocations);
        return true;
    }

}