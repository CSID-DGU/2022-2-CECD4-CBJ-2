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
    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;
    public static final String TAG = "BasicHistoryAPI";
    private static int [] health_data = {0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("MainActivity.onCreate");

        // 데이터를 읽어올 때 필요한 권한들 정의
        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE) // 누적 걸음 수를 조회하기 위해 필요한 권한
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA) // 단위시간별 걸음 수를 확인하기 위해 필요한 권한
                        .addDataType(DataType.TYPE_CALORIES_EXPENDED) // 소비된 칼로리양을 확인하기 위해 필요한 권한
                        .addDataType(DataType.TYPE_DISTANCE_DELTA) // 이동 거리를 확인하기 위해 필요한 권한
                        .addDataType(DataType.TYPE_HEART_POINTS) //심장 점수를 확인하기 위해 필요한 권한
                        .build();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION},0);
        }

        //구글 로그인
        if(!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
            //data = getIntent().getIntArrayExtra("health_info");
        } else {
            subscribe();
        }
        //data - 0~6은 일~토 걸음수, 7~13은 일~토 심장점수
        Log.w("걸음수", String.valueOf(health_data[0]));
        Log.w("걸음수", String.valueOf(health_data[1]));
        Log.w("걸음수", String.valueOf(health_data[2]));
        Log.w("걸음수", String.valueOf(health_data[3]));
        Log.w("걸음수", String.valueOf(health_data[4]));
        Log.w("걸음수", String.valueOf(health_data[5]));
        Log.w("걸음수", String.valueOf(health_data[6]));

       androidx.appcompat.widget.Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("This is Tab1"));
        tabLayout.addTab(tabLayout.newTab().setText("This is Tab2"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), health_data);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //로그인 성공시
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
            }
        } else {
            // debug
            System.out.println("result code=" + resultCode);
            System.out.println("login fail");
        }
    }

    public void subscribe(){
        System.out.println("MainActivity.subscribe");
        //final int[][] temp = {new int[14]};
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task){
                                if (task.isSuccessful()){
                                    Log.w(TAG, "Successfully subscribed!");
                                    readDataMy();
                                }else{
                                    Log.w("ErrorError:", task.getException());
                                }
                            }
                        }
                );
    }

    private void readDataMy() {
        final Calendar cal = Calendar.getInstance();
        Date now = Calendar.getInstance().getTime();
        cal.setTime(now);

        int currentDayofWeek = cal.get(Calendar.DAY_OF_WEEK);
        for(int count = 0; count < 7; count++) {
            // 시작 시간
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            cal.add(Calendar.DAY_OF_MONTH, -currentDayofWeek+count);
            long startTime = cal.getTimeInMillis();

            // 종료 시간
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            long endTime = cal.getTimeInMillis();

            int finalCount = count;
            Fitness.getHistoryClient(this,
                            GoogleSignIn.getLastSignedInAccount(this))
                    .readData(new DataReadRequest.Builder()
                            .read(DataType.TYPE_STEP_COUNT_DELTA) // Raw 걸음 수
                            .read(DataType.TYPE_HEART_POINTS) // Raw 심장 점수
                            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                            .build())
                    .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                        @Override
                        public void onSuccess(DataReadResponse response) {
                            int totalStep = 0;
                            long totalStepTimeNumber = 0;
                            float totalHeartPointsFloat = 0;
                            int totalHeartPointsInt = 0;
                            String totalStepTimeString = "";

                            DataSet dataSetStepCount = response.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);
                            DataSet dataSetHeartPoint = response.getDataSet(DataType.TYPE_HEART_POINTS);

                            //누적 걸음 수 및 걸은 시간 확인(금일 오전 6시~22시)
                            for (DataPoint dpStep : dataSetStepCount.getDataPoints()) {
                                totalStepTimeNumber += dpStep.getEndTime(TimeUnit.MILLISECONDS) - dpStep.getStartTime(TimeUnit.MILLISECONDS);

                                for (Field field : dpStep.getDataType().getFields()) {
                                    totalStep += dpStep.getValue(field).asInt();
                                }
                            }

                            //걸은 시간을 형식에 맞춰서 문자열에 저장
                            if (totalStepTimeNumber > 0) {
                                totalStepTimeString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalStepTimeNumber),
                                        TimeUnit.MILLISECONDS.toMinutes(totalStepTimeNumber) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalStepTimeNumber)),
                                        TimeUnit.MILLISECONDS.toSeconds(totalStepTimeNumber) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalStepTimeNumber)));
                            }

                            //총 심장 점수 측정
                            for (DataPoint dpHea : dataSetHeartPoint.getDataPoints()) {
                                for (Field field : dpHea.getDataType().getFields()) {
                                    totalHeartPointsFloat += dpHea.getValue(field).asFloat();
                                }
                            }
                            totalHeartPointsInt = (int) Math.floor(totalHeartPointsFloat);

                            health_data[finalCount] = totalStep;
                            health_data[finalCount +7] = totalHeartPointsInt;
                            Log.w("걸음수1", String.valueOf(health_data[finalCount]));
                        }
                    });
        }
    }
}