package pablo.myexample.drivewayfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

public class MainActivityTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_two);
        setTitle("Create Account");
    }

    public void driverRoute(View view) {
        Intent intent = new Intent(this, DriverRoute.class);
        startActivity(intent);
    }

    public void ownerRoute(View view) {
        Intent intent = new Intent(this, OwnerRoute.class);
        startActivity(intent);
    }
}
