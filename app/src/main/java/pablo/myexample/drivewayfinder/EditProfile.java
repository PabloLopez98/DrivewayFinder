package pablo.myexample.drivewayfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
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
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

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
        String Street = St[0] + " " + St[1] + " " + St[2].replace(",", "");//11223 Laurel Ave
        String City = St[3].replace(",", "");//Whittier
        String State = St[4];//CA

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
                        verifiedLocation.setText(formal_address);
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

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void finish(View view) {
        String Name = nameInput.getText().toString();
        String displayedLocation = verifiedLocation.getText().toString();
        String phoneNumber = phoneInput.getText().toString();
        //check if any field is empty
        if (displayedLocation.matches("") || phoneNumber.matches("") || Name.matches("")) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
        } else {
            editAccount();
        }
    }

    public void editAccount() {

        if (uri == null) {//same image

            ownerProfileObject = new OwnerProfileObject(nameInput.getText().toString(), phoneInput.getText().toString(), verifiedLocation.getText().toString(), intent.getStringExtra("imageUrl"));
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(userId).child("ProfileInfo");
            databaseReference.setValue(ownerProfileObject);
            Toast.makeText(getApplicationContext(), "Edit Successful!", Toast.LENGTH_SHORT).show();

        } else {

            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Uploads");
            final StorageReference filereference = storageReference.child("images/").child(userId).child(System.currentTimeMillis() + "." + getFileExtension(uri));
            filereference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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

                    Uri downloadUri = task.getResult();

                    ownerProfileObject = new OwnerProfileObject(nameInput.getText().toString(), phoneInput.getText().toString(), verifiedLocation.getText().toString(), downloadUri.toString());

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(userId).child("ProfileInfo");
                    databaseReference.setValue(ownerProfileObject);

                    Toast.makeText(getApplicationContext(), "Edit Successful!", Toast.LENGTH_SHORT).show();

                }
            });

        }

        final Intent intent = new Intent(getApplicationContext(), OwnerActivity.class);
        //erase history stacks and start fresh
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

}
