package pablo.myexample.drivewayfindertwo;

import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;

import java.util.Calendar;

import pablo.myexample.drivewayfinder.MainActivity;
import pablo.myexample.drivewayfinder.OwnerProfileObject;
import pablo.myexample.drivewayfinder.R;
import pablo.myexample.drivewayfinder.SpotObjectClass;
import pablo.myexample.drivewayfinder.TransferObjectInterface;


/*
Summary:

TheDriverActivity.java represents the activity which holds all the fragments corresponding to drivers.
 */

public class TheDriverActivity extends AppCompatActivity implements TransferObjectInterface {

    private DriverProfileObject driverProfileObject;
    private SpotObjectClass spotObject;
    private String rate, date;

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

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //give default values for rate and date
        Calendar instance = Calendar.getInstance();
        String m = String.valueOf(instance.get(Calendar.MONTH) + 1);
        String y = String.valueOf(instance.get(Calendar.YEAR));
        String d = String.valueOf(instance.get(Calendar.DAY_OF_MONTH));
        rate = "100";

        if (Integer.parseInt(m) < 10) {

            m = "0" + m;
        }

        if (Integer.parseInt(d) < 10) {

            d = "0" + d;
        }

        date = y + " " + m + " " + d;

        switchToFragmentDriverOne();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.driverlogout, menu);
        MenuItem menuItem = menu.findItem(R.id.searchViewOption);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(android.R.attr.maxWidth);
        searchView.setQueryHint("Search By City...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String searchForThis = query;
                onedriver fragObj = (onedriver) getSupportFragmentManager().findFragmentById(R.id.TheFragmentHolderDriver);

                //date is never empty, just check the input rate
                if (rate.matches("")) {

                    rate = "100";
                    Log.i("chechThisStuff", date + " " + rate);
                    fragObj.retrieveFormalAddress(searchForThis, date, rate);//("11223 Laurel Ave, Whittier, CA 90605, USA");
                } else {

                    fragObj.retrieveFormalAddress(searchForThis, date, rate);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

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

                        Snackbar.make(findViewById(R.id.container), "Logging Out", Snackbar.LENGTH_INDEFINITE).show();

                        //set FCM TOKEN TO 'NA'
                        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseDatabase.getInstance().getReference().child("Drivers").child(driverId).child("Token").setValue("NA");

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
            case R.id.filterSearch:
                final Dialog dialog = new Dialog(TheDriverActivity.this);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.show();
                //for calendarview
                CalendarView calendarView = dialog.findViewById(R.id.dialogCalendar);
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                        int newMonth = month + 1;

                        String m = "0";
                        String d = "0";

                        if (newMonth < 10) {

                            m = m + newMonth;
                        } else {

                            m = String.valueOf(newMonth);
                        }

                        if (dayOfMonth < 10) {

                            d = d + dayOfMonth;
                        } else {

                            d = String.valueOf(dayOfMonth);
                        }

                        date = year + " " + m + " " + d;

                    }
                });
                dialog.findViewById(R.id.dialogCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                final EditText editText = dialog.findViewById(R.id.dialogInput);
                dialog.findViewById(R.id.dialogApply).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rate = editText.getText().toString();
                        dialog.dismiss();
                    }
                });
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
