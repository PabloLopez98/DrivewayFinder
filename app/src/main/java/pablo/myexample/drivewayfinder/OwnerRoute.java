package pablo.myexample.drivewayfinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OwnerRoute extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    Uri imageUri;
    EditText email, passWord, fullName, street, city, state, phone;
    TextView displayLocationVerification;
    ImageView drivewayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_route);
        setTitle("Input Information");
        email = findViewById(R.id.email);
        passWord = findViewById(R.id.passWord);
        fullName = findViewById(R.id.fullName);
        street = findViewById(R.id.streetAddress);
        city = findViewById(R.id.citytAddress);
        state = findViewById(R.id.stateAddress);
        drivewayImage = findViewById(R.id.drivewayImageInput);
        displayLocationVerification = findViewById(R.id.verifiedLocationShown);
        phone = findViewById(R.id.phoneNumberInput);
    }

    public void finish(View view) {
        String Street = street.getText().toString();
        String City = city.getText().toString();
        String State = state.getText().toString();
        String displayedLocation = displayLocationVerification.getText().toString();
        String phoneNumber = phone.getText().toString();
        //check if any field is empty
        if (email.getText().toString().matches("") || fullName.getText().toString().matches("") || passWord.getText().toString().matches("") || Street.matches("") || City.matches("") || State.matches("") || imageUri == null || displayedLocation.matches("") || phoneNumber.matches("")) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
            //check if password is long enough
        } else if (passWord.getText().length() < 6) {
            Toast.makeText(getApplicationContext(), "Password must be at least 6 characters.", Toast.LENGTH_LONG).show();
        } else {
            createAccountAndLogin();
        }
    }

    public void validateAddress(View view) {
        String Street = street.getText().toString();
        String[] St = Street.split(" ");
        String StreetNumber = St[0];//11223
        String StreetName = St[1];//laurel
        String StreetType = St[2];//ave
        String City = city.getText().toString();
        String State = state.getText().toString();


        if (Street.matches("") || City.matches("") || State.matches("")) {
            Toast.makeText(getApplicationContext(), "Please fill location info.", Toast.LENGTH_SHORT).show();
        } else {
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String formal_address = jsonObject.getString("formatted_address");
                        displayLocationVerification.setText(formal_address);
                    } catch (Exception e) {
                        Log.i("VolleyError", e.getLocalizedMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Please Try Again.", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
    }

    public void openFileChooser(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(drivewayImage);
        }
    }

    public void createAccountAndLogin() {
        Intent intent = new Intent(this, OwnerActivity.class);
        startActivity(intent);
    }

}
