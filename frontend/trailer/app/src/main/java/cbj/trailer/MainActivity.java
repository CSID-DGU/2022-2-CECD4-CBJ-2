package cbj.trailer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.bundle.BundleLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //int [] data = this.getIntent().getIntArrayExtra("health_info");
        //Log.w("걸음수Main", Integer.toString(this.getIntent().getIntArrayExtra("health_info")[0]));
        Log.w("걸음수Main", Integer.toString(this.getIntent().getIntArrayExtra("health_info")[0]));
        Log.w("걸음수Main", Integer.toString(this.getIntent().getIntArrayExtra("health_info")[1]));
        Log.w("걸음수Main", Integer.toString(this.getIntent().getIntArrayExtra("health_info")[2]));
        Log.w("걸음수Main", Integer.toString(this.getIntent().getIntArrayExtra("health_info")[3]));
        Log.w("걸음수Main", Integer.toString(this.getIntent().getIntArrayExtra("health_info")[4]));
        Log.w("걸음수Main", Integer.toString(this.getIntent().getIntArrayExtra("health_info")[5]));
        Log.w("걸음수Main", Integer.toString(this.getIntent().getIntArrayExtra("health_info")[6]));
        androidx.appcompat.widget.Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("This is Tab1"));
        tabLayout.addTab(tabLayout.newTab().setText("This is Tab2"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pager);
        //final PagerAdapter adapter = new PagerAdapter
        //        (getSupportFragmentManager(), tabLayout.getTabCount(), new int[]{1, 2, 3, 4, 5, 0, 0, 0, 0, 4, 0, 3, 0, 0});
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), this.getIntent().getIntArrayExtra("health_info"));

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });
    }
}