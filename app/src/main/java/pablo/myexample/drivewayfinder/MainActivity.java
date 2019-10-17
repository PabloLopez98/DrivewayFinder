package pablo.myexample.drivewayfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void signIn(View view){
        Intent intent = new Intent(this, OwnerActivity.class);
        startActivity(intent);
    }

    public void createAccount(View view){
        Intent intent = new Intent(this, MainActivityTwo.class);
        startActivity(intent);
    }
}
