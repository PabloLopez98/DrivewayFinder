package pablo.myexample.drivewayfindertwo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import pablo.myexample.drivewayfinder.R;

public class LocationsScreen extends AppCompatActivity implements MyRecyclerViewAdapterDriver.ItemClickListener{

    MyRecyclerViewAdapterDriver myRecyclerViewAdapterDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_screen);

        // data to populate the RecyclerView with
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("11223 Laurel Ave, Whittier CA");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.locationsScreenRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapterDriver = new MyRecyclerViewAdapterDriver(this, animalNames);
        myRecyclerViewAdapterDriver.setClickListener(this);
        recyclerView.setAdapter(myRecyclerViewAdapterDriver);
    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, ReservationDetailsForDriver.class);
        startActivity(intent);
    }
}
