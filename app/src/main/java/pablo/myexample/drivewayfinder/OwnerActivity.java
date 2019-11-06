package pablo.myexample.drivewayfinder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pablo.myexample.drivewayfindertwo.DriverProfileObject;
import pablo.myexample.drivewayfindertwo.threedriver;

import static android.nfc.NfcAdapter.EXTRA_ID;

public class OwnerActivity extends AppCompatActivity implements TransferObjectInterface {

    OwnerProfileObject ownerProfileObject;
    SpotObjectClass spotObject;

    public void switchToFragmentOne() {
        setTitle("Home");
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
        intent.putExtra("isActive", spotObject.getIsActive());
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
                Toast.makeText(getApplicationContext(), "Logging out.", Toast.LENGTH_SHORT).show();
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
