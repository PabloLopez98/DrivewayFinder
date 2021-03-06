package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import pablo.myexample.drivewayfindertwo.TheDriverActivity;

/*
Summary:

MainActivity.java represents the sign in screen.
It routes the user to the correct screen once signed in.
User can also navigate to MainActivityTwo.java,
which is where the user decides what type of account he/she wants.
 */

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
    }

    public void signIn(View view) {

        if (email.getText().toString().matches("") || password.getText().toString().matches("")) {

            Toast.makeText(getApplicationContext(), "Missing Input", Toast.LENGTH_SHORT).show();
        } else {

            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        Snackbar.make(findViewById(R.id.theMainLayout), "Signing In!", Snackbar.LENGTH_LONG).show();

                        routeChooser(firebaseAuth.getCurrentUser().getUid());
                    } else {

                        Snackbar.make(findViewById(R.id.theMainLayout), "Error, Please Try Again", Snackbar.LENGTH_INDEFINITE).show();
                    }
                }
            });
        }
    }

    public void createAccount(View view) {

        Intent intent = new Intent(this, MainActivityTwo.class);
        startActivity(intent);
    }

    public void routeChooser(final String id) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ownerRef = firebaseDatabase.getReference().child("Owners").child(id);

        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    //generate new FCM TOKEN for owner
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {

                            String newOwnerToken = instanceIdResult.getToken();
                            ownerRef.child("Token").setValue(newOwnerToken);
                        }
                    });

                    //send to owner activity
                    Intent intent = new Intent(getApplicationContext(), OwnerActivity.class);
                    startActivity(intent);
                } else {

                    //generate new FCM TOKEN for driver
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {

                            String newDriverToken = instanceIdResult.getToken();
                            FirebaseDatabase.getInstance().getReference().child("Drivers").child(id).child("Token").setValue(newDriverToken);
                        }
                    });

                    //send to driver activity
                    Intent intent = new Intent(getApplicationContext(), TheDriverActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}