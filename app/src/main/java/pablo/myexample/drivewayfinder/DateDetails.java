package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DateDetails extends AppCompatActivity implements CardDetailsRecyclerView.ItemClickListener {

    private CardDetailsRecyclerView cardDetailsRecyclerView;
    private TextView location, rate, date, name, isActive, phone;
    private ImageView image;
    private Intent intent;
    private ArrayList<String> timeSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_details);

        intent = getIntent();

        image = findViewById(R.id.dateDetailsImage);
        Picasso.get().load(intent.getStringExtra("imageUrl")).into(image);
        location = findViewById(R.id.dateDetailsLocation);
        location.setText(intent.getStringExtra("location"));
        rate = findViewById(R.id.dateDetailsRate);
        rate.setText(intent.getStringExtra("rate"));
        date = findViewById(R.id.dateDetailsDate);
        date.setText(intent.getStringExtra("date"));
        name = findViewById(R.id.dateDetailsName);
        name.setText(intent.getStringExtra("name"));
        phone = findViewById(R.id.dateDetailsPhone);
        phone.setText(intent.getStringExtra("phone"));
        isActive = findViewById(R.id.dateDetailsIsActive);
        isActive.setText(intent.getStringExtra("isActive"));

        //rows for each client
        ArrayList<CardDetailsRecyclerViewObject> rows = new ArrayList<>();
        //to add time slot into each row of 'rows'
        timeSlots = intent.getStringArrayListExtra("timeSlotsArray");


        /*
            I left off here before going to driver side.
            I need to finish the driver side until the point of requesting an appointment,
            in order for me to finish this part of the owner side. Plus I need to finsih driver side,
            in order to even start the second fragment of current activity.
         */
        //before setting up recyclerview, search for requests and those already booked
        //lookUpRequest();
        //fetchThoseBooked();


        for (int i = 0; i < timeSlots.size(); i++) {
            CardDetailsRecyclerViewObject cardDetailsRecyclerViewObject = new CardDetailsRecyclerViewObject(timeSlots.get(i), "No Client", "Unoccupied");
            rows.add(cardDetailsRecyclerViewObject);
        }
        RecyclerView recyclerView = findViewById(R.id.carddetailsrecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardDetailsRecyclerView = new CardDetailsRecyclerView(this, rows);
        cardDetailsRecyclerView.setClickListener(this);
        recyclerView.setAdapter(cardDetailsRecyclerView);

    }

    public void lookUpRequest() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(intent.getStringExtra("ownerId")).child("Requests").child(intent.getStringExtra("date"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //loop through children etc.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void fetchThoseBooked(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                Intent intent = new Intent(this, OwnerActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, DriverProfile.class);
        startActivity(intent);
    }
}
