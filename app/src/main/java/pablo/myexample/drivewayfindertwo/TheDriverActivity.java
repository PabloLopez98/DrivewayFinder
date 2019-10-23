package pablo.myexample.drivewayfindertwo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pablo.myexample.drivewayfinder.AddDate;
import pablo.myexample.drivewayfinder.EditProfile;
import pablo.myexample.drivewayfinder.R;
import pablo.myexample.drivewayfinder.one;
import pablo.myexample.drivewayfinder.three;
import pablo.myexample.drivewayfinder.two;

//screen transition methods are allowed here,
//however complicated task like searching and location is to be kept in the fragment!!!
public class TheDriverActivity extends AppCompatActivity {

    public void switchToFragmentDriverOne() {
        setTitle("Driver Home");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.TheFragmentHolderDriver, new onedriver()).commit();
    }

    public void switchToFragmentDriverTwo() {
        setTitle("Driver Activity");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.TheFragmentHolderDriver, new twodriver()).commit();
    }

    public void switchToFragmentDriverThree() {
        setTitle("Driver Profile");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.TheFragmentHolderDriver, new threedriver()).commit();
    }

    public void switchToFragmentDriverFour() {
        setTitle("Driver Card List");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.TheFragmentHolderDriver, new fourdriver()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToFragmentDriverOne();
                    return true;
                case R.id.navigation_dashboard:
                    switchToFragmentDriverTwo();
                    return true;
                case R.id.navigation_notifications:
                    switchToFragmentDriverThree();
                    return true;
                case R.id.card_screen:
                    switchToFragmentDriverFour();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_driver);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setTitle("Driver Home");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.driverlogout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.driverLogout:
                Toast.makeText(getApplicationContext(),"Logout",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void toEditProfileDriver(View view) {
        Intent intent = new Intent(this, EditProfileDriver.class);
        startActivity(intent);
    }

    public void toMapFilter(View view) {
        Intent intent = new Intent(this, MapFilter.class);
        startActivity(intent);
    }

}
