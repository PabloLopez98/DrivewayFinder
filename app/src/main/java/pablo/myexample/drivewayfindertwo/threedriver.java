package pablo.myexample.drivewayfindertwo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import pablo.myexample.drivewayfinder.OwnerProfileObject;
import pablo.myexample.drivewayfinder.R;
import pablo.myexample.drivewayfinder.TransferObjectInterface;

public class threedriver extends Fragment {

    TransferObjectInterface listener;
    TextView name, phone, model, plate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_threedriver, container, false);
        name = view.findViewById(R.id.profileDriverName);
        phone = view.findViewById(R.id.profileDriverPhone);
        model = view.findViewById(R.id.profileDriverCarModel);
        plate = view.findViewById(R.id.profileDriverLicensePlate);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Drivers").child(userId).child("ProfileInfo");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DriverProfileObject driverProfileObject = dataSnapshot.getValue(DriverProfileObject.class);
                    name.setText(driverProfileObject.getDriverName());
                    phone.setText(driverProfileObject.getDriverPhoneNumber());
                    model.setText(driverProfileObject.getDriverCarModel());
                    plate.setText(driverProfileObject.getDriverLicensePlates());
                    //transfer profile object from this fragment into parent activity
                    listener.transferDriverProfileObject(driverProfileObject);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return view;
    }

}
