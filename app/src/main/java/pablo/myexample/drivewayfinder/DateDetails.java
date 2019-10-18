package pablo.myexample.drivewayfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class DateDetails extends AppCompatActivity implements CardDetailsRecyclerView.ItemClickListener{

    CardDetailsRecyclerView cardDetailsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_details);

        // data to populate the RecyclerView with
        ArrayList<CardDetailsRecyclerViewObject> animalNames = new ArrayList<>();

        //create object
        CardDetailsRecyclerViewObject cardDetailsRecyclerViewObject = new CardDetailsRecyclerViewObject("Time","Name","Status");

        animalNames.add(cardDetailsRecyclerViewObject);

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.carddetailsrecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardDetailsRecyclerView = new CardDetailsRecyclerView(this, animalNames);
        cardDetailsRecyclerView.setClickListener(this);
        recyclerView.setAdapter(cardDetailsRecyclerView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
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
