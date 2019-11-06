package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pablo.myexample.drivewayfindertwo.DriverProfileObject;
import pablo.myexample.drivewayfindertwo.TheDriverActivity;

public class DriverRoute extends AppCompatActivity {

    EditText name, phone, plates, model, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_route);

        name = findViewById(R.id.fullNameDriver);
        phone = findViewById(R.id.phoneNumberInputDriver);
        plates = findViewById(R.id.licensePlatesDriver);
        model = findViewById(R.id.carModelDriver);
        email = findViewById(R.id.emailDriver);
        password = findViewById(R.id.passWordDriver);

    }

    public void registerDriver(View view) {
        if (name.getText().toString().matches("") || phone.getText().toString().matches("") || plates.getText().toString().matches("") || model.getText().toString().matches("") || email.getText().toString().matches("") || password.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Fill in all fields.", Toast.LENGTH_LONG).show();
        } else if (password.getText().toString().length() < 6) {
            Toast.makeText(getApplicationContext(), "Password must be at least 6 characters.", Toast.LENGTH_LONG).show();
        } else {
            createDriverAccount();
        }
    }

    public void createDriverAccount() {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String userId = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Drivers").child(userId).child("ProfileInfo");
                    DriverProfileObject driverProfileObject = new DriverProfileObject(name.getText().toString(), phone.getText().toString(), plates.getText().toString(), model.getText().toString());
                    databaseReference.setValue(driverProfileObject);

                    Toast.makeText(getApplicationContext(), "Creation Successful!", Toast.LENGTH_SHORT).show();
                    final Intent intent = new Intent(getApplicationContext(), TheDriverActivity.class);
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

                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
