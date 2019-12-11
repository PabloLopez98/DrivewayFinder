package pablo.myexample.drivewayfindertwo;

import android.content.Context;
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
import pablo.myexample.drivewayfinder.OwnerActivity;
import pablo.myexample.drivewayfinder.R;
import pablo.myexample.drivewayfinder.SpotObjectClass;
import pablo.myexample.drivewayfinder.TransferObjectInterface;

public class fourdriver extends Fragment implements MyRecyclerViewAdapterDriver.ItemClickListener {

    private MyRecyclerViewAdapterDriver myRecyclerViewAdapterDriver;
    private RecyclerView recyclerView;
    private String driverId;
    private ArrayList<String> arrayListAppointmentsDatesOrRequested;
    private View view;

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getContext(), LocationsScreen.class);
        if (arrayListAppointmentsDatesOrRequested.get(position).matches("Requested Appointments")) {
            //when reaching the other side, it will only check the requested info
            intent.putExtra("checkRequested", "yes");
            startActivity(intent);
        } else {
            //pass date 'yyyy mm dd', on other side, it will check only specified date
            intent.putExtra("checkRequested", arrayListAppointmentsDatesOrRequested.get(position));
            startActivity(intent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fourdriver, container, false);

        driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = view.findViewById(R.id.recyclerViewForFour);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        retrieveAppointmentDates();

        return view;

    }

    public void retrieveAppointmentDates() {

        arrayListAppointmentsDatesOrRequested = new ArrayList<>();

        //if an appointment exists, store it in the displaying arrayList 'arrayListAppointmentsDatesOrRequested'
        DatabaseReference driverAppointmentDates = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Appointments");
        driverAppointmentDates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot DateObj : dataSnapshot.getChildren()) {
                        if (DateObj.exists()) {
                            arrayListAppointmentsDatesOrRequested.add(DateObj.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        retrieveRequested(arrayListAppointmentsDatesOrRequested);
    }

    public void retrieveRequested(final ArrayList<String> arrayListAppointmentsDatesOrRequested) {

        //if a request or request(s) exists than we can add it to the displaying arrayList 'arrayListAppointmentsDatesOrRequested'
        DatabaseReference driverRequestedDates = FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Requested");
        driverRequestedDates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dateObj : dataSnapshot.getChildren()) {
                        if (dateObj.exists()) {
                            arrayListAppointmentsDatesOrRequested.add("Requested Appointments");
                            break;
                        }
                    }
                    myRecyclerViewAdapterDriver = new MyRecyclerViewAdapterDriver(getContext(), arrayListAppointmentsDatesOrRequested);
                    myRecyclerViewAdapterDriver.setClickListener(fourdriver.this);
                    recyclerView.setAdapter(myRecyclerViewAdapterDriver);
                } else {
                    myRecyclerViewAdapterDriver = new MyRecyclerViewAdapterDriver(getContext(), arrayListAppointmentsDatesOrRequested);
                    myRecyclerViewAdapterDriver.setClickListener(fourdriver.this);
                    recyclerView.setAdapter(myRecyclerViewAdapterDriver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //hide progress circle, show layout
        view.findViewById(R.id.theCircleInFragFour).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.recyclerViewForFour).setVisibility(View.VISIBLE);
    }

}
