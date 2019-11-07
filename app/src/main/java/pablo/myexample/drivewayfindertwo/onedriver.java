package pablo.myexample.drivewayfindertwo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pablo.myexample.drivewayfinder.OwnerActivity;
import pablo.myexample.drivewayfinder.R;
import pablo.myexample.drivewayfinder.SpotObjectClass;
import pablo.myexample.drivewayfinder.TransferObjectInterface;


public class onedriver extends Fragment implements OneDriverAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private OneDriverAdapter oneDriverAdapter;
    private TransferObjectInterface listener;
    private ArrayList<SpotObjectClass> spotObjects;
    private Button button;
    private EditText editText;

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
        View view = inflater.inflate(R.layout.fragment_onedriver, container, false);
        editText = view.findViewById(R.id.searchInput);
        recyclerView = view.findViewById(R.id.fragmentonerecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        button = view.findViewById(R.id.searchB);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateFragmentOneDriver();
            }
        });
        return view;
    }


    //dont worry about filter just yet
    //set up search and display then go back to filter later
    public void populateFragmentOneDriver() {
        if (editText.getText().toString().matches("")) {
            Toast.makeText(getContext(), "Search is empty.", Toast.LENGTH_SHORT).show();
        } else {

            spotObjects = new ArrayList<>();
            String input = editText.getText().toString();

           /* String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + StreetNumber + "+" + StreetName + "+" + StreetType + ",+" + City.toLowerCase() + ",+" + State.toLowerCase() + "&key=AIzaSyCIdCaG2CZmkG0yezN3RSGc-eNFpnUireM";
            RequestQueue queue = Volley.newRequestQueue(getContext());
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String formal_address = jsonObject.getString("formatted_address");

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
            queue.add(stringRequest);*/

            /*
            //after finding a string location via google api
            retrieve from firebase by filterning by city via split etc.
            assign adapter
            fill arraylist with new search
            display info and pass SpotObectClass object to each card,
            and use Driver activity intent to pass info to icon click activity
            */

        }
    }

}
