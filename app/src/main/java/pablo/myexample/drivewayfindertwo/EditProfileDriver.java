package pablo.myexample.drivewayfindertwo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Driver;

import pablo.myexample.drivewayfinder.MainActivity;
import pablo.myexample.drivewayfinder.R;

public class EditProfileDriver extends AppCompatActivity {

    Intent intent;
    TextView dname, dphone, dplate, dmodel;
    EditText nname, nphone, nplate, nmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_driver);

        setTitle("Edit Profile");

        intent = getIntent();
        dname = findViewById(R.id.profileDisplayName);
        dname.setText(dname.getText().toString() + " " + intent.getStringExtra("name"));
        dphone = findViewById(R.id.profileDisplayPhoneNumber);
        dphone.setText(dphone.getText().toString() + " " + intent.getStringExtra("phone"));
        dplate = findViewById(R.id.profileDisiplayPlate);
        dplate.setText(dplate.getText().toString() + " " + intent.getStringExtra("plate"));
        dmodel = findViewById(R.id.profileDisplayCarModel);
        dmodel.setText(dmodel.getText().toString() + " " + intent.getStringExtra("model"));

        nname = findViewById(R.id.newName);
        nphone = findViewById(R.id.newPhone);
        nplate = findViewById(R.id.newPlate);
        nmodel = findViewById(R.id.newCarModel);

    }

    public void editProfileDriverConfirm(View view) {
        if (nname.getText().toString().matches("") || nphone.getText().toString().matches("") || nplate.getText().toString().matches("") || nmodel.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
        } else {
            DriverProfileObject driverProfileObject = new DriverProfileObject(nname.getText().toString(), nphone.getText().toString(), nplate.getText().toString(), nmodel.getText().toString());
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Drivers").child(userId).child("ProfileInfo");
            databaseReference.setValue(driverProfileObject);
            Toast.makeText(getApplicationContext(), "Successful update.", Toast.LENGTH_LONG).show();
            final Intent intent = new Intent(getApplicationContext(), TheDriverActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3500);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

}