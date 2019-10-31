package pablo.myexample.drivewayfinder;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class three extends Fragment {

    TransferObjectInterface listener;
    OwnerProfileObject ownerProfileObject;

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
        View view = inflater.inflate(R.layout.fragment_three, container, false);

        final ImageView drivewayImage = view.findViewById(R.id.profileDisplayImage);
        final TextView name = view.findViewById(R.id.profileDisplayName);
        final TextView phone = view.findViewById(R.id.profileDisplayPhoneNumber);
        final TextView location = view.findViewById(R.id.profileDisplayLocation);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Owners").child(userId).child("ProfileInfo");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ownerProfileObject = dataSnapshot.getValue(OwnerProfileObject.class);
                    Picasso.get().load(ownerProfileObject.getDrivwayImageUrl()).into(drivewayImage);
                    name.setText(ownerProfileObject.getFullName());
                    phone.setText(ownerProfileObject.getPhoneNumber());
                    location.setText(ownerProfileObject.getDrivewayLocation());

                    //transfer profile object from this fragment into parent activity
                    listener.transferOwnerProfileObject(ownerProfileObject);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return view;

    }

}
