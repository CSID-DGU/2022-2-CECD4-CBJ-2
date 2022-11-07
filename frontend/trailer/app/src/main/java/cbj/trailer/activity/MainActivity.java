package cbj.trailer.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import cbj.trailer.adapter.PagerAdapter;
import cbj.trailer.R;

public class MainActivity extends AppCompatActivity {
    public SharedPreferences preferences;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

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

            private String getSharedPreference(String s) {
                SharedPreferences sharedPreferences = getSharedPreferences("last_last_login_time", MODE_PRIVATE);
                String last_last_login = sharedPreferences.getString("last_last_login_time", "");

                sharedPreferences = getSharedPreferences("last_login_time", MODE_PRIVATE);
                String last_login = sharedPreferences.getString("last_login_time", "");

                if() {
                    Intent intent = new Intent(MainActivity.this, CustomDialog.class);
                    startActivity(intent);
                }

                return "";
            }
        });
    }
}