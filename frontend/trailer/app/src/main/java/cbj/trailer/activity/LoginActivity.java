package cbj.trailer.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cbj.trailer.R;
import cbj.trailer.data.CodeResponse;
import cbj.trailer.data.InitialDataRequest;
import cbj.trailer.data.LoginRequest;
import cbj.trailer.data.LoginResponse;
import cbj.trailer.network.ServiceApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText login_id;
    private EditText login_pwd;
    private Button login_btn;
    private Button join_btn;
    private String id;
    private String pwd;
    private boolean input_id;
    private boolean input_pwd;
    private boolean isAutomatic = false;
    private ProgressBar login_progressbar;
    private final int REQUEST_OAUTH_REQUEST_CODE = 1;
    private ServiceApi service;
    private SharedPreferences preferences;
    private final String TAG = "BasicHistoryAPI";
    private int [] health_data_day;
    private int [] health_data_week;
    String[] stepsOf3weeks = new String[42];


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);                        // xml, java 연결

        //이전에 로그인 한 경력이 있어서 자동 로그인이 되는 경우
        preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        /**
        if (preferences.getString("my_cookie", "") != ""){
            isAutomatic = true;
            startAutomaticLogin();
        }
         **/
        login_id = findViewById(R.id.login_id);
        login_pwd = findViewById(R.id.login_pwd);

        login_btn = findViewById(R.id.login_btn);
        join_btn = findViewById(R.id.join_btn);
        login_progressbar = findViewById(R.id.login_pbar);              // xml의 컴포넌트와 각각 연결

        input_id = false;
        input_pwd = false;                                              // EditText에 문자가 있는지 확인하는 변수 초기화

        login_btn.setEnabled(false);                                    // 버튼 비활성화

        login_id.addTextChangedListener(new TextWatcher() {             // EditText에서 문자의 변화를 감지하는 함수
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    // 바뀌기 전에는 할 필요 없음
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {       // text가 바뀌고 있는 중 모션
                String cor_id = s.toString();

                if (cor_id.length() > 0) {                              // 만약 입력된 문자가 있다면
                    input_id = true;                                    // input_id을 true로 초기화하고
                    if (input_pwd)                                      // password도 입력이 되어 있다면
                        login_btn.setEnabled(true);                     // 버튼을 활성화 해줌
                    else                                                // 입력이 안되어 있다면
                        login_btn.setEnabled(false);                    // 버튼을 비활성화 함
                } else {                                                // 만약 입력된 문자가 없다면
                    input_id = false;                                   // input_id를 false로 초기화하고
                    login_btn.setEnabled(false);                        // 버튼을 비활성화 함
                }
            }

            @Override
            public void afterTextChanged(Editable s) {                  // text가 바뀐 후에는 할 필요 없음(어차피 바뀌면서 함)
            }
        });

        login_pwd.addTextChangedListener(new TextWatcher() {             // EditText에서 문자의 변화를 감지하는 함수
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    // 바뀌기 전에는 할 필요 없음
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {       // text가 바뀌고 있는 중 모션
                String cor_pwd = s.toString();

                if (cor_pwd.length() > 0) {                             // 만약 입력된 문자가 있다면
                    input_pwd = true;                                   // input_pwd을 true로 초기화하고
                    if (input_id)                                       // password도 입력이 되어 있다면
                        login_btn.setEnabled(true);                     // 버튼을 활성화 해줌
                    else                                                // 입력이 안되어 있다면
                        login_btn.setEnabled(false);                    // 버튼을 비활성화 함
                } else {                                                // 만약 입력된 문자가 없다면
                    input_pwd = false;                                  // input_pwd을 false로 초기화하고
                    login_btn.setEnabled(false);                        // 버튼을 비활성화 함
                }
            }

            @Override
            public void afterTextChanged(Editable s) {                  // text가 바뀐 후에는 할 필요 없음(어차피 바뀌면서 함)
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {       // 버튼을 클릭 했을 때 모션을 정의
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                tryLogin();
            }                 // 로그인을 시도함
        });

        join_btn.setOnClickListener(new View.OnClickListener() {        // 버튼을 클릭 했을 때 모션을 정의
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);                                  // 회원가입 액티비티로 넘어감
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void tryLogin() {
        id = login_id.getText().toString();                             // id에 입력된 input을 넣어줌
        pwd = login_pwd.getText().toString();                           // password에 입력된 input을 넣어줌

        login_progressbar.setVisibility(View.VISIBLE);                  // progressbar를 활성화 해주고\

        startLogin(new LoginRequest(id, pwd));                          // 로그인을 시작함
    }

    //처음 로그인 하는 경우
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startLogin(LoginRequest data) {                        // 로그인을 하는 함수(이전에 설명했으므로 요약함)
        service.userLogin(data).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse user = response.body();                   // 응답받은 body의 객체를 넣고 code에 따라 활동이 나뉨
                if (user.getCode() == 200) {                            // 로그인 성공이라면
                    //로그인 이력 있는 경우 바로 Main
                    //로그인 이력 없는 경우 권한 요청 후 데이터 받아서 넘김
                    // 데이터를 읽어올 때 필요한 권한들 정의
                    FitnessOptions fitnessOptions =
                            FitnessOptions.builder()
                                    .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE) // 누적 걸음 수를 조회하기 위해 필요한 권한
                                    .addDataType(DataType.TYPE_STEP_COUNT_DELTA) // 단위시간별 걸음 수를 확인하기 위해 필요한 권한
                                    .addDataType(DataType.TYPE_CALORIES_EXPENDED) // 소비된 칼로리양을 확인하기 위해 필요한 권한
                                    .addDataType(DataType.TYPE_DISTANCE_DELTA) // 이동 거리를 확인하기 위해 필요한 권한
                                    .addDataType(DataType.TYPE_HEART_POINTS) //심장 점수를 확인하기 위해 필요한 권한
                                    .build();

                    if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
                        requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION},0);
                    }

                    //로그인한 이력이 있다는 것을 남기기 위해 쿠키 정보 저장
                    Calendar cal = Calendar.getInstance();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("my_cookie", response.headers().get("Set-Cookie"));
                    editor.commit();

                    //구글 로그인
                    /**
                    if(!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(LoginActivity.this), fitnessOptions)) {
                        GoogleSignIn.requestPermissions(
                                LoginActivity.this,
                                REQUEST_OAUTH_REQUEST_CODE,
                                GoogleSignIn.getLastSignedInAccount(LoginActivity.this),
                                fitnessOptions);
                    } else {
                        //기존에 로그인 된 이력이 있는 경우
                        subscribe();
                    }*/
                    GoogleSignIn.requestPermissions(
                            LoginActivity.this,
                            REQUEST_OAUTH_REQUEST_CODE,
                            GoogleSignIn.getLastSignedInAccount(LoginActivity.this),
                            fitnessOptions);
                    finish();
                } else if (user.getCode() == 204)                       // 아이디가 존재하지 않을 경우
                    Toast.makeText(LoginActivity.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                else                                                    // 비밀번호가 일치하지 않을 경우
                    Toast.makeText(LoginActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                login_progressbar.setVisibility(View.INVISIBLE);        // 로그인 모션이 끝났으니 progressbar 비활성화
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "통신 오류 발생", Toast.LENGTH_SHORT).show();
                Log.e("통신 오류 발생", t.getMessage());
                login_progressbar.setVisibility(View.INVISIBLE);        // 통신의 오류가 생김, progressbar 비활성화
            }
        });
    }

    //자동로그인
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startAutomaticLogin() {                        // 로그인을 하는 함수(이전에 설명했으므로 요약함)
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
        } else {
            //기존에 로그인 된 이력이 있는 경우
            subscribe();
        }
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

    public synchronized void subscribe(){
        health_data_day = new int[14];
        for(int i=0; i<14; i++){
            health_data_day[i] = 0;
        }
        health_data_week = new int[12];
        for(int i=0; i<12; i++){
            health_data_week[i] = 0;
        }
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task){
                                if (task.isSuccessful()){
                                    Log.w(TAG, "Successfully subscribed!");
                                    readDataDay();
                                    readDataWeek();
                                    if(isAutomatic){
                                        readDataUntilLastLogin();
                                    }
                                    else{
                                        readDataThreeWeeks();
                                    }
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            /**
                                            for(int i=0; i<6; i++)
                                                Log.w(Integer.toString(i), Integer.toString(health_data_week[i]));
                                            for(int i = 0; i<41; i+=2) {
                                                String st1 = "날짜" + Integer.toString(i);
                                                String st2 = "걸음 수" + Integer.toString(i);
                                                Log.w(st1, stepsOf3weeks[i]);
                                                Log.w(st2, stepsOf3weeks[i+1]);
                                            }*/
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.putExtra("health_info_day", health_data_day);
                                            intent.putExtra("health_info_week", health_data_week);
                                            service.initialData(new InitialDataRequest(stepsOf3weeks)).enqueue(new Callback<CodeResponse>() {

                                                @Override
                                                public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                                                    CodeResponse user = response.body();                   // 응답받은 body의 객체를 넣고 code에 따라 활동이 나뉨
                                                    if (user.getCode() == 200) {                            // 로그인 성공이라면
                                                        Log.w("걸음수", "데이터 전송 성공");
                                                    } else if (user.getCode() == 204)                       // 아이디가 존재하지 않을 경우
                                                        Toast.makeText(LoginActivity.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                                    else                                                    // 비밀번호가 일치하지 않을 경우
                                                        Toast.makeText(LoginActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                                    login_progressbar.setVisibility(View.INVISIBLE);        // 로그인 모션이 끝났으니 progressbar 비활성화
                                                }

                                                @Override
                                                public void onFailure(Call<CodeResponse> call, Throwable t) {
                                                    Toast.makeText(LoginActivity.this, "통신 오류 발생", Toast.LENGTH_SHORT).show();
                                                    Log.e("통신 오류 발생", t.getMessage());
                                                    login_progressbar.setVisibility(View.INVISIBLE);        // 통신의 오류가 생김, progressbar 비활성화
                                                }
                                            });
                                            Calendar cal = Calendar.getInstance();
                                            SharedPreferences.Editor editor = preferences.edit();
                                            //자동로그인인 경우
                                            if(isAutomatic){
                                                editor.putString("last_last_login_time", preferences.getString("last_login_time", ""));
                                                editor.putString("last_login_time", cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH));
                                            }
                                            //첫로그인인 경우
                                            else{
                                                editor.putString("last_login_time", cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH));
                                            }
                                            editor.commit();
                                            startActivity(intent);                              // 성공이라면 Main 액티비티로 넘어가고 현 액티비티 종료
                                            finish();
                                        }
                                    },2000);
                                }else{
                                    Log.w("ErrorError:", task.getException());
                                }
                            }
                        }
                );
    }

    private void readDataDay() {
        final Calendar cal = Calendar.getInstance();
        Date now = Calendar.getInstance().getTime();
        cal.setTime(now);
        //월  화  수  목  금  토  일
        //일 : 1, 월 : 2, 화 : 3, 수 : 4, 목 : 5, 금 : 6, 토 : 7
        int currentDayofWeek = cal.get(Calendar.DAY_OF_WEEK); //현재 요일
        if(currentDayofWeek == 1){
            for (int count = 6; count >= 0; count--) {
                final Calendar cal2 = Calendar.getInstance();
                Date now2 = Calendar.getInstance().getTime();
                cal2.setTime(now2);
                // 시작 시간
                cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH),
                        cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal2.add(Calendar.DAY_OF_MONTH, -count);
                long startTime = cal2.getTimeInMillis();

                final Calendar cal3 = Calendar.getInstance();
                Date now3 = Calendar.getInstance().getTime();
                cal3.setTime(now3);
                // 종료 시간
                cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                        cal3.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                cal3.add(Calendar.DAY_OF_MONTH, -count);
                long endTime = cal3.getTimeInMillis();

                int finalCount = 6-count;
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

                                health_data_day[finalCount] = totalStep;
                                health_data_day[finalCount + 7] = totalHeartPointsInt;
                            }
                        });
            }
        }
        else {
            for (int count = 2; count <= currentDayofWeek; count++) {
                final Calendar cal2 = Calendar.getInstance();
                Date now2 = Calendar.getInstance().getTime();
                cal2.setTime(now2);
                // 시작 시간
                cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH),
                        cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal2.add(Calendar.DAY_OF_MONTH, -currentDayofWeek + count);
                long startTime = cal2.getTimeInMillis();

                final Calendar cal3 = Calendar.getInstance();
                Date now3 = Calendar.getInstance().getTime();
                cal3.setTime(now3);
                // 종료 시간
                cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                        cal3.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                cal3.add(Calendar.DAY_OF_MONTH, -currentDayofWeek + count);
                long endTime = cal3.getTimeInMillis();

                int finalCount = count - 2;
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

                                health_data_day[finalCount] = totalStep;
                                health_data_day[finalCount + 7] = totalHeartPointsInt;
                            }
                        });
            }
        }
    }

    private void readDataWeek() {
        final Calendar cal = Calendar.getInstance();
        Date now = Calendar.getInstance().getTime();
        cal.setTime(now);

        // 28 29 30 31 1 2 3
        // 4  5  6  7  8 9 10
        // 11 12 13 14 15 16 17
        // 18 19 20
        int currentDay = cal.get(Calendar.DATE); //현재 몇일인지 저장하는 변수(20)
        int currentDayofWeek = cal.get(Calendar.DAY_OF_WEEK); //현재 요일(3)
        int temp = (int)((currentDay - currentDayofWeek+1) / 7) + 1; //3
        if ((currentDay - currentDayofWeek+1) % 7 != 0.0){
            temp+=1; //4
        }

        for(int i = temp; i>0; i--) {
            if (i == temp) {
                for (int count = 1; count <= currentDayofWeek; count++) {
                    final Calendar cal2 = Calendar.getInstance();
                    Date now2 = Calendar.getInstance().getTime();
                    cal2.setTime(now2);
                    // 시작 시간
                    cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH),
                            cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                    cal2.add(Calendar.DAY_OF_MONTH, -currentDayofWeek + count);
                    long startTime = cal2.getTimeInMillis();

                    final Calendar cal3 = Calendar.getInstance();
                    Date now3 = Calendar.getInstance().getTime();
                    cal3.setTime(now3);
                    // 종료 시간
                    cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                            cal3.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                    cal3.add(Calendar.DAY_OF_MONTH, -currentDayofWeek + count);
                    long endTime = cal3.getTimeInMillis();

                    int finalCount = i- 1;
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

                                    health_data_week[finalCount] += totalStep;
                                    health_data_week[finalCount + 6] += totalHeartPointsInt;
                                    //Log.w("걸음수", Integer.toString(finalCount));
                                    //Log.w("걸음수", Integer.toString(totalStep));
                                }
                            });
                }
            }
            else{
                for (int count = 1; count <= 7; count++) {
                    final Calendar cal3 = Calendar.getInstance();
                    Date now3 = Calendar.getInstance().getTime();
                    cal3.setTime(now3);
                    // 시작 시간
                    cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                            cal3.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                    cal3.add(Calendar.DAY_OF_MONTH, -currentDayofWeek - 7*(temp-i) + count);
                    long startTime = cal3.getTimeInMillis();

                    final Calendar cal4 = Calendar.getInstance();
                    Date now4 = Calendar.getInstance().getTime();
                    cal4.setTime(now4);
                    // 종료 시간
                    cal4.set(cal4.get(Calendar.YEAR), cal4.get(Calendar.MONTH),
                            cal4.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                    cal4.add(Calendar.DAY_OF_MONTH, -currentDayofWeek - 7*(temp-i) + count);
                    long endTime = cal4.getTimeInMillis();

                    int finalCount = i- 1;
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

                                    health_data_week[finalCount] += totalStep;
                                    health_data_week[finalCount + 6] += totalHeartPointsInt;
                                    //Log.w("걸음수", Integer.toString(finalCount));
                                    //Log.w("걸음수", Integer.toString(totalStep));
                                }
                            });
                }
            }
        }
    }
    private void readDataThreeWeeks() {
        final Calendar cal = Calendar.getInstance();
        Date now = Calendar.getInstance().getTime();
        cal.setTime(now);
        //월  화  수  목  금  토  일
        //일 : 1, 월 : 2, 화 : 3, 수 : 4, 목 : 5, 금 : 6, 토 : 7
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for(int i=0; i<42; i++)
            stepsOf3weeks[i] = "0";
        for (int count = 21; count > 0; count--) {
            final Calendar cal2 = Calendar.getInstance();
            Date now2 = Calendar.getInstance().getTime();
            cal2.setTime(now2);
            // 시작 시간
            cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH),
                    cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            cal2.add(Calendar.DAY_OF_MONTH, -count);
            long startTime = cal2.getTimeInMillis();

            final Calendar cal3 = Calendar.getInstance();
            Date now3 = Calendar.getInstance().getTime();
            cal3.setTime(now3);
            // 종료 시간
            cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                    cal3.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            cal3.add(Calendar.DAY_OF_MONTH, -count);
            long endTime = cal3.getTimeInMillis();
            String mydate = sdf.format(cal3.getTime());
            int finalCount = count;
            Fitness.getHistoryClient(this,
                            GoogleSignIn.getLastSignedInAccount(this))
                    .readData(new DataReadRequest.Builder()
                            .read(DataType.TYPE_STEP_COUNT_DELTA) // Raw 걸음 수
                            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                            .build())
                    .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                        @Override
                        public void onSuccess(DataReadResponse response) {
                            int totalStep = 0;
                            DataSet dataSetStepCount = response.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);

                            //누적 걸음 수 및 걸은 시간 확인(금일 오전 6시~22시)
                            for (DataPoint dpStep : dataSetStepCount.getDataPoints()) {
                                for (Field field : dpStep.getDataType().getFields()) {
                                    totalStep += dpStep.getValue(field).asInt();
                                }
                            }
                            stepsOf3weeks[(21- finalCount)*2] = mydate;
                            stepsOf3weeks[(21- finalCount)*2+1] = Integer.toString(totalStep);
                            //Log.w("날짜", mydate);
                            //Log.w("걸음 수", Integer.toString(totalStep));
                        }
                    });
        }
    }
    private void readDataUntilLastLogin(){
        //YYYY-MM-dd
        String LastLogin = preferences.getString("last_login_time", "");
        if(LastLogin == ""){
            Log.w("Error", "이전 로그인 한 기록이 없어짐");
        }
        else{
            //날짜 차이 구하기(가장 최근 로그인 한 날짜 ~ 지금 날짜)
            Calendar baseCal = new GregorianCalendar(Integer.parseInt(LastLogin.substring(0,4)), Integer.parseInt(LastLogin.substring(5,7))-1, Integer.parseInt(LastLogin.substring(8)));
            Calendar targetCal = new GregorianCalendar(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
            long diffSec = (targetCal.getTimeInMillis() - baseCal.getTimeInMillis())/1000;
            long diffDays = diffSec / (24*60*60);

            //최근 3주간 접속이 한번도 없었던 경우
            if(diffDays > 21)
                diffDays = 21;

            int diff = (int)diffDays;

            final Calendar cal = Calendar.getInstance();
            Date now = Calendar.getInstance().getTime();
            cal.setTime(now);
            //월  화  수  목  금  토  일
            //일 : 1, 월 : 2, 화 : 3, 수 : 4, 목 : 5, 금 : 6, 토 : 7
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for(int i=0; i<42; i++)
                stepsOf3weeks[i] = "0";
            int index = 0;
            for (int count = diff; count > 0; count--) {
                final Calendar cal2 = Calendar.getInstance();
                Date now2 = Calendar.getInstance().getTime();
                cal2.setTime(now2);
                // 시작 시간
                cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH),
                        cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal2.add(Calendar.DAY_OF_MONTH, -count);
                long startTime = cal2.getTimeInMillis();

                final Calendar cal3 = Calendar.getInstance();
                Date now3 = Calendar.getInstance().getTime();
                cal3.setTime(now3);
                // 종료 시간
                cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                        cal3.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                cal3.add(Calendar.DAY_OF_MONTH, -count);
                long endTime = cal3.getTimeInMillis();
                String mydate = sdf.format(cal3.getTime());

                int finalIndex = index;
                Fitness.getHistoryClient(this,
                                GoogleSignIn.getLastSignedInAccount(this))
                        .readData(new DataReadRequest.Builder()
                                .read(DataType.TYPE_STEP_COUNT_DELTA) // Raw 걸음 수
                                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                                .build())
                        .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse response) {
                                int totalStep = 0;
                                DataSet dataSetStepCount = response.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);

                                //누적 걸음 수 및 걸은 시간 확인(금일 오전 6시~22시)
                                for (DataPoint dpStep : dataSetStepCount.getDataPoints()) {
                                    for (Field field : dpStep.getDataType().getFields()) {
                                        totalStep += dpStep.getValue(field).asInt();
                                    }
                                }
                                stepsOf3weeks[finalIndex] = mydate;
                                stepsOf3weeks[finalIndex+1] = Integer.toString(totalStep);
                                //Log.w("날짜", mydate);
                                //Log.w("걸음 수", Integer.toString(totalStep));
                            }
                        });
                index+=2;
            }
        }
    }
}