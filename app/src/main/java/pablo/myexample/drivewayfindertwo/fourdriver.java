package pablo.myexample.drivewayfindertwo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pablo.myexample.drivewayfinder.DateDetails;
import pablo.myexample.drivewayfinder.R;

public class fourdriver extends Fragment implements MyRecyclerViewAdapterDriver.ItemClickListener {

    private MyRecyclerViewAdapterDriver myRecyclerViewAdapterDriver;
    private RecyclerView recyclerView;
    private String driverId;
    private ArrayList<String> arrayListAppointmentsDatesOrRequested;

    @Override
    public void onItemClick(View view, int position) {

        Intent intent = new Intent(getContext(), LocationsScreen.class);
        startActivity(intent);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fourdriver, container, false);

        driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = view.findViewById(R.id.recyclerViewForFour);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arrayListAppointmentsDatesOrRequested = new ArrayList<>();

        retrieveAppointmentDates();

        return view;

    }

    public void retrieveAppointmentDates() {

        //if an appointment exists, store it in the displaying arrayList 'arrayListAppointmentsDatesOrRequested'
        DatabaseReference driverAppointmentDates = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Appointments");
        driverAppointmentDates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot DateObj : dataSnapshot.getChildren()) {
                        if (DateObj.exists()) {
                            arrayListAppointmentsDatesOrRequested.add(DateObj.getKey());
                        } else {
                            break;
                        }
                    }
                }

                //inside ondatachange

                //if a request or request(s) exists than we can add it to the displaying arrayList 'arrayListAppointmentsDatesOrRequested'
                DatabaseReference driverRequestedDates = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Requested");
                driverRequestedDates.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dateObj : dataSnapshot.getChildren()) {
                                if (dateObj.exists()) {
                                    arrayListAppointmentsDatesOrRequested.add("Requested Appointment(s)");
                                    Log.i("Requests", String.valueOf(arrayListAppointmentsDatesOrRequested));

                                    myRecyclerViewAdapterDriver = new MyRecyclerViewAdapterDriver(getContext(), arrayListAppointmentsDatesOrRequested);
                                    recyclerView.setAdapter(myRecyclerViewAdapterDriver);

                                   /* myRecyclerViewAdapterDriver = new MyRecyclerViewAdapterDriver(getContext(), arrayListAppointmentsDatesOrRequested);
                                    myRecyclerViewAdapterDriver.setClickListener(fourdriver.this);
                                    recyclerView.setAdapter(myRecyclerViewAdapterDriver);*/
                                    break;
                                } else {
                                    myRecyclerViewAdapterDriver = new MyRecyclerViewAdapterDriver(getContext(), arrayListAppointmentsDatesOrRequested);
                                    recyclerView.setAdapter(myRecyclerViewAdapterDriver);

                                   /* Log.i("RequestsAgain", arrayListAppointmentsDatesOrRequested.toString());
                                    myRecyclerViewAdapterDriver = new MyRecyclerViewAdapterDriver(getContext(), arrayListAppointmentsDatesOrRequested);
                                    myRecyclerViewAdapterDriver.setClickListener(fourdriver.this);
                                    recyclerView.setAdapter(myRecyclerViewAdapterDriver);*/
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //inside ondatachange

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //adapter set here


    }

}
