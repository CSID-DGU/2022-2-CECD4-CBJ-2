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
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import cbj.trailer.adapter.PagerAdapter;
import cbj.trailer.R;

public class MainActivity extends AppCompatActivity {

    private ImageButton rank_btn;
    private ImageButton myPage_btn;
    private String last_last_login_time;
    private String last_login_time;
    private SharedPreferences preferences;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar =
                findViewById(R.id.toolbar);

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

        rank_btn.setOnClickListener(new View.OnClickListener() {       // ????????? ?????? ?????? ??? ????????? ??????
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RankActivity.class);
                startActivity(intent);
            }
        });

        myPage_btn.setOnClickListener(new View.OnClickListener() {       // ????????? ?????? ?????? ??? ????????? ??????
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                startActivity(intent);
            }
        });

        preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);

        last_last_login_time = preferences.getString("last_last_login_time", "");
        last_login_time = preferences.getString("last_login_time", "");

        if(isNewWeek() && preferences.getBoolean("scoreAccept", true)) {
            Intent intent = new Intent(MainActivity.this, CustomDialogActivity.class);
            intent.putExtra("health_info_day", this.getIntent().getIntArrayExtra("health_info_day"));
            intent.putExtra("health_info_week", this.getIntent().getIntArrayExtra("health_info_week"));
            startActivity(intent);
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("scoreAccept", true);
        editor.commit();
    }
    public boolean isNewWeek(){
        //??????????????? ??????
        if(last_last_login_time.equals("") || preferences.getString("default_rank", "").equals(""))
            return true;

        //????????? ?????????????????? ????????? ??????
        int baseYear = Integer.parseInt(last_last_login_time.substring(0,4));
        int baseMonth = Integer.parseInt(last_last_login_time.substring(5,7))-1;
        int baseDay = Integer.parseInt(last_last_login_time.substring(8)); //1??? ???, 7??? ???

        int targetYear = Integer.parseInt(last_login_time.substring(0,4));
        int targetMonth = Integer.parseInt(last_login_time.substring(5,7))-1;
        int targetDay = Integer.parseInt(last_login_time.substring(8)); //1??? ???, 7??? ???

        Calendar baseCal = new GregorianCalendar(baseYear, baseMonth, baseDay);
        Calendar targetCal = new GregorianCalendar(targetYear, targetMonth, targetDay);
        long diffSec = (targetCal.getTimeInMillis() - baseCal.getTimeInMillis())/1000;
        long diffDays = diffSec / (24*60*60);

        //??? : 2, ??? : 3, ....., ??? : 7, ??? : 8
        if(baseDay == 1)
            baseDay = 8;
        if(targetDay==1)
            targetDay = 8;

        // ?????? ?????????????????? 7?????? ???????????? ???????????? ????????? ????????? ????????? ????????? ?????? true ??????
        if(diffDays >= 7 || targetDay - baseDay < 0)
            return true;
        else
            return false;
    }
}