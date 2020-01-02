package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pablo.myexample.drivewayfindertwo.TheDriverActivity;

public class SplashScreen extends AppCompatActivity {

    //private static int SPASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(toMain);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                finish();
            }
        }, SPASH_TIME_OUT);*/

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isNetworkAvailable()) {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser != null) {

                //too slow, will see splash screen
                routeChooser(firebaseUser.getUid());

            } else {

               /* new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {*/
                Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(toMain);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                finish();
            }
            //}, SPASH_TIME_OUT);

            //}

        } else {

            Snackbar.make(findViewById(R.id.splashLayout), "No Internet Connection", Snackbar.LENGTH_LONG).show();

        }

    }

    public void routeChooser(String id) {
        DatabaseReference ownerRef = FirebaseDatabase.getInstance().getReference().child("Owners").child(id);
        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //send to owner activity
                   /* Intent intent = new Intent(getApplicationContext(), OwnerActivity.class);
                    startActivity(intent);*/
                    Intent toMain = new Intent(getApplicationContext(), OwnerActivity.class);
                    startActivity(toMain);
                    overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                    finish();
                } else {
                    //send to driver activity
                   /* Intent intent = new Intent(getApplicationContext(), TheDriverActivity.class);
                    startActivity(intent);*/
                    Intent toMain = new Intent(getApplicationContext(), TheDriverActivity.class);
                    startActivity(toMain);
                    overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
