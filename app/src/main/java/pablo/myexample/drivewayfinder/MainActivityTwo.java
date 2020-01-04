package pablo.myexample.drivewayfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


/*
Summary:

MainActivityTwo.java represents the screen in which the user
decides what type of account he/she wants.
 */

public class MainActivityTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_two);

        //change status bar color to dark
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
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
