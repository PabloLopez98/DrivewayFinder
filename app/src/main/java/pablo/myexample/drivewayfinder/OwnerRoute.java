package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OwnerRoute extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private EditText email, passWord, fullName, street, city, state, phone;
    private TextView displayLocationVerification;
    private ImageView drivewayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_route);
        setTitle("Fill out your driveway owner info");

        //change status bar icons to dark
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //change status bar color to white
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

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
            createAccount();
        }
    }

    public void validateAddress(View view) {
        String Street = street.getText().toString();
        String City = city.getText().toString();
        String State = state.getText().toString();
        if (Street.matches("") || City.matches("") || State.matches("")) {
            Toast.makeText(getApplicationContext(), "Please fill location info.", Toast.LENGTH_SHORT).show();
        } else {
            String[] St = Street.split(" ");
            String StreetNumber = St[0];//11223
            String StreetName = St[1];//laurel
            String StreetType = St[2];//ave
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + StreetNumber + "+" + StreetName + "+" + StreetType + ",+" + City.toLowerCase() + ",+" + State.toLowerCase() + "&key=AIzaSyCIdCaG2CZmkG0yezN3RSGc-eNFpnUireM";
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

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //create account then send user to sign in screen
    public void createAccount() {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), passWord.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final String userId = firebaseAuth.getCurrentUser().getUid();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Uploads");
                    final StorageReference filereference = storageReference.child("images/").child(userId).child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
                    filereference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return filereference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                OwnerProfileObject ownerProfileObject = new OwnerProfileObject(fullName.getText().toString(), phone.getText().toString(), displayLocationVerification.getText().toString(), downloadUri.toString());
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(userId).child("ProfileInfo");
                                databaseReference.setValue(ownerProfileObject);
                                Toast.makeText(getApplicationContext(), "Creation Successful!", Toast.LENGTH_SHORT).show();
                                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                //erase history stacks and start fresh
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Thread thread = new Thread(){
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
                                Toast.makeText(getApplicationContext(), "Image upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error creating account, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
