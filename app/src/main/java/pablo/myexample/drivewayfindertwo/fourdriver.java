package pablo.myexample.drivewayfindertwo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pablo.myexample.drivewayfinder.DateDetails;
import pablo.myexample.drivewayfinder.R;

//This class is for displaying cards.
//This is shown in the second option of the bottom navigation menu.
public class fourdriver extends Fragment implements MyRecyclerViewAdapterDriver.ItemClickListener {

    MyRecyclerViewAdapterDriver myRecyclerViewAdapterDriver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fourdriver, container, false);

        // data to populate the RecyclerView with
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("July 99, 2019");
        animalNames.add("July 21, 2019");
        animalNames.add("July 31, 2019");

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewForFour);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myRecyclerViewAdapterDriver = new MyRecyclerViewAdapterDriver(getContext(), animalNames);
        myRecyclerViewAdapterDriver.setClickListener(this);
        recyclerView.setAdapter(myRecyclerViewAdapterDriver);

        return view;

    }

    @Override
    public void onItemClick(View view, int position) {

        Intent intent = new Intent(getContext(), LocationsScreen.class);
        startActivity(intent);

    }

}
