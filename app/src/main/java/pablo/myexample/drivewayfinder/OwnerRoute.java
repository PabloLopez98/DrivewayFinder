package pablo.myexample.drivewayfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OwnerRoute extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_route);
    }

    public void finish(View view){
        Intent intent = new Intent(this, OwnerActivity.class);
        startActivity(intent);
    }
}
