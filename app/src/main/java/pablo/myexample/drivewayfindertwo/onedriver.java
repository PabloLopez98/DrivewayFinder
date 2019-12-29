package pablo.myexample.drivewayfindertwo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import pablo.myexample.drivewayfinder.MainActivity;
import pablo.myexample.drivewayfinder.R;
import pablo.myexample.drivewayfinder.SpotObjectClass;
import pablo.myexample.drivewayfinder.TransferObjectInterface;

public class onedriver extends Fragment implements OneDriverAdapter.ItemClickListener, DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerView;
    private OneDriverAdapter oneDriverAdapter;
    private TransferObjectInterface listener;
    private ArrayList<SpotObjectClass> spotObjects;
    private String searchString;
    private TextView date;
    private EditText rate;
    private SearchView searchView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TransferObjectInterface) {
            listener = (TransferObjectInterface) context;
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public void onItemClick(View view, int position) {
        SpotObjectClass spotObject = spotObjects.get(position);
        listener.transferSpotObject(spotObject);
        ((TheDriverActivity) getActivity()).toIconClick();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_onedriver, container, false);

        /* ******************************************************************************************** */

       /* searchView = view.findViewById(R.id.searchB);

        searchString = "";
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchString = query;
                //retrieveFormalAddress();
                searchFirebaseAndPopulateRecyclerView("11223 Laurel Ave, Whittier, CA 90605, USA");

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
*/
        /* ******************************************************************************************** */

        rate = view.findViewById(R.id.rate);
        date = view.findViewById(R.id.date);
        recyclerView = view.findViewById(R.id.fragmentonerecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        view.findViewById(R.id.date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCalendar();
            }
        });
        return view;
    }

    public void retrieveFormalAddress() {
        if (searchString.matches("") || rate.getText().toString().matches("") || date.getText().toString().matches("")) {
            Toast.makeText(getContext(), "Please in all fields.", Toast.LENGTH_SHORT).show();
        } else {
            String input = searchString;//.getText().toString();
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + input + "&key=AIzaSyCIdCaG2CZmkG0yezN3RSGc-eNFpnUireM";
            RequestQueue queue = Volley.newRequestQueue(getContext());
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String formal_address = jsonObject.getString("formatted_address");

                        //searchFirebaseAndPopulateRecyclerView(formal_address);

                    } catch (Exception e) {
                        Log.i("VolleyError", e.getLocalizedMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Please Try Again.", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
    }

    public void searchFirebaseAndPopulateRecyclerView(final String formal_address) {
        spotObjects = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot OwnerId : dataSnapshot.getChildren()) {
                    for (DataSnapshot Date : OwnerId.child("Spots").getChildren()) {
                        SpotObjectClass spot = Date.getValue(SpotObjectClass.class);
                        String spotRate = spot.getRate();
                        String spotDate = spot.getDate();
                        String spotCity = spot.getDrivewayLocation().split(" ")[3]; //Whittier,
                        //if (formal_address.contains(spotCity) && (Integer.valueOf(spotRate) < Integer.valueOf(rate.getText().toString())) && date.getText().toString().matches(spotDate)) {
                        spotObjects.add(spot);
                        spotObjects.add(spot);
                        spotObjects.add(spot);
                        spotObjects.add(spot);
                        //}
                    }
                }
                oneDriverAdapter = new OneDriverAdapter(getContext(), spotObjects);
                oneDriverAdapter.setClickListener(onedriver.this);
                recyclerView.setAdapter(oneDriverAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void dialogCalendar() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int newMonth = month + 1;
        date.setText(year + " " + newMonth + " " + dayOfMonth);
    }
}
