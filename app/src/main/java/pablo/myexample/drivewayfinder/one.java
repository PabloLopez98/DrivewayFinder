package pablo.myexample.drivewayfinder;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


/*
Summary:

one.java represents the fragment where owners see their scheduled spots open for rental.
 */

public class one extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {

    private MyRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<String> spots;
    private ArrayList<SpotObjectClass> spotObjects;
    private TransferObjectInterface listener;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof TransferObjectInterface) {

            listener = (TransferObjectInterface) context;
        }
    }

    @Override
    public void onDetach() {

        listener = null;
        super.onDetach();
    }

    @Override
    public void onItemClick(View view, int position) {

        SpotObjectClass spotObject = spotObjects.get(position);
        listener.transferSpotObject(spotObject);
        ((OwnerActivity) getActivity()).toDateDetails();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_one, container, false);
        spotObjects = new ArrayList<>();
        spots = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        populateFragmentOne();

        return view;
    }

    public void populateFragmentOne() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(userId).child("Spots");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        SpotObjectClass spotObject = ds.getValue(SpotObjectClass.class);
                        spotObjects.add(spotObject);
                        String[] date = spotObject.getDate().split(" ");
                        String dateShow = date[0] + " " + date[1] + " " + date[2];
                        spots.add(dateShow);
                    }
                    adapter = new MyRecyclerViewAdapter(getContext(), spots);
                    adapter.setClickListener(one.this);
                    recyclerView.setAdapter(adapter);

                    //hide progress circle, show layout
                    view.findViewById(R.id.fragonecircle).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.fragonelayout).setVisibility(View.VISIBLE);
                } else {

                    //hide progress circle, show layout
                    view.findViewById(R.id.fragonecircle).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.fragonelayout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

}
