package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        Picasso.get().load(intent.getStringExtra("imageUrl")).into(image, new Callback() {
            @Override
            public void onSuccess() {
                //hide progress circle, show layout
                findViewById(R.id.datedetailsCircle).setVisibility(View.INVISIBLE);
                findViewById(R.id.datedetailsscrollview).setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {

            }
        });

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
        //isActive = findViewById(R.id.dateDetailsIsActive);
        //isActive.setText(intent.getStringExtra("isActive"));

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
                deleteEmptyDate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteEmptyDate() {
        if (appointmentTextView.getText().toString().matches("No Appointments") && requestedTextView.getText().toString().matches("No Request")) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Are you sure you want to delete this driveway opening?");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String spotDate = intent.getStringExtra("date");
                    DatabaseReference deleteEmptyDateRef = FirebaseDatabase.getInstance().getReference().child("Owners").child(ownerId).child("Spots").child(spotDate);
                    deleteEmptyDateRef.removeValue();

                    Snackbar.make(findViewById(R.id.datedetailsrootlayout), "Deleted Opening", Snackbar.LENGTH_LONG).show();
                    final Intent intent = new Intent(getApplicationContext(), OwnerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3500); //As I am using LENGTH_LONG in Toast
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }
            });
            alertDialog.show();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Driveway opening is still in use.");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Dismiss", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    public void deleteOldAppointment(final String timeSlot, final int position) {
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
                appointmentRows.remove(position);
                adapter.notifyDataSetChanged();
                FirebaseDatabase.getInstance().getReference().child("Owners").child(intent.getStringExtra("ownerId")).child("Appointments").child(date.getText().toString()).child(timeSlot).removeValue();
                Snackbar.make(findViewById(R.id.datedetailsrootlayout), "Deleted Old Appointment", Snackbar.LENGTH_LONG).show();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onItemClick(View view, int position) {

        CardDetailsRecyclerViewObject cardDetailsRecyclerViewObject = appointmentRows.get(position);

        //new below

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        boolean theYear = (year >= Integer.parseInt(date.getText().toString().substring(0, 4)));
        boolean theMonth = (month >= Integer.parseInt(date.getText().toString().substring(5, 7)));

        DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        LocalTime currentT = LocalTime.parse(currentTime, dtf);
        LocalTime endT = LocalTime.parse(cardDetailsRecyclerViewObject.getTime().substring(11, 19), dtf);

        //if on or after appointment date
        if (theMonth && theYear) {
            //if current date is same day as appointment and after appt time
            if (currentT.isAfter(endT) && (Integer.parseInt(date.getText().toString().substring(8, 10)) == day)) {
                String timeSlot = String.valueOf(appointmentRows.get(position).getTime());
                deleteOldAppointment(timeSlot, position);
            } else if (currentT.isBefore(endT) && (Integer.parseInt(date.getText().toString().substring(8, 10)) == day)) {
            } else {
                String timeSlot = String.valueOf(appointmentRows.get(position).getTime());
                deleteOldAppointment(timeSlot, position);
            }
        } else {

            //new above

            intentTo.putExtra("requested", "no");
            intentTo.putExtra("date", date.getText().toString());
            intentTo.putExtra("time", appointmentRows.get(position).getTime());
            startActivity(intentTo);

        }

    }

    @Override
    public void onItemClickTwo(View view, int position) {

        intentTo.putExtra("requested", "yes");
        intentTo.putExtra("date", date.getText().toString());
        intentTo.putExtra("time", requestedRows.get(position).getTime());

        startActivity(intentTo);

    }
}
