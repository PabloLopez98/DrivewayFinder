package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

/*
Summary:

EditProfile.java represents the activity where owners update/edit their profile.
 */

public class EditProfile extends AppCompatActivity {

    private ImageView drivewayImageInput;
    private EditText nameInput, phoneInput, street, city, state;
    private TextView verifiedLocation;
    private Uri uri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Intent intent;
    private OwnerProfileObject ownerProfileObject;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        verifiedLocation = findViewById(R.id.currentVerifiedLocationShown);

        intent = getIntent();

        nameInput = findViewById(R.id.fullNameEdit);
        nameInput.setText(intent.getStringExtra("name"));

        phoneInput = findViewById(R.id.phoneNumberInputEdit);
        phoneInput.setText(intent.getStringExtra("phone"));

        drivewayImageInput = findViewById(R.id.currentImage);
        Picasso.get().load(intent.getStringExtra("imageUrl")).into(drivewayImageInput);

        String[] St = intent.getStringExtra("location").split(" ");
        String Street = St[0] + " " + St[1] + " " + St[2].replace(",", "");
        String City = St[3].replace(",", "");
        String State = St[4];

        street = findViewById(R.id.streetAddressEdit);
        street.setText(Street);
        city = findViewById(R.id.citytAddressEdit);
        city.setText(City);
        state = findViewById(R.id.stateAddressEdit);
        state.setText(State);
    }

    public void validateAddress(View view) {

        String Street = street.getText().toString();
        String City = city.getText().toString();
        String State = state.getText().toString();

        if (Street.matches("") || City.matches("") || State.matches("")) {

            Toast.makeText(getApplicationContext(), "Please fill location info.", Toast.LENGTH_SHORT).show();
        } else {

            String[] St = Street.split(" ");
            String StreetNumber = St[0];
            String StreetName = St[1];
            String StreetType = St[2];

            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + StreetNumber + "+" + StreetName + "+" + StreetType + ",+" + City.toLowerCase() + ",+" + State.toLowerCase() + "&key=AIzaSyCIdCaG2CZmkG0yezN3RSGc-eNFpnUireM";

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        JSONArray jsonArray = response.getJSONArray("results");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String formal_address = jsonObject.getString("formatted_address");
                        verifiedLocation.setText(formal_address);
                    } catch (Exception e) {
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

    public void openImages(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();
            Picasso.get().load(uri).into(drivewayImageInput);
        }
    }

    public void finish(View view) {

        String Name = nameInput.getText().toString();
        String displayedLocation = verifiedLocation.getText().toString();
        String phoneNumber = phoneInput.getText().toString();

        //check if any field is empty
        if (phoneNumber.matches("") || Name.matches("")) {

            Toast.makeText(getApplicationContext(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
        } else if (displayedLocation.matches("Verified Location Shown Here")) {

            Toast.makeText(getApplicationContext(), "Please verify address.", Toast.LENGTH_LONG).show();
        } else {

            editAccount();
        }
    }

    public void editAccount() {

        if (uri == null) {//if same image

            ownerProfileObject = new OwnerProfileObject(nameInput.getText().toString(), phoneInput.getText().toString(), verifiedLocation.getText().toString(), intent.getStringExtra("imageUrl"));
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(userId).child("ProfileInfo");
            databaseReference.setValue(ownerProfileObject);
        } else {

            //Use same old image reference to update it, not delete it!
            String oldPhotoRefUrl = intent.getStringExtra("imageUrl");
            final StorageReference oldPhotoRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldPhotoRefUrl);

            oldPhotoRef.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {

                        throw task.getException();
                    }

                    return oldPhotoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    Uri downloadUri = task.getResult();

                    ownerProfileObject = new OwnerProfileObject(nameInput.getText().toString(), phoneInput.getText().toString(), verifiedLocation.getText().toString(), String.valueOf(downloadUri));

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(userId).child("ProfileInfo");
                    databaseReference.setValue(ownerProfileObject);
                }
            });

        }

        Snackbar.make(findViewById(R.id.editprofileroot), "Successfully Updated Profile!", Snackbar.LENGTH_LONG).show();

        final Intent intent = new Intent(getApplicationContext(), OwnerActivity.class);
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
