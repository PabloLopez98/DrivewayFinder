package pablo.myexample.drivewayfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;


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
