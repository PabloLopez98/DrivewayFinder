package pablo.myexample.drivewayfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class DriverProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
