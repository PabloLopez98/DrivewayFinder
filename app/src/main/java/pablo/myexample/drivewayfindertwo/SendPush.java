package pablo.myexample.drivewayfindertwo;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendPush {

    public void sendFCMPush(final Context context, String ownersOrDrivers, String ownerOrDriverId) {

        //Listen for other users FCM Token
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child(ownersOrDrivers).child(ownerOrDriverId).child("Token");
        tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String token = (String) dataSnapshot.getValue(String.class);
                Log.i("theToken", token);

                if (token.matches("NA")) {
                    Log.i("insideIf", "yes");
                    //Do nothing
                } else {
                    Log.i("insideElse", "yes");
                    //Listen for server legacy key
                    DatabaseReference Legacy_SERVER_KEY_REF = FirebaseDatabase.getInstance().getReference().child("Legacy").child("key");
                    Legacy_SERVER_KEY_REF.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            final String Legacy_SERVER_KEY = (String) dataSnapshot.getValue(String.class);

                            String msg = "this is test message,.,,.,.";
                            String title = "my title";

                            JSONObject obj = null;
                            JSONObject objData = null;
                            JSONObject dataobjData = null;
                            try {
                                obj = new JSONObject();
                                objData = new JSONObject();
                                objData.put("body", msg);
                                objData.put("title", title);
                                objData.put("tag", token);
                                objData.put("priority", "high");
                                dataobjData = new JSONObject();
                                dataobjData.put("text", msg);
                                dataobjData.put("title", title);
                                obj.put("to", token);
                                obj.put("notification", objData);
                                obj.put("data", dataobjData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                                    params.put("Content-Type", "application/json");
                                    return params;
                                }
                            };

                            RequestQueue requestQueue = Volley.newRequestQueue(context);
                            int socketTimeout = 1000 * 60;// 60 seconds
                            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            jsObjRequest.setRetryPolicy(policy);
                            requestQueue.add(jsObjRequest);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }

}
