package pablo.myexample.drivewayfindertwo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pablo.myexample.drivewayfinder.R;

public class MapFilter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_filter);
    }

    public void backToDriverHome(View view) {
        Intent intent = new Intent(this, TheDriverActivity.class);
        startActivity(intent);
    }
}
