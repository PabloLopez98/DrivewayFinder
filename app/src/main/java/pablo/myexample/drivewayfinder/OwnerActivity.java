package pablo.myexample.drivewayfinder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.security.acl.Owner;

import pablo.myexample.drivewayfindertwo.DriverProfileObject;

public class OwnerActivity extends AppCompatActivity implements TransferObjectInterface {

    OwnerProfileObject ownerProfileObject;
    SpotObjectClass spotObject;

    public void switchToFragmentOne() {
        setTitle("Scheduled");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.TheFragmentHolder, new one()).commit();
    }

    public void switchToFragmentTwo() {
        setTitle("Activity");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.TheFragmentHolder, new two()).commit();
    }

    public void switchToFragmentThree() {
        setTitle("Profile");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.TheFragmentHolder, new three()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToFragmentOne();
                    break;
                case R.id.navigation_dashboard:
                    switchToFragmentTwo();
                    break;
                case R.id.navigation_notifications:
                    switchToFragmentThree();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        switchToFragmentOne();

        /*SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("5623873924", null, "smsText", null, null);*/
        findViewById(R.id.sendSmsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String readSms = Manifest.permission.READ_SMS;
                String receiveSms = Manifest.permission.RECEIVE_SMS;
                int grant = ContextCompat.checkSelfPermission(OwnerActivity.this, readSms) + ContextCompat.checkSelfPermission(OwnerActivity.this, receiveSms);
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OwnerActivity.this, new String[]{readSms, receiveSms}, 1);
                }else{
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("5623873924", null, "smsText", null, null);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(getApplicationContext(), AddDate.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                alertInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    These following methods are here to transfer data objects from fragment to activity
     */

    @Override
    public void transferOwnerProfileObject(OwnerProfileObject ownerProfileObject) {
        this.ownerProfileObject = ownerProfileObject;
    }

    public void toEditProfile(View view) {
        Intent intent = new Intent(this, EditProfile.class);
        intent.putExtra("name", ownerProfileObject.getFullName());
        intent.putExtra("phone", ownerProfileObject.getPhoneNumber());
        intent.putExtra("location", ownerProfileObject.getDrivewayLocation());
        intent.putExtra("imageUrl", ownerProfileObject.getDrivwayImageUrl());
        startActivity(intent);
    }

    @Override
    public void transferSpotObject(SpotObjectClass spotObject) {
        this.spotObject = spotObject;
    }

    public void toDateDetails() {
        Intent intent = new Intent(this, DateDetails.class);
        intent.putExtra("date", spotObject.getDate());
        intent.putExtra("location", spotObject.getDrivewayLocation());
        intent.putExtra("imageUrl", spotObject.getDrivwayImageUrl());
        intent.putExtra("name", spotObject.getFullName());
        //intent.putExtra("isActive", spotObject.getIsActive());
        intent.putExtra("ownerId", spotObject.getOwnerId());
        intent.putExtra("phone", spotObject.getPhoneNumber());
        intent.putExtra("rate", spotObject.getRate());
        intent.putStringArrayListExtra("timeSlotsArray", spotObject.getTimeSlots());
        startActivity(intent);
    }

    @Override
    public void transferDriverProfileObject(DriverProfileObject driverProfileObject) {
    }

     /*
    The above methods are here to transfer data objects from fragment to activity
     */

    @Override
    public void onBackPressed() {
        alertInfo();
    }

    private void alertInfo() {
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
    }

}
