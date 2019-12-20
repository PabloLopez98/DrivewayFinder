package pablo.myexample.drivewayfinder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Owner;
import java.util.HashMap;
import java.util.Map;

import pablo.myexample.drivewayfindertwo.DriverProfileObject;

public class OwnerActivity extends AppCompatActivity implements TransferObjectInterface {

    OwnerProfileObject ownerProfileObject;
    SpotObjectClass spotObject;

    private void sendFCMPush() {
        final String Legacy_SERVER_KEY = ; String token = ;
        String msg = "this is test message,.,,.,.";
        String title = "my title";
        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;
        try {
            obj = new JSONObject();
            objData = new JSONObject();
            objData.put("body", msg);
            objData.put("title", title);
            objData.put("tag", token);
            objData.put("priority", "high");
            dataobjData = new JSONObject();
            dataobjData.put("text", msg);
            dataobjData.put("title", title);
            obj.put("to", token);
            obj.put("notification", objData);
            obj.put("data", dataobjData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

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

        findViewById(R.id.notifySamsungPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFCMPush();
            }
        });

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
