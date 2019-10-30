package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pablo.myexample.drivewayfindertwo.TheDriverActivity;

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
            Toast.makeText(getApplicationContext(), "Missing Input.", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Signed In.", Toast.LENGTH_LONG).show();
                        routeChooser(firebaseAuth.getCurrentUser().getUid());
                    } else {
                        Toast.makeText(getApplicationContext(), "Error: Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void createAccount(View view) {
        Intent intent = new Intent(this, MainActivityTwo.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            routeChooser(firebaseUser.getUid());
        }
    }

    public void routeChooser(String id) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ownerRef = firebaseDatabase.getReference().child("Owners").child(id);
        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //send to owner activity
                    Intent intent = new Intent(getApplicationContext(), OwnerActivity.class);
                    startActivity(intent);
                } else {
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

    /*  Reminders:
        - check intent navigations when app is done!
     */