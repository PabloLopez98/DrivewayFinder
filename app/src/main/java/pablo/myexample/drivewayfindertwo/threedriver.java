package pablo.myexample.drivewayfindertwo;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pablo.myexample.drivewayfinder.R;
import pablo.myexample.drivewayfinder.TransferObjectInterface;

/*
Summary:

threedriver.java represents the fragment where the driver profile info is seen.
 */

public class threedriver extends Fragment {

    private TransferObjectInterface listener;
    private TextView name, phone, model, plate;
    private View view;

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.searchViewOption);
        MenuItem item1 = menu.findItem(R.id.filterSearch);
        if (item != null) item.setVisible(false);
        if (item1 != null) item1.setVisible(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        view = inflater.inflate(R.layout.fragment_threedriver, container, false);
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

                //hide progress circle, show layout
                view.findViewById(R.id.theCircle).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.fragThreeDriverLayout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return view;
    }
}
