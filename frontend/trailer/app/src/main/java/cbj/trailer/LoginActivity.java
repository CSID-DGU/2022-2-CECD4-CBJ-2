package cbj.trailer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText login_id;
    private EditText login_pwd;
    private Button login_btn;
    private Button join_btn;
    private String id;
    private String pwd;
    private boolean input_id;
    private boolean input_pwd;
    private ProgressBar login_progressbar;
    private final int REQUEST_OAUTH_REQUEST_CODE = 1;
    private final String TAG = "BasicHistoryAPI";
    private int [] health_data = {0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);                        // xml, java 연결

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
                //Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                //startActivity(intent);                                  // 회원가입 액티비티로 넘어감
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void tryLogin() {
        id = login_id.getText().toString();                             // id에 입력된 input을 넣어줌
        pwd = login_pwd.getText().toString();                           // password에 입력된 input을 넣어줌

        //login_progressbar.setVisibility(View.VISIBLE);                  // progressbar를 활성화 해주고
        //startLogin(new LoginRequest(id, pwd));                          // 로그인을 시작함
        startLogin();
    }

    /**
    private void startLogin(LoginRequest data) {                        // 로그인을 하는 함수(이전에 설명했으므로 요약함)
        service.userLogin(data).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse user = response.body();                   // 응답받은 body의 객체를 넣고 code에 따라 활동이 나뉨
                if (user.getCode() == 200) {                            // 로그인 성공이라면
                    Toast.makeText(LoginActivity.this, user.getUserName() + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("UserID", id);
                    intent.putExtra("UserName", user.getUserName());
                    startActivity(intent);                              // 성공이라면 Main 액티비티로 넘어가고 현 액티비티 종료
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
    }*/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startLogin() {                        // 로그인을 하는 함수(이전에 설명했으므로 요약함)
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
        System.out.println("MainActivity.subscribe");
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task){
                                if (task.isSuccessful()){
                                    Log.w(TAG, "Successfully subscribed!");
                                    readDataMy();
                                    Log.w("걸음수2", Integer.toString(health_data[0]));
                                    Log.w("걸음수2", Integer.toString(health_data[1]));
                                    Log.w("걸음수2", Integer.toString(health_data[2]));
                                    Log.w("걸음수2", Integer.toString(health_data[3]));
                                    Log.w("걸음수2", Integer.toString(health_data[4]));
                                    Log.w("걸음수2", Integer.toString(health_data[5]));
                                    Log.w("걸음수2", Integer.toString(health_data[6]));
                                    Log.w("걸음수2", Integer.toString(health_data[7]));
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("health_info", health_data);
                                    startActivity(intent);                              // 성공이라면 Main 액티비티로 넘어가고 현 액티비티 종료
                                    finish();
                                }else{
                                    Log.w("ErrorError:", task.getException());
                                }
                            }
                        }
                );
    }

    private synchronized void readDataMy() {
        final Calendar cal = Calendar.getInstance();
        Date now = Calendar.getInstance().getTime();
        cal.setTime(now);

        int currentDayofWeek = cal.get(Calendar.DAY_OF_WEEK);
        for(int count = 1; count <= currentDayofWeek; count++) {
            final Calendar cal2 = Calendar.getInstance();
            Date now2 = Calendar.getInstance().getTime();
            cal2.setTime(now2);
            // 시작 시간
            cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH),
                    cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            cal2.add(Calendar.DAY_OF_MONTH, -currentDayofWeek+count);
            long startTime = cal2.getTimeInMillis();

            final Calendar cal3 = Calendar.getInstance();
            Date now3 = Calendar.getInstance().getTime();
            cal3.setTime(now3);
            // 종료 시간
            cal3.set(cal3.get(Calendar.YEAR), cal3.get(Calendar.MONTH),
                    cal3.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            cal3.add(Calendar.DAY_OF_MONTH, -currentDayofWeek+count);
            long endTime = cal3.getTimeInMillis();

            int finalCount = count-1;
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