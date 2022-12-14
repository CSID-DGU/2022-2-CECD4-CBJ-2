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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import cbj.trailer.R;
import cbj.trailer.data.CodeResponse;
import cbj.trailer.data.InitialDataRequest;
import cbj.trailer.data.LoginRequest;
import cbj.trailer.data.LoginResponse;
import cbj.trailer.data.TargetStepsOfDayResponse;
import cbj.trailer.network.RetrofitClient;
import cbj.trailer.network.ServiceApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView title;
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
        setContentView(R.layout.activity_login);                        // xml, java ??????

        service = RetrofitClient.getClient().create(ServiceApi.class);

        title = (TextView)findViewById(R.id.login_main);
        String titleMessage = title.getText().toString();
        SpannableString spannableString = new SpannableString(titleMessage);

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main2)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.3f), 0, 5, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannableString);

        //????????? ????????? ??? ????????? ????????? ?????? ???????????? ?????? ??????
        preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);

        if (preferences.getString("userId", "") != ""){
            isAutomatic = true;
            startAutomaticLogin();
        }

        login_id = findViewById(R.id.login_id);
        login_pwd = findViewById(R.id.login_pwd);

        login_btn = findViewById(R.id.login_btn);
        join_btn = findViewById(R.id.join_btn);
        login_progressbar = findViewById(R.id.login_pbar);              // xml??? ??????????????? ?????? ??????

        input_id = false;
        input_pwd = false;                                              // EditText??? ????????? ????????? ???????????? ?????? ?????????

        login_btn.setEnabled(false);                                    // ?????? ????????????

        login_id.addTextChangedListener(new TextWatcher() {             // EditText?????? ????????? ????????? ???????????? ??????
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    // ????????? ????????? ??? ?????? ??????
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {       // text??? ????????? ?????? ??? ??????
                String cor_id = s.toString();

                if (cor_id.length() > 0) {                              // ?????? ????????? ????????? ?????????
                    input_id = true;                                    // input_id??? true??? ???????????????
                    if (input_pwd)                                      // password??? ????????? ?????? ?????????
                        login_btn.setEnabled(true);                     // ????????? ????????? ??????
                    else                                                // ????????? ????????? ?????????
                        login_btn.setEnabled(false);                    // ????????? ???????????? ???
                } else {                                                // ?????? ????????? ????????? ?????????
                    input_id = false;                                   // input_id??? false??? ???????????????
                    login_btn.setEnabled(false);                        // ????????? ???????????? ???
                }
            }

            @Override
            public void afterTextChanged(Editable s) {                  // text??? ?????? ????????? ??? ?????? ??????(????????? ???????????? ???)
            }
        });

        login_pwd.addTextChangedListener(new TextWatcher() {             // EditText?????? ????????? ????????? ???????????? ??????
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    // ????????? ????????? ??? ?????? ??????
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {       // text??? ????????? ?????? ??? ??????
                String cor_pwd = s.toString();

                if (cor_pwd.length() > 0) {                             // ?????? ????????? ????????? ?????????
                    input_pwd = true;                                   // input_pwd??? true??? ???????????????
                    if (input_id)                                       // password??? ????????? ?????? ?????????
                        login_btn.setEnabled(true);                     // ????????? ????????? ??????
                    else                                                // ????????? ????????? ?????????
                        login_btn.setEnabled(false);                    // ????????? ???????????? ???
                } else {                                                // ?????? ????????? ????????? ?????????
                    input_pwd = false;                                  // input_pwd??? false??? ???????????????
                    login_btn.setEnabled(false);                        // ????????? ???????????? ???
                }
            }

            @Override
            public void afterTextChanged(Editable s) {                  // text??? ?????? ????????? ??? ?????? ??????(????????? ???????????? ???)
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {       // ????????? ?????? ?????? ??? ????????? ??????
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                tryLogin();
                login_progressbar.setVisibility(View.VISIBLE);        // ????????? ????????? ???????????? progressbar ????????????
            }                 // ???????????? ?????????
        });

        join_btn.setOnClickListener(new View.OnClickListener() {        // ????????? ?????? ?????? ??? ????????? ??????
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
                finish();// ???????????? ??????????????? ?????????
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void tryLogin() {
        id = login_id.getText().toString();                             // id??? ????????? input??? ?????????
        pwd = login_pwd.getText().toString();                           // password??? ????????? input??? ?????????

        login_progressbar.setVisibility(View.VISIBLE);                  // progressbar??? ????????? ?????????\

        startLogin(new LoginRequest(id, pwd));                          // ???????????? ?????????
    }

    //?????? ????????? ?????? ??????
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startLogin(LoginRequest data) {                        // ???????????? ?????? ??????(????????? ?????????????????? ?????????)
        service.userLogin(data).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse user = response.body();                   // ???????????? body??? ????????? ?????? code??? ?????? ????????? ??????
                if (user.getCode() == 200) {                            // ????????? ???????????????

                    //????????? ?????? ?????? ?????? ?????? Main
                    //????????? ?????? ?????? ?????? ?????? ?????? ??? ????????? ????????? ??????
                    // ???????????? ????????? ??? ????????? ????????? ??????
                    FitnessOptions fitnessOptions =
                            FitnessOptions.builder()
                                    .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE) // ?????? ?????? ?????? ???????????? ?????? ????????? ??????
                                    .addDataType(DataType.TYPE_STEP_COUNT_DELTA) // ??????????????? ?????? ?????? ???????????? ?????? ????????? ??????
                                    .addDataType(DataType.TYPE_CALORIES_EXPENDED) // ????????? ??????????????? ???????????? ?????? ????????? ??????
                                    .addDataType(DataType.TYPE_DISTANCE_DELTA) // ?????? ????????? ???????????? ?????? ????????? ??????
                                    .addDataType(DataType.TYPE_HEART_POINTS) //?????? ????????? ???????????? ?????? ????????? ??????
                                    .build();

                    if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
                        requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION},0);
                    }

                    //???????????? ????????? ????????? ?????? ????????? ?????? ?????? ?????? ??????
                    Calendar cal = Calendar.getInstance();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("userId", user.getUserId());
                    editor.putString("userNickname", user.getUserNickname());
                    editor.putString("userAge", Integer.toString(user.getUserAge()));
                    editor.putString("userGender", user.getUserGender());
                    editor.commit();

                    //?????? ?????????
                    /**
                    if(!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(LoginActivity.this), fitnessOptions)) {
                        GoogleSignIn.requestPermissions(
                                LoginActivity.this,
                                REQUEST_OAUTH_REQUEST_CODE,
                                GoogleSignIn.getLastSignedInAccount(LoginActivity.this),
                                fitnessOptions);
                    } else {
                        //????????? ????????? ??? ????????? ?????? ??????
                        subscribe();
                    }*/
                    GoogleSignIn.requestPermissions(
                            LoginActivity.this,
                            REQUEST_OAUTH_REQUEST_CODE,
                            GoogleSignIn.getLastSignedInAccount(LoginActivity.this),
                            fitnessOptions);
                } else if (user.getCode() == 204) {                   // ???????????? ???????????? ?????? ??????
                    Toast.makeText(LoginActivity.this, "???????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    login_progressbar.setVisibility(View.INVISIBLE);        // ????????? ????????? ???????????? progressbar ????????????
                }
                else {                                                  // ??????????????? ???????????? ?????? ??????
                    Toast.makeText(LoginActivity.this, "??????????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                    login_progressbar.setVisibility(View.INVISIBLE);        // ????????? ????????? ???????????? progressbar ????????????
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
                Log.e("?????? ?????? ??????", t.getMessage());
                login_progressbar.setVisibility(View.INVISIBLE);        // ????????? ????????? ??????, progressbar ????????????
            }
        });
    }

    //???????????????
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startAutomaticLogin() {                        // ???????????? ?????? ??????(????????? ?????????????????? ?????????)
        // ???????????? ????????? ??? ????????? ????????? ??????
        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE) // ?????? ?????? ?????? ???????????? ?????? ????????? ??????
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA) // ??????????????? ?????? ?????? ???????????? ?????? ????????? ??????
                        .addDataType(DataType.TYPE_CALORIES_EXPENDED) // ????????? ??????????????? ???????????? ?????? ????????? ??????
                        .addDataType(DataType.TYPE_DISTANCE_DELTA) // ?????? ????????? ???????????? ?????? ????????? ??????
                        .addDataType(DataType.TYPE_HEART_POINTS) //?????? ????????? ???????????? ?????? ????????? ??????
                        .build();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION},0);
        }

        //?????? ?????????
        if(!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            //????????? ????????? ??? ????????? ?????? ??????
            subscribe();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //????????? ?????????
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
            health_data_day[i] = -1;
        }
        health_data_week = new int[12];
        for(int i=0; i<12; i++){
            health_data_week[i] = -1;
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
                                                String st1 = "??????" + Integer.toString(i);
                                                String st2 = "?????? ???" + Integer.toString(i);
                                                Log.w(st1, stepsOf3weeks[i]);
                                                Log.w(st2, stepsOf3weeks[i+1]);
                                            }*/
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.putExtra("health_info_day", health_data_day);
                                            intent.putExtra("health_info_week", health_data_week);
                                            service.initialData(new InitialDataRequest(preferences.getString("userId", ""), stepsOf3weeks)).enqueue(new Callback<CodeResponse>() {

                                                @Override
                                                public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                                                    CodeResponse code = response.body();                   // ???????????? body??? ????????? ?????? code??? ?????? ????????? ??????
                                                    if (code.getCode() == 200) {                            // ???????????? ?????? ??????
                                                        //intent.putExtra("targetSteps", targetSteps.getTargetSteps());
                                                        Log.w("?????????", "?????? ????????? ????????? ?????? ?????? ??? ?????? ??????");
                                                    }
                                                    else                                                    // ???????????? ?????? ??????
                                                        Toast.makeText(LoginActivity.this, "???????????? ?????? ??????", Toast.LENGTH_SHORT).show();
                                                    login_progressbar.setVisibility(View.INVISIBLE);        // ????????? ????????? ???????????? progressbar ????????????
                                                }

                                                @Override
                                                public void onFailure(Call<CodeResponse> call, Throwable t) {
                                                    Toast.makeText(LoginActivity.this, "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
                                                    Log.e("?????? ?????? ??????", t.getMessage());
                                                    login_progressbar.setVisibility(View.INVISIBLE);        // ????????? ????????? ??????, progressbar ????????????
                                                }
                                            });
                                            Calendar cal = Calendar.getInstance();
                                            SharedPreferences.Editor editor = preferences.edit();
                                            //?????????????????? ??????
                                            if(isAutomatic){
                                                editor.putString("last_last_login_time", preferences.getString("last_login_time", ""));
                                                editor.putString("last_login_time", cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH));
                                            }
                                            //??????????????? ??????
                                            else{
                                                editor.putString("last_login_time", cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH));
                                            }
                                            int count = 0;
                                            int temp = 0;
                                            while(count < 7){
                                                if(health_data_day[count] == -1){
                                                    break;
                                                }
                                                count+=1;
                                            }
                                            editor.putString("userStep", String.valueOf(health_data_day[count-1]));
                                            editor.commit();
                                            login_progressbar.setVisibility(View.INVISIBLE);        // ????????? ????????? ???????????? progressbar ????????????
                                            startActivity(intent);                              // ??????????????? Main ??????????????? ???????????? ??? ???????????? ??????
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
        //???  ???  ???  ???  ???  ???  ???
        //??? : 1, ??? : 2, ??? : 3, ??? : 4, ??? : 5, ??? : 6, ??? : 7
        int currentDayofWeek = cal.get(Calendar.DAY_OF_WEEK); //?????? ??????
        if(currentDayofWeek == 1){
            for (int count = 6; count >= 0; count--) {
                final Calendar cal2 = Calendar.getInstance();
                Date now2 = Calendar.getInstance().getTime();
                cal2.setTime(now2);
                // ?????? ??????
                cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH),
                        cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal2.add(Calendar.DAY_OF_MONTH, -count);
                long startTime = cal2.getTimeInMillis();

                final Calendar cal3 = Calendar.getInstance();
                Date now3 = Calendar.getInstance().getTime();
                cal3.setTime(now3);
                // ?????? ??????
                cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                        cal3.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                cal3.add(Calendar.DAY_OF_MONTH, -count);
                long endTime = cal3.getTimeInMillis();

                int finalCount = 6-count;
                Fitness.getHistoryClient(this,
                                GoogleSignIn.getLastSignedInAccount(this))
                        .readData(new DataReadRequest.Builder()
                                .read(DataType.TYPE_STEP_COUNT_DELTA) // Raw ?????? ???
                                .read(DataType.TYPE_HEART_POINTS) // Raw ?????? ??????
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

                                //?????? ?????? ??? ??? ?????? ?????? ??????(?????? ?????? 6???~22???)
                                for (DataPoint dpStep : dataSetStepCount.getDataPoints()) {
                                    totalStepTimeNumber += dpStep.getEndTime(TimeUnit.MILLISECONDS) - dpStep.getStartTime(TimeUnit.MILLISECONDS);

                                    for (Field field : dpStep.getDataType().getFields()) {
                                        totalStep += dpStep.getValue(field).asInt();
                                    }
                                }

                                //?????? ????????? ????????? ????????? ???????????? ??????
                                if (totalStepTimeNumber > 0) {
                                    totalStepTimeString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalStepTimeNumber),
                                            TimeUnit.MILLISECONDS.toMinutes(totalStepTimeNumber) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalStepTimeNumber)),
                                            TimeUnit.MILLISECONDS.toSeconds(totalStepTimeNumber) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalStepTimeNumber)));
                                }

                                //??? ?????? ?????? ??????
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
                // ?????? ??????
                cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH),
                        cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal2.add(Calendar.DAY_OF_MONTH, -currentDayofWeek + count);
                long startTime = cal2.getTimeInMillis();

                final Calendar cal3 = Calendar.getInstance();
                Date now3 = Calendar.getInstance().getTime();
                cal3.setTime(now3);
                // ?????? ??????
                cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                        cal3.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                cal3.add(Calendar.DAY_OF_MONTH, -currentDayofWeek + count);
                long endTime = cal3.getTimeInMillis();

                int finalCount = count - 2;
                Fitness.getHistoryClient(this,
                                GoogleSignIn.getLastSignedInAccount(this))
                        .readData(new DataReadRequest.Builder()
                                .read(DataType.TYPE_STEP_COUNT_DELTA) // Raw ?????? ???
                                .read(DataType.TYPE_HEART_POINTS) // Raw ?????? ??????
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

                                //?????? ?????? ??? ??? ?????? ?????? ??????(?????? ?????? 6???~22???)
                                for (DataPoint dpStep : dataSetStepCount.getDataPoints()) {
                                    totalStepTimeNumber += dpStep.getEndTime(TimeUnit.MILLISECONDS) - dpStep.getStartTime(TimeUnit.MILLISECONDS);

                                    for (Field field : dpStep.getDataType().getFields()) {
                                        totalStep += dpStep.getValue(field).asInt();
                                    }
                                }

                                //?????? ????????? ????????? ????????? ???????????? ??????
                                if (totalStepTimeNumber > 0) {
                                    totalStepTimeString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalStepTimeNumber),
                                            TimeUnit.MILLISECONDS.toMinutes(totalStepTimeNumber) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalStepTimeNumber)),
                                            TimeUnit.MILLISECONDS.toSeconds(totalStepTimeNumber) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalStepTimeNumber)));
                                }

                                //??? ?????? ?????? ??????
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
        int currentDay = cal.get(Calendar.DATE); //?????? ???????????? ???????????? ??????(20)
        int currentDayofWeek = cal.get(Calendar.DAY_OF_WEEK); //?????? ??????(3)
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
                    // ?????? ??????
                    cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH),
                            cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                    cal2.add(Calendar.DAY_OF_MONTH, -currentDayofWeek + count);
                    long startTime = cal2.getTimeInMillis();

                    final Calendar cal3 = Calendar.getInstance();
                    Date now3 = Calendar.getInstance().getTime();
                    cal3.setTime(now3);
                    // ?????? ??????
                    cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                            cal3.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                    cal3.add(Calendar.DAY_OF_MONTH, -currentDayofWeek + count);
                    long endTime = cal3.getTimeInMillis();

                    int finalCount = i- 1;
                    Fitness.getHistoryClient(this,
                                    GoogleSignIn.getLastSignedInAccount(this))
                            .readData(new DataReadRequest.Builder()
                                    .read(DataType.TYPE_STEP_COUNT_DELTA) // Raw ?????? ???
                                    .read(DataType.TYPE_HEART_POINTS) // Raw ?????? ??????
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

                                    //?????? ?????? ??? ??? ?????? ?????? ??????(?????? ?????? 6???~22???)
                                    for (DataPoint dpStep : dataSetStepCount.getDataPoints()) {
                                        totalStepTimeNumber += dpStep.getEndTime(TimeUnit.MILLISECONDS) - dpStep.getStartTime(TimeUnit.MILLISECONDS);

                                        for (Field field : dpStep.getDataType().getFields()) {
                                            totalStep += dpStep.getValue(field).asInt();
                                        }
                                    }

                                    //?????? ????????? ????????? ????????? ???????????? ??????
                                    if (totalStepTimeNumber > 0) {
                                        totalStepTimeString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalStepTimeNumber),
                                                TimeUnit.MILLISECONDS.toMinutes(totalStepTimeNumber) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalStepTimeNumber)),
                                                TimeUnit.MILLISECONDS.toSeconds(totalStepTimeNumber) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalStepTimeNumber)));
                                    }

                                    //??? ?????? ?????? ??????
                                    for (DataPoint dpHea : dataSetHeartPoint.getDataPoints()) {
                                        for (Field field : dpHea.getDataType().getFields()) {
                                            totalHeartPointsFloat += dpHea.getValue(field).asFloat();
                                        }
                                    }
                                    totalHeartPointsInt = (int) Math.floor(totalHeartPointsFloat);

                                    health_data_week[finalCount] += totalStep;
                                    health_data_week[finalCount + 6] += totalHeartPointsInt;
                                    //Log.w("?????????", Integer.toString(finalCount));
                                    //Log.w("?????????", Integer.toString(totalStep));
                                }
                            });
                }
            }
            else{
                for (int count = 1; count <= 7; count++) {
                    final Calendar cal3 = Calendar.getInstance();
                    Date now3 = Calendar.getInstance().getTime();
                    cal3.setTime(now3);
                    // ?????? ??????
                    cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                            cal3.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                    cal3.add(Calendar.DAY_OF_MONTH, -currentDayofWeek - 7*(temp-i) + count);
                    long startTime = cal3.getTimeInMillis();

                    final Calendar cal4 = Calendar.getInstance();
                    Date now4 = Calendar.getInstance().getTime();
                    cal4.setTime(now4);
                    // ?????? ??????
                    cal4.set(cal4.get(Calendar.YEAR), cal4.get(Calendar.MONTH),
                            cal4.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                    cal4.add(Calendar.DAY_OF_MONTH, -currentDayofWeek - 7*(temp-i) + count);
                    long endTime = cal4.getTimeInMillis();

                    int finalCount = i- 1;
                    Fitness.getHistoryClient(this,
                                    GoogleSignIn.getLastSignedInAccount(this))
                            .readData(new DataReadRequest.Builder()
                                    .read(DataType.TYPE_STEP_COUNT_DELTA) // Raw ?????? ???
                                    .read(DataType.TYPE_HEART_POINTS) // Raw ?????? ??????
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

                                    //?????? ?????? ??? ??? ?????? ?????? ??????(?????? ?????? 6???~22???)
                                    for (DataPoint dpStep : dataSetStepCount.getDataPoints()) {
                                        totalStepTimeNumber += dpStep.getEndTime(TimeUnit.MILLISECONDS) - dpStep.getStartTime(TimeUnit.MILLISECONDS);

                                        for (Field field : dpStep.getDataType().getFields()) {
                                            totalStep += dpStep.getValue(field).asInt();
                                        }
                                    }

                                    //?????? ????????? ????????? ????????? ???????????? ??????
                                    if (totalStepTimeNumber > 0) {
                                        totalStepTimeString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalStepTimeNumber),
                                                TimeUnit.MILLISECONDS.toMinutes(totalStepTimeNumber) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalStepTimeNumber)),
                                                TimeUnit.MILLISECONDS.toSeconds(totalStepTimeNumber) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalStepTimeNumber)));
                                    }

                                    //??? ?????? ?????? ??????
                                    for (DataPoint dpHea : dataSetHeartPoint.getDataPoints()) {
                                        for (Field field : dpHea.getDataType().getFields()) {
                                            totalHeartPointsFloat += dpHea.getValue(field).asFloat();
                                        }
                                    }
                                    totalHeartPointsInt = (int) Math.floor(totalHeartPointsFloat);

                                    health_data_week[finalCount] += totalStep;
                                    health_data_week[finalCount + 6] += totalHeartPointsInt;
                                    //Log.w("?????????", Integer.toString(finalCount));
                                    //Log.w("?????????", Integer.toString(totalStep));
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
        //???  ???  ???  ???  ???  ???  ???
        //??? : 1, ??? : 2, ??? : 3, ??? : 4, ??? : 5, ??? : 6, ??? : 7
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for(int i=0; i<42; i++)
            stepsOf3weeks[i] = "-1";
        for (int count = 21; count > 0; count--) {
            final Calendar cal2 = Calendar.getInstance();
            Date now2 = Calendar.getInstance().getTime();
            cal2.setTime(now2);
            // ?????? ??????
            cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH),
                    cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            cal2.add(Calendar.DAY_OF_MONTH, -count);
            long startTime = cal2.getTimeInMillis();

            final Calendar cal3 = Calendar.getInstance();
            Date now3 = Calendar.getInstance().getTime();
            cal3.setTime(now3);
            // ?????? ??????
            cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                    cal3.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            cal3.add(Calendar.DAY_OF_MONTH, -count);
            long endTime = cal3.getTimeInMillis();
            String mydate = sdf.format(cal3.getTime());
            int finalCount = count;
            Fitness.getHistoryClient(this,
                            GoogleSignIn.getLastSignedInAccount(this))
                    .readData(new DataReadRequest.Builder()
                            .read(DataType.TYPE_STEP_COUNT_DELTA) // Raw ?????? ???
                            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                            .build())
                    .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                        @Override
                        public void onSuccess(DataReadResponse response) {
                            int totalStep = 0;
                            DataSet dataSetStepCount = response.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);

                            //?????? ?????? ??? ??? ?????? ?????? ??????(?????? ?????? 6???~22???)
                            for (DataPoint dpStep : dataSetStepCount.getDataPoints()) {
                                for (Field field : dpStep.getDataType().getFields()) {
                                    totalStep += dpStep.getValue(field).asInt();
                                }
                            }
                            stepsOf3weeks[(21- finalCount)*2] = mydate;
                            stepsOf3weeks[(21- finalCount)*2+1] = Integer.toString(totalStep);
                            //Log.w("??????", mydate);
                            //Log.w("?????? ???", Integer.toString(totalStep));
                        }
                    });
        }
    }
    private void readDataUntilLastLogin(){
        //YYYY-MM-dd
        String LastLogin = preferences.getString("last_login_time", "");
        if(LastLogin == ""){
            Log.w("Error", "?????? ????????? ??? ????????? ?????????");
        }
        else{
            //?????? ?????? ?????????(?????? ?????? ????????? ??? ?????? ~ ?????? ??????)
            Calendar baseCal = new GregorianCalendar(Integer.parseInt(LastLogin.substring(0,4)), Integer.parseInt(LastLogin.substring(5,7))-1, Integer.parseInt(LastLogin.substring(8)));
            Calendar targetCal = new GregorianCalendar(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
            long diffSec = (targetCal.getTimeInMillis() - baseCal.getTimeInMillis())/1000;
            long diffDays = diffSec / (24*60*60);

            //?????? 3?????? ????????? ????????? ????????? ??????
            if(diffDays > 21)
                diffDays = 21;

            int diff = (int)diffDays;

            final Calendar cal = Calendar.getInstance();
            Date now = Calendar.getInstance().getTime();
            cal.setTime(now);
            //???  ???  ???  ???  ???  ???  ???
            //??? : 1, ??? : 2, ??? : 3, ??? : 4, ??? : 5, ??? : 6, ??? : 7
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for(int i=0; i<42; i++)
                stepsOf3weeks[i] = "-1";
            int index = 0;
            for (int count = diff; count > 0; count--) {
                final Calendar cal2 = Calendar.getInstance();
                Date now2 = Calendar.getInstance().getTime();
                cal2.setTime(now2);
                // ?????? ??????
                cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH),
                        cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal2.add(Calendar.DAY_OF_MONTH, -count);
                long startTime = cal2.getTimeInMillis();

                final Calendar cal3 = Calendar.getInstance();
                Date now3 = Calendar.getInstance().getTime();
                cal3.setTime(now3);
                // ?????? ??????
                cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                        cal3.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                cal3.add(Calendar.DAY_OF_MONTH, -count);
                long endTime = cal3.getTimeInMillis();
                String mydate = sdf.format(cal3.getTime());

                int finalIndex = index;
                Fitness.getHistoryClient(this,
                                GoogleSignIn.getLastSignedInAccount(this))
                        .readData(new DataReadRequest.Builder()
                                .read(DataType.TYPE_STEP_COUNT_DELTA) // Raw ?????? ???
                                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                                .build())
                        .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse response) {
                                int totalStep = 0;
                                DataSet dataSetStepCount = response.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);

                                //?????? ?????? ??? ??? ?????? ?????? ??????(?????? ?????? 6???~22???)
                                for (DataPoint dpStep : dataSetStepCount.getDataPoints()) {
                                    for (Field field : dpStep.getDataType().getFields()) {
                                        totalStep += dpStep.getValue(field).asInt();
                                    }
                                }
                                stepsOf3weeks[finalIndex] = mydate;
                                stepsOf3weeks[finalIndex+1] = Integer.toString(totalStep);
                                //Log.w("??????", mydate);
                                //Log.w("?????? ???", Integer.toString(totalStep));
                            }
                        });
                index+=2;
            }
        }
    }
}