package pablo.myexample.drivewayfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import pablo.myexample.drivewayfindertwo.TheDriverActivity;

public class DriverRoute extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_route);
    }

    public void toTheDriverActivity(View view){
        Intent intent = new Intent(this, TheDriverActivity.class);
        startActivity(intent);
    }
}
