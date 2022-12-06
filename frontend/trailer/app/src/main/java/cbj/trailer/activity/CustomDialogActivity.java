package cbj.trailer.activity;

import static android.app.PendingIntent.getActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cbj.trailer.R;
import cbj.trailer.data.CodeResponse;
import cbj.trailer.data.InitialLookUpRequest;
import cbj.trailer.data.TargetWalk;
import cbj.trailer.data.TargetWalkResponse;
import cbj.trailer.network.RetrofitClient;
import cbj.trailer.network.ServiceApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomDialogActivity extends AppCompatActivity {

    private TextView predict_mon_num;
    private TextView predict_tue_num;
    private TextView predict_wed_num;
    private TextView predict_thur_num;
    private TextView predict_fri_num;
    private TextView predict_sat_num;
    private TextView predict_sun_num;

    private EditText input_mon;
    private EditText input_tue;
    private EditText input_wed;
    private EditText input_thur;
    private EditText input_fri;
    private EditText input_sat;
    private EditText input_sun;

    private boolean mon_check;
    private boolean tue_check;
    private boolean wed_check;
    private boolean thur_check;
    private boolean fri_check;
    private boolean sat_check;
    private boolean sun_check;

    private String person_id;

    private double mon = 3;
    private double tue = 3;
    private double wed = 3;
    private double thur = 3;
    private double fri = 3;
    private double sat = 3;
    private double sun = 3;

    private Button delete_btn;
    private Button save_btn;
    private ServiceApi service;
    private SharedPreferences preferences;


    public void targetWalk(InitialLookUpRequest data) {
        service.userTargetWalk(data).enqueue(new Callback<TargetWalkResponse>() {
            @Override
            public void onResponse(Call<TargetWalkResponse> call, Response<TargetWalkResponse> response) {
                TargetWalkResponse resultCode = response.body();
                if (resultCode.getResultCode() == 200) {
                    List<TargetWalk> data = resultCode.getMid();

                    for (int i = 0; i < data.size(); i++) { // JSON data 돌면서 수행
                        if (data.get(i).getDayOfWeek() == "mon") { // 월요일이면
                            predict_mon_num.setText(String.valueOf(data.get(i).getSteps())); // 월요일 걸음 수 받아오기
                        }
                        if (data.get(i).getDayOfWeek() == "tue") { // 화요일이면
                            predict_tue_num.setText(String.valueOf(data.get(i).getSteps())); // 화요일 걸음 수 받아오기
                        }
                        if (data.get(i).getDayOfWeek() == "wed") { // 수요일이면
                            predict_wed_num.setText(String.valueOf(data.get(i).getSteps())); // 수요일 걸음 수 받아오기
                        }
                        if (data.get(i).getDayOfWeek() == "thur") { // 목요일이면
                            predict_thur_num.setText(String.valueOf(data.get(i).getSteps())); // 목요일 걸음 수 받아오기
                        }
                        if (data.get(i).getDayOfWeek() == "fri") { // 금요일이면
                            predict_fri_num.setText(String.valueOf(data.get(i).getSteps())); // 금요일 걸음 수 받아오기
                        }
                        if (data.get(i).getDayOfWeek() == "sat") { // 토요일이면
                            predict_sat_num.setText(String.valueOf(data.get(i).getSteps())); // 토요일 걸음 수 받아오기
                        }
                        if (data.get(i).getDayOfWeek() == "sun") { // 일요일이면
                            predict_sun_num.setText(String.valueOf(data.get(i).getSteps())); // 일요일 걸음 수 받아오기
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TargetWalkResponse> call, Throwable t) {
                Toast.makeText(CustomDialogActivity.this, "통신 오류 발생", Toast.LENGTH_SHORT).show();
                Log.e("통신 오류 발생", t.getMessage());
            }
        });
    }

    protected void onCreate (Bundle savedInstance){
//        Bundle savedInstanceState;
        Bundle savedInstanceState = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dialog);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);

        input_mon = findViewById(R.id.mon);
        input_tue = findViewById(R.id.tue);
        input_wed = findViewById(R.id.wed);
        input_thur = findViewById(R.id.thur);
        input_fri = findViewById(R.id.fri);
        input_sat = findViewById(R.id.sat);
        input_sun = findViewById(R.id.sun);

        predict_mon_num = findViewById(R.id.predict_mon_num);
        predict_tue_num = findViewById(R.id.predict_tue_num);
        predict_wed_num = findViewById(R.id.predict_wed_num);
        predict_thur_num = findViewById(R.id.predict_thur_num);
        predict_fri_num = findViewById(R.id.predict_fri_num);
        predict_sat_num = findViewById(R.id.predict_sat_num);
        predict_sun_num = findViewById(R.id.predict_sun_num);


        delete_btn = findViewById(R.id.btn_delete);
        save_btn = findViewById(R.id.btn_save);

        person_id = preferences.getString("userId", "");
        targetWalk(new InitialLookUpRequest(person_id));

        input_mon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                mon_check = false;
                boolean output = true;
                String str_mon = s.toString();
                if (!str_mon.equals("")) {
                    for (int i = 0; i < str_mon.length(); i++) {
                        char tmp = str_mon.charAt(i);
                        if (!('1' <= tmp && tmp <= '5')) {
                            output = false;
                        }
                    }
                }
                else if(str_mon.equals("")){
                    mon = Double.parseDouble(predict_mon_num.getText().toString()) * 1.0;
                }
                if (output){
                    int real_score = Integer.parseInt(str_mon);
                    if (real_score == 1) {
                        mon_check = true;
                        mon = Double.parseDouble(predict_mon_num.getText().toString()) * 0.5;
                    }
                    else if (real_score == 2) {
                        mon_check = true;
                        mon = Double.parseDouble(predict_mon_num.getText().toString()) * 0.8;
                    }
                    else if (real_score == 3){
                        mon_check = true;
                        mon = Double.parseDouble(predict_mon_num.getText().toString()) * 1.0;
                    }
                    else if (real_score == 4){
                        mon_check = true;
                        mon = Double.parseDouble(predict_mon_num.getText().toString()) * 1.2;
                    }
                    else if (real_score == 5){
                        mon_check = true;
                        mon = Double.parseDouble(predict_mon_num.getText().toString()) * 1.5;
                    }
                    else
                        mon_check = false;
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("mon_steps", (int) mon);
                editor.commit();
            }
        });

        input_tue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                tue_check = false;
                boolean output = true;
                String str_tue = s.toString();
                if (!str_tue.equals("")) {
                    for (int i = 0; i < str_tue.length(); i++) {
                        char tmp = str_tue.charAt(i);
                        if (!('1' <= tmp && tmp <= '5')) {
                            output = false;
                        }
                    }
                }
                else if(str_tue.equals("")){
                    tue = Double.parseDouble(predict_tue_num.getText().toString()) * 1.0;
                }
                if (output){
                    int real_score = Integer.parseInt(str_tue);
                    if (real_score == 1) {
                        tue_check = true;
                        tue = Double.parseDouble(predict_tue_num.getText().toString()) * 0.5;
                    }
                    else if (real_score == 2) {
                        tue_check = true;
                        tue = Double.parseDouble(predict_tue_num.getText().toString()) * 0.8;
                    }
                    else if (real_score == 3){
                        tue_check = true;
                        tue = Double.parseDouble(predict_tue_num.getText().toString()) * 1.0;
                    }
                    else if (real_score == 4){
                        tue_check = true;
                        tue = Double.parseDouble(predict_tue_num.getText().toString()) * 1.2;
                    }
                    else if (real_score == 5){
                        tue_check = true;
                        tue = Double.parseDouble(predict_tue_num.getText().toString()) * 1.5;
                    }
                    else
                        tue_check = false;
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("tue_steps", (int) tue);
                editor.commit();
            }
        });

        input_wed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                wed_check = false;
                boolean output = true;
                String str_wed = s.toString();
                if (!str_wed.equals("")) {
                    for (int i = 0; i < str_wed.length(); i++) {
                        char tmp = str_wed.charAt(i);
                        if (!('1' <= tmp && tmp <= '5')) {
                            output = false;
                        }
                    }
                }
                else if(str_wed.equals("")){
                    wed = Double.parseDouble(predict_wed_num.getText().toString()) * 1.0;
                }
                if (output){
                    int real_score = Integer.parseInt(str_wed);
                    if (real_score == 1) {
                        wed_check = true;
                        wed = Double.parseDouble(predict_wed_num.getText().toString()) * 0.5;
                    }
                    else if (real_score == 2) {
                        wed_check = true;
                        wed = Double.parseDouble(predict_wed_num.getText().toString()) * 0.8;
                    }
                    else if (real_score == 3){
                        wed_check = true;
                        wed = Double.parseDouble(predict_wed_num.getText().toString()) * 1.0;
                    }
                    else if (real_score == 4){
                        wed_check = true;
                        wed = Double.parseDouble(predict_wed_num.getText().toString()) * 1.2;
                    }
                    else if (real_score == 5){
                        wed_check = true;
                        wed = Double.parseDouble(predict_wed_num.getText().toString()) * 1.5;
                    }
                    else
                        wed_check = false;
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("wed_steps", (int) wed);
                editor.commit();
            }
        });

        input_thur.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                thur_check = false;
                boolean output = true;
                String str_thur = s.toString();
                if (!str_thur.equals("")) {
                    for (int i = 0; i < str_thur.length(); i++) {
                        char tmp = str_thur.charAt(i);
                        if (!('1' <= tmp && tmp <= '5')) {
                            output = false;
                        }
                    }
                }
                else if(str_thur.equals("")){
                    thur = Double.parseDouble(predict_thur_num.getText().toString()) * 1.0;
                }
                if (output){
                    int real_score = Integer.parseInt(str_thur);
                    if (real_score == 1) {
                        thur_check = true;
                        thur = Double.parseDouble(predict_thur_num.getText().toString()) * 0.5;
                    }
                    else if (real_score == 2) {
                        thur_check = true;
                        thur = Double.parseDouble(predict_thur_num.getText().toString()) * 0.8;
                    }
                    else if (real_score == 3){
                        thur_check = true;
                        thur = Double.parseDouble(predict_thur_num.getText().toString()) * 1.0;
                    }
                    else if (real_score == 4){
                        thur_check = true;
                        thur = Double.parseDouble(predict_thur_num.getText().toString()) * 1.2;
                    }
                    else if (real_score == 5){
                        thur_check = true;
                        thur = Double.parseDouble(predict_thur_num.getText().toString()) * 1.5;
                    }
                    else
                        thur_check = false;
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("thur_steps", (int) thur);
                editor.commit();
            }
        });

        input_fri.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                fri_check = false;
                boolean output = true;
                String str_fri = s.toString();
                if (!str_fri.equals("")) {
                    for (int i = 0; i < str_fri.length(); i++) {
                        char tmp = str_fri.charAt(i);
                        if (!('1' <= tmp && tmp <= '5')) {
                            output = false;
                        }
                    }
                }
                else if(str_fri.equals("")){
                    fri = Double.parseDouble(predict_fri_num.getText().toString()) * 1.0;
                }
                if (output){
                    int real_score = Integer.parseInt(str_fri);
                    if (real_score == 1) {
                        fri_check = true;
                        fri = Double.parseDouble(predict_fri_num.getText().toString()) * 0.5;
                    }
                    else if (real_score == 2) {
                        fri_check = true;
                        fri = Double.parseDouble(predict_fri_num.getText().toString()) * 0.8;
                    }
                    else if (real_score == 3){
                        fri_check = true;
                        fri = Double.parseDouble(predict_fri_num.getText().toString()) * 1.0;
                    }
                    else if (real_score == 4){
                        fri_check = true;
                        fri = Double.parseDouble(predict_fri_num.getText().toString()) * 1.2;
                    }
                    else if (real_score == 5){
                        fri_check = true;
                        fri = Double.parseDouble(predict_fri_num.getText().toString()) * 1.5;
                    }
                    else
                        fri_check = false;
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("fri_steps", (int) fri);
                editor.commit();
            }
        });

        input_sat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                sat_check = false;
                boolean output = true;
                String str_sat = s.toString();
                if (!str_sat.equals("")) {
                    for (int i = 0; i < str_sat.length(); i++) {
                        char tmp = str_sat.charAt(i);
                        if (!('1' <= tmp && tmp <= '5')) {
                            output = false;
                        }
                    }
                }
                else if(str_sat.equals("")){
                    sat = Double.parseDouble(predict_sat_num.getText().toString()) * 1.0;
                }
                if (output){
                    int real_score = Integer.parseInt(str_sat);
                    if (real_score == 1) {
                        sat_check = true;
                        sat = Double.parseDouble(predict_sat_num.getText().toString()) * 0.5;
                    }
                    else if (real_score == 2) {
                        sat_check = true;
                        sat = Double.parseDouble(predict_sat_num.getText().toString()) * 0.8;
                    }
                    else if (real_score == 3){
                        sat_check = true;
                        sat = Double.parseDouble(predict_sat_num.getText().toString()) * 1.0;
                    }
                    else if (real_score == 4){
                        sat_check = true;
                        sat = Double.parseDouble(predict_sat_num.getText().toString()) * 1.2;
                    }
                    else if (real_score == 5){
                        sat_check = true;
                        sat = Double.parseDouble(predict_sat_num.getText().toString()) * 1.5;
                    }
                    else
                        sat_check = false;
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("sat_steps", (int) sat);
                editor.commit();
            }
        });

        input_sun.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                sun_check = false;
                boolean output = true;
                String str_sun = s.toString();
                if (!str_sun.equals("")) {
                    for (int i = 0; i < str_sun.length(); i++) {
                        char tmp = str_sun.charAt(i);
                        if (!('1' <= tmp && tmp <= '5')) {
                            output = false;
                        }
                    }
                }
                else if(str_sun.equals("")){
                    sun = Double.parseDouble(predict_sun_num.getText().toString()) * 1.0;

                }
                if (output){
                    int real_score = Integer.parseInt(str_sun);
                    if (real_score == 1) {
                        sun_check = true;
                        sun = Double.parseDouble(predict_sun_num.getText().toString()) * 0.5;
                    }
                    else if (real_score == 2) {
                        sun_check = true;
                        sun = Double.parseDouble(predict_sun_num.getText().toString()) * 0.8;
                    }
                    else if (real_score == 3){
                        sun_check = true;
                        sun = Double.parseDouble(predict_sun_num.getText().toString()) * 1.0;
                    }
                    else if (real_score == 4){
                        sun_check = true;
                        sun = Double.parseDouble(predict_sun_num.getText().toString()) * 1.2;
                    }
                    else if (real_score == 5){
                        sun_check = true;
                        sun = Double.parseDouble(predict_sun_num.getText().toString()) * 1.5;
                    }
                    else
                        sun_check = false;
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("sun_steps", (int) sun);
                editor.commit();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSave(new InitialLookUpRequest(person_id));
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDelete(new InitialLookUpRequest(person_id));
            }
        });
    }
    public void startSave(InitialLookUpRequest data){
        service.userTargetWalk(data).enqueue(new Callback<TargetWalkResponse>() {
            @Override
            public void onResponse(Call<TargetWalkResponse> call, Response<TargetWalkResponse> response) {
                TargetWalkResponse resultCode = response.body();
                if(resultCode.getResultCode() == 200){
                    Toast.makeText(CustomDialogActivity.this, "입력한 내용이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<TargetWalkResponse> call, Throwable t) {
                Toast.makeText(CustomDialogActivity.this, "통신 오류 발생", Toast.LENGTH_SHORT).show();
                Log.e("통신 오류 발생", t.getMessage());
            }
        });
    }

    public void startDelete(InitialLookUpRequest data){
        service.userTargetWalk(data).enqueue(new Callback<TargetWalkResponse>() {
            @Override
            public void onResponse(Call<TargetWalkResponse> call, Response<TargetWalkResponse> response) {
                TargetWalkResponse resultCode = response.body();
                if(resultCode.getResultCode() == 200){
                    Toast.makeText(CustomDialogActivity.this, "자동으로 3점이 입력됩니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<TargetWalkResponse> call, Throwable t) {
                Toast.makeText(CustomDialogActivity.this, "통신 오류 발생", Toast.LENGTH_SHORT).show();
                Log.e("통신 오류 발생", t.getMessage());
            }
        });
    }
}