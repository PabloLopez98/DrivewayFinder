package pablo.myexample.drivewayfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class FinalizeDate extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter myRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize_date);

        // data to populate the RecyclerView with
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("11am - 12pm");
        animalNames.add("12:30pm - 1:30pm");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.timeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, animalNames);
        myRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(myRecyclerViewAdapter);

    }

    public void backToHome(View view) {
        Intent intent = new Intent(this, OwnerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "Discard?" + myRecyclerViewAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
