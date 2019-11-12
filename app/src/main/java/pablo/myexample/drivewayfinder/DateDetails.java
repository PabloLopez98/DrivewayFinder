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

import pablo.myexample.drivewayfindertwo.RequestedOrAppointmentObject;

//I am going to have two different recyclerviews one for appointments and another for requested
public class DateDetails extends AppCompatActivity implements CardDetailsRecyclerView.ItemClickListener {

    private CardDetailsRecyclerView cardDetailsRecyclerView;
    private TextView location, rate, date, name, isActive, phone;
    private ImageView image;
    private Intent intent;
    private ArrayList<String> timeSlots;
    private ArrayList<RequestedOrAppointmentObject> requestedOrAppointmentObjectArrayList;
    private ArrayList<CardDetailsRecyclerViewObject> rows;

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

        //arraylist of rows
        rows = new ArrayList<>();
        //arraylist of times
        timeSlots = intent.getStringArrayListExtra("timeSlotsArray");
        //before setting up recyclerview, search for requests and those already booked
        requestedOrAppointmentObjectArrayList = new ArrayList<>();
        //lookUpRequest();
        //fetchThoseBooked();

        //add individual timeSlots.get(i), nameSlots.get(i), statusSlots.get(i) into each row
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(intent.getStringExtra("ownerId")).child("Requested").child(intent.getStringExtra("date"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot timeSlot : dataSnapshot.getChildren()) {
                        RequestedOrAppointmentObject requestedOrAppointmentObject = timeSlot.getValue(RequestedOrAppointmentObject.class);
                        requestedOrAppointmentObjectArrayList.add(requestedOrAppointmentObject);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void fetchThoseBooked() {//in Appointments

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
