package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
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

public class DateDetails extends AppCompatActivity implements CardDetailsRecyclerView.ItemClickListener, CardDetailsRecyclerViewTwo.ItemClickListener {

    private CardDetailsRecyclerView adapter;
    private CardDetailsRecyclerViewTwo adapterTwo;
    private TextView location, rate, date, name, isActive, phone;
    private ImageView image;
    private Intent intent;
    private ArrayList<String> timeSlots;
    private ArrayList<CardDetailsRecyclerViewObject> appointmentRows;
    private ArrayList<CardDetailsRecyclerViewObject> requestedRows;
    private TextView appointmentTextView, requestedTextView, timeSlotsTextView;
    private RecyclerView recyclerView, recyclerView2;
    private Intent intentTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_details);

        setTitle("Date Details");

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

        appointmentRows = new ArrayList<>();
        requestedRows = new ArrayList<>();
        timeSlots = intent.getStringArrayListExtra("timeSlotsArray");

        appointmentTextView = findViewById(R.id.appt);
        requestedTextView = findViewById(R.id.rqappt);
        timeSlotsTextView = findViewById(R.id.timeSlotsTextView);

        recyclerView = findViewById(R.id.carddetailsrecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView2 = findViewById(R.id.carddetailsrecyclerviewRequested);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        intentTo = new Intent(this, DriverProfile.class);

        fetchThoseBooked();

    }

    public void fetchThoseBooked() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(intent.getStringExtra("ownerId")).child("Appointments").child(intent.getStringExtra("date"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot timeSlot : dataSnapshot.getChildren()) {
                        RequestedOrAppointmentObject requestedOrAppointmentObject = timeSlot.getValue(RequestedOrAppointmentObject.class);
                        appointmentRows.add(new CardDetailsRecyclerViewObject(requestedOrAppointmentObject.getTimeSlot(), requestedOrAppointmentObject.getDriverName(), "Occupied"));
                        if (timeSlots.contains(requestedOrAppointmentObject.getTimeSlot())) {
                            timeSlots.remove(requestedOrAppointmentObject.getTimeSlot());
                        }
                    }
                }
                if (appointmentRows.isEmpty()) {
                    appointmentTextView.setText("No Appointments");
                }
                lookUpRequest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void lookUpRequest() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(intent.getStringExtra("ownerId")).child("Requested").child(intent.getStringExtra("date"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot timeSlot : dataSnapshot.getChildren()) {
                        RequestedOrAppointmentObject requestedOrAppointmentObject = timeSlot.getValue(RequestedOrAppointmentObject.class);
                        requestedRows.add(new CardDetailsRecyclerViewObject(requestedOrAppointmentObject.getTimeSlot(), requestedOrAppointmentObject.getDriverName(), "Requested"));
                        if (timeSlots.contains(requestedOrAppointmentObject.getTimeSlot())) {
                            timeSlots.remove(requestedOrAppointmentObject.getTimeSlot());
                        }
                    }
                }
                if (requestedRows.isEmpty()) {
                    requestedTextView.setText("No Request");
                }
                showRemainingSlots();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void showRemainingSlots() {
        if (!timeSlots.isEmpty()) {
            String s = "";
            for (int i = 0; i < timeSlots.size(); i++) {
                s = s + timeSlots.get(i) + "\n";
            }
            timeSlotsTextView.setText(s);
        } else {
            timeSlotsTextView.setText("No time slots available");
        }
        setBothAdapters();
    }

    public void setBothAdapters() {

        adapter = new CardDetailsRecyclerView(this, appointmentRows);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        adapterTwo = new CardDetailsRecyclerViewTwo(this, requestedRows);
        adapterTwo.setClickListener(this);
        recyclerView2.setAdapter(adapterTwo);

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

        Log.i("onItemClick","Yes");

        intentTo.putExtra("requested", "no");
        intentTo.putExtra("date", date.getText().toString());
        intentTo.putExtra("time", appointmentRows.get(position).getTime());

        startActivity(intentTo);

    }

    @Override
    public void onItemClickTwo(View view, int position) {

        intentTo.putExtra("requested", "yes");
        intentTo.putExtra("date", date.getText().toString());
        intentTo.putExtra("time", requestedRows.get(position).getTime());

        startActivity(intentTo);

    }
}
