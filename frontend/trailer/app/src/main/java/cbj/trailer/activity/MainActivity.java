package cbj.trailer.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cbj.trailer.adapter.PagerAdapter;
import cbj.trailer.R;

public class MainActivity extends AppCompatActivity {

    private ImageButton rank_btn;
    private ImageButton myPage_btn;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar =
                findViewById(R.id.toolbar);

        SharedPreferences preferences;
        setSupportActionBar(toolbar);

        rank_btn = findViewById(R.id.rank_page);
        myPage_btn = findViewById(R.id.my_page);

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("This is Tab1"));
        tabLayout.addTab(tabLayout.newTab().setText("This is Tab2"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pager);

        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), this.getIntent().getIntArrayExtra("health_info_day"), this.getIntent().getIntArrayExtra("health_info_week"));

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

        rank_btn.setOnClickListener(new View.OnClickListener() {       // 버튼을 클릭 했을 때 모션을 정의
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RankActivity.class);
                startActivity(intent);
            }
        });

        myPage_btn.setOnClickListener(new View.OnClickListener() {       // 버튼을 클릭 했을 때 모션을 정의
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                startActivity(intent);
            }
        });

        preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);

        String last_last_login_time = preferences.getString("last_last_login_time", "");
        String last_login_time = preferences.getString("last_login_time", "");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;

        if (last_last_login_time.length() == 0){
            Intent intent = new Intent(MainActivity.this, CustomDialogActivity.class);
            startActivity(intent);
        }

        try {
            date1 = formatter.parse(last_last_login_time);
            date2 = formatter.parse(last_login_time);

        } catch (ParseException e) {
        }

        date1 = new Date(date1.getTime() + (1000 * 60 * 60 * 24 - 1));
        date2 = new Date(date2.getTime() + (1000 * 60 * 60 * 24 - 1));

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        int weekNum1 = cal1.get(Calendar.WEEK_OF_YEAR); // 해당 년의 주차
        int weekNum2 = cal2.get(Calendar.WEEK_OF_YEAR); // 해당 년의 주차

        if(weekNum2 > weekNum1) {
            Intent intent = new Intent(MainActivity.this, CustomDialogActivity.class);
            startActivity(intent);
        }

    }


}