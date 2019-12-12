package pablo.myexample.drivewayfindertwo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import pablo.myexample.drivewayfinder.AddDate;
import pablo.myexample.drivewayfinder.EditProfile;
import pablo.myexample.drivewayfinder.MainActivity;
import pablo.myexample.drivewayfinder.OwnerProfileObject;
import pablo.myexample.drivewayfinder.R;
import pablo.myexample.drivewayfinder.SpotObjectClass;
import pablo.myexample.drivewayfinder.TransferObjectInterface;
import pablo.myexample.drivewayfinder.one;
import pablo.myexample.drivewayfinder.three;
import pablo.myexample.drivewayfinder.two;

public class TheDriverActivity extends AppCompatActivity implements TransferObjectInterface {

    private DriverProfileObject driverProfileObject;
    private SpotObjectClass spotObject;

    public void switchToFragmentDriverOne() {
        setTitle("Search For Parking");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.TheFragmentHolderDriver, new onedriver()).commit();
    }

    public void switchToFragmentDriverTwo() {
        setTitle("Activity");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.TheFragmentHolderDriver, new twodriver()).commit();
    }

    public void switchToFragmentDriverThree() {
        setTitle("Profile");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.TheFragmentHolderDriver, new threedriver()).commit();
    }

    public void switchToFragmentDriverFour() {
        setTitle("Scheduled");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.TheFragmentHolderDriver, new fourdriver()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToFragmentDriverOne();
                    break;
                case R.id.navigation_dashboard:
                    switchToFragmentDriverTwo();
                    break;
                case R.id.navigation_notifications:
                    switchToFragmentDriverThree();
                    break;
                case R.id.card_screen:
                    switchToFragmentDriverFour();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_driver);

        //TESTING

       /* Toast.makeText(getApplicationContext(), "Started Service!", Toast.LENGTH_SHORT).show();
        startService(new Intent(TheDriverActivity.this, service.class));*/

        //TESTING

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        switchToFragmentDriverOne();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.driverlogout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.driverLogout:
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Logout?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //TESTING

                      /*  Toast.makeText(getApplicationContext(), "Stopped Service!", Toast.LENGTH_SHORT).show();
                        stopService(new Intent(TheDriverActivity.this, service.class));*/

                        //TESTING

                        Snackbar.make(findViewById(R.id.container), "Logging Out", Snackbar.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    }
                });
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void toEditProfileDriver(View view) {
        Intent intent = new Intent(this, EditProfileDriver.class);
        intent.putExtra("name", driverProfileObject.getDriverName());
        intent.putExtra("phone", driverProfileObject.getDriverPhoneNumber());
        intent.putExtra("model", driverProfileObject.getDriverCarModel());
        intent.putExtra("plate", driverProfileObject.getDriverLicensePlates());
        startActivity(intent);
    }

    public void toIconClick() {
        Intent intent = new Intent(this, IconClick.class);
        intent.putExtra("date", spotObject.getDate());
        intent.putExtra("location", spotObject.getDrivewayLocation());
        intent.putExtra("url", spotObject.getDrivwayImageUrl());
        intent.putExtra("name", spotObject.getFullName());
        //intent.putExtra("active", spotObject.getIsActive());
        intent.putExtra("id", spotObject.getOwnerId());
        intent.putExtra("phone", spotObject.getPhoneNumber());
        intent.putExtra("rate", spotObject.getRate());
        intent.putStringArrayListExtra("slots", spotObject.getTimeSlots());
        startActivity(intent);
    }

    @Override
    public void transferOwnerProfileObject(OwnerProfileObject ownerProfileObject) {

    }

    @Override
    public void transferSpotObject(SpotObjectClass spotObject) {
        this.spotObject = spotObject;
    }

    @Override
    public void transferDriverProfileObject(DriverProfileObject driverProfileObject) {
        this.driverProfileObject = driverProfileObject;
    }
}
