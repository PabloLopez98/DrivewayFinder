package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pablo.myexample.drivewayfindertwo.TheDriverActivity;

/*
Summary:

SplashScreen.java represents the opening splash screen.
First, it checks if there is a network connection.
Second, it checks if a user is signed in and routes the user to the correct screen.
 */

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isNetworkAvailable()) {//if phone is connected to internet

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {//if user is signed in

                routeChooser(firebaseUser.getUid());
            } else {//else, send to sign in screen

                Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(toMain);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                finish();
            }
        } else {

            Snackbar.make(findViewById(R.id.splashLayout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    public void routeChooser(String id) {

        DatabaseReference ownerRef = FirebaseDatabase.getInstance().getReference().child("Owners").child(id);

        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {//if id exists under owners, send to owner activity

                    Intent toOwner = new Intent(getApplicationContext(), OwnerActivity.class);
                    startActivity(toOwner);
                    overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                    finish();
                } else {//send to driver activity

                    Intent toDriver = new Intent(getApplicationContext(), TheDriverActivity.class);
                    startActivity(toDriver);
                    overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
