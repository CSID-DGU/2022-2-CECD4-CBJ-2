package com.example.tabbarexample;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;
    public static final String TAG = "BasicHistoryAPI";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("MainActivity.onCreate");
        androidx.appcompat.widget.Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("This is Tab1"));
        tabLayout.addTab(tabLayout.newTab().setText("This is Tab2"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());

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
        // google fit
// 데이터를 읽어올 때 필요한 권한들 정의
        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE) // 누적 걸음 수를 조회하기 위해 필요한 권한
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA) // 단위시간별 걸음 수를 확인하기 위해 필요한 권한
                        .addDataType(DataType.TYPE_CALORIES_EXPENDED) // 소비된 칼로리양을 확인하기 위해 필요한 권한
                        .addDataType(DataType.TYPE_DISTANCE_DELTA) // 이동 거리를 확인하기 위해 필요한 권한
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
        } else {
            subscribe();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // test
        setResult(Activity.RESULT_OK);
        System.out.println("MainActivity.onActivityResult");
        //로그인 성공시
        if (resultCode == Activity.RESULT_OK) {
            System.out.println("login success");
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                System.out.println("requestCode = " + requestCode);
                subscribe();
                //readDataMy();ㄱ
            }
        } else {
            // debug
            System.out.println("result code=" + resultCode);
            System.out.println("login fail");
        }
    }

    public void subscribe(){
        System.out.println("MainActivity.subscribe");
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task){
                                if (task.isSuccessful()){
                                    System.out.println("subscribe success");
                                    Log.w(TAG, "Successfully subscribed!");
                                    readDataMy();
                                }else{
                                    System.out.println("subscribe fail");
                                    Log.w("ErrorError:", task.getException());
                                }
                            }
                        }
                );
    }

    private void readDataMy() {
        System.out.println("MainActivity.readDataMy");
        final Calendar cal = Calendar.getInstance();
        Date now = Calendar.getInstance().getTime();
        cal.setTime(now);

        // 시작 시간
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), 6, 0, 0);
        long startTime = cal.getTimeInMillis();

        // 종료 시간
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), 22, 0, 0);
        long endTime = cal.getTimeInMillis();
        Log.w(TAG, String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

        Fitness.getHistoryClient(this,
                GoogleSignIn.getLastSignedInAccount(this))
                .readData(new DataReadRequest.Builder()
                        .read(DataType.TYPE_STEP_COUNT_DELTA) // Raw 걸음 수
                        .read(DataType.TYPE_CALORIES_EXPENDED) // Raw 칼로리 소비량
                        .read(DataType.TYPE_DISTANCE_DELTA) // Raw 이동 거리
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build())
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse response) {
                        int totalStep = 0;
                        long totalStepTimeNumber = 0;
                        float totalCaloriesFloat = 0;
                        int totalCaloriesInt = 0;
                        float totalDistanceFloat = 0;
                        int totalDistanceInt = 0;
                        String totalStepTimeString = "";

                        DataSet dataSetStepCount = response.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);
                        DataSet dataSetCalories = response.getDataSet(DataType.TYPE_CALORIES_EXPENDED);
                        DataSet dataSetDistance = response.getDataSet(DataType.TYPE_DISTANCE_DELTA);

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

                        //총 칼로리 소모량을 계산
                        for (DataPoint dpCal : dataSetCalories.getDataPoints()) {
                            for (Field field : dpCal.getDataType().getFields()) {
                                totalCaloriesFloat += dpCal.getValue(field).asFloat();
                            }
                        }
                        totalCaloriesInt = (int) Math.floor(totalCaloriesFloat);

                        //총 걸은 거리 측정
                        for (DataPoint dpDis : dataSetDistance.getDataPoints()) {
                            for (Field field : dpDis.getDataType().getFields()) {
                                totalDistanceFloat += dpDis.getValue(field).asFloat();
                            }
                        }
                        totalDistanceInt = (int) Math.floor(totalDistanceFloat);

                        //걸음 수, 걸은 시간, 걸은 거리, 소모한 칼로리 로그로 출력
                        Log.w(TAG, "오늘 걸은 걸음 수: " + totalStep);
                        Log.w(TAG, "오늘 걸은 시간: " + totalStepTimeString);
                        Log.w(TAG, "오늘 걸은 거리: " + totalDistanceInt + "m");
                        Log.w(TAG, "오늘 총 소모 칼로리: " + totalCaloriesInt + "cal");
                        // debug
                        System.out.println("totalStep = " + totalStep);
                    }
                });

    }
}