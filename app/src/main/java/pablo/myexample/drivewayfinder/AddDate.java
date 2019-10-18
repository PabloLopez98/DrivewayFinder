package pablo.myexample.drivewayfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AddDate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_date);
    }

    public void toFinalizeDate(View view){
        Intent intent = new Intent(this, FinalizeDate.class);
        startActivity(intent);
    }
}
