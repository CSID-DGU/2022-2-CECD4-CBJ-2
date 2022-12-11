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
    private String person_id;

    private Button save_btn;
    private ServiceApi service;
    private SharedPreferences preferences;

    @Override
    protected void onCreate (Bundle savedInstanceState){
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


        save_btn = findViewById(R.id.btn_save);

        person_id = preferences.getString("userId", "");
        targetWalk(new InitialLookUpRequest(person_id));

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStepScore(input_mon.getText().toString()) && isStepScore(input_tue.getText().toString())&&
                        isStepScore(input_wed.getText().toString()) && isStepScore(input_thur.getText().toString()) && isStepScore(input_fri.getText().toString())
                && isStepScore(input_sat.getText().toString()) && isStepScore(input_sun.getText().toString())){
                    SharedPreferences.Editor editor = preferences.edit();
                    if(input_mon.getText().toString().equals(""))
                        editor.putString("input_mon", "3");
                    else
                        editor.putString("input_mon", input_mon.getText().toString());
                    if(input_tue.getText().toString().equals(""))
                        editor.putString("input_tue", "3");
                    else
                        editor.putString("input_tue", input_tue.getText().toString());
                    if(input_wed.getText().toString().equals(""))
                        editor.putString("input_wed", "3");
                    else
                        editor.putString("input_wed", input_wed.getText().toString());
                    if(input_thur.getText().toString().equals(""))
                        editor.putString("input_thu", "3");
                    else
                        editor.putString("input_thu", input_thur.getText().toString());
                    if(input_fri.getText().toString().equals(""))
                        editor.putString("input_fri", "3");
                    else
                        editor.putString("input_fri", input_fri.getText().toString());
                    if(input_sat.getText().toString().equals(""))
                        editor.putString("input_sat", "3");
                    else
                        editor.putString("input_sat", input_sat.getText().toString());
                    if(input_sun.getText().toString().equals(""))
                        editor.putString("input_sun", "3");
                    else
                        editor.putString("input_sun", input_sun.getText().toString());
                    editor.commit();

                    editor.putInt("mon_steps", stepsOfTarget(Integer.parseInt(preferences.getString("input_mon", "3")),Double.parseDouble(predict_mon_num.getText().toString())));
                    editor.putInt("tue_steps", stepsOfTarget(Integer.parseInt(preferences.getString("input_tue", "3")),Double.parseDouble(predict_tue_num.getText().toString())));
                    editor.putInt("wed_steps", stepsOfTarget(Integer.parseInt(preferences.getString("input_wed", "3")),Double.parseDouble(predict_wed_num.getText().toString())));
                    editor.putInt("thu_steps", stepsOfTarget(Integer.parseInt(preferences.getString("input_thu", "3")),Double.parseDouble(predict_thur_num.getText().toString())));
                    editor.putInt("fri_steps", stepsOfTarget(Integer.parseInt(preferences.getString("input_fri", "3")),Double.parseDouble(predict_fri_num.getText().toString())));
                    editor.putInt("sat_steps", stepsOfTarget(Integer.parseInt(preferences.getString("input_sat", "3")),Double.parseDouble(predict_sat_num.getText().toString())));
                    editor.putInt("sun_steps", stepsOfTarget(Integer.parseInt(preferences.getString("input_sun", "3")),Double.parseDouble(predict_sun_num.getText().toString())));
                    editor.commit();
                    startSave(new InitialLookUpRequest(person_id));
                }
                else
                    Toast.makeText(CustomDialogActivity.this, "1에서 5까지의 숫자만 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean isStepScore(String score){
        if (score.equals(""))
            return true;
        else{
            int real_score = Integer.parseInt(score);
            if(1<= real_score && real_score <=5)
                return true;
            else
                return false;
        }
    }
    public int stepsOfTarget(int score, double step){
        if(score == 1)
            return (int)(step*0.5);
        else if(score == 2)
            return (int)(step*0.8);
        else if(score == 3)
            return (int)step;
        else if(score == 4)
            return (int)(step*1.2);
        else
            return (int)(step*1.5);
    }
    public void startSave(InitialLookUpRequest data){
        service.userTargetWalk(data).enqueue(new Callback<TargetWalkResponse>() {
            @Override
            public void onResponse(Call<TargetWalkResponse> call, Response<TargetWalkResponse> response) {
                TargetWalkResponse resultCode = response.body();
                if(resultCode.getResultCode() == 200){
                    Toast.makeText(CustomDialogActivity.this, "입력한 내용이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("health_info_day", CustomDialogActivity.this.getIntent().getIntArrayExtra("health_info_day"));
                    intent.putExtra("health_info_week", CustomDialogActivity.this.getIntent().getIntArrayExtra("health_info_week"));
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("scoreAccept", false);
                    editor.commit();
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

    public void targetWalk(InitialLookUpRequest data) {
        service.userTargetWalk(data).enqueue(new Callback<TargetWalkResponse>() {
            @Override
            public void onResponse(Call<TargetWalkResponse> call, Response<TargetWalkResponse> response) {
                TargetWalkResponse resultCode = response.body();
                if (resultCode.getResultCode() == 200) {
                    List<TargetWalk> data = resultCode.getMid();

                    for (int i = 0; i < data.size(); i++) { // JSON data 돌면서 수행
                        if (data.get(i).getDayOfWeek().equals("mon")) { // 월요일이면
                            predict_mon_num.setText(String.valueOf(data.get(i).getSteps())); // 월요일 걸음 수 받아오기
                        }
                        if (data.get(i).getDayOfWeek().equals("tue")) { // 화요일이면
                            predict_tue_num.setText(String.valueOf(data.get(i).getSteps())); // 화요일 걸음 수 받아오기
                        }
                        if (data.get(i).getDayOfWeek().equals("wed")) { // 수요일이면
                            predict_wed_num.setText(String.valueOf(data.get(i).getSteps())); // 수요일 걸음 수 받아오기
                        }
                        if (data.get(i).getDayOfWeek().equals("thur")) { // 목요일이면
                            predict_thur_num.setText(String.valueOf(data.get(i).getSteps())); // 목요일 걸음 수 받아오기
                        }
                        if (data.get(i).getDayOfWeek().equals("fri")) { // 금요일이면
                            predict_fri_num.setText(String.valueOf(data.get(i).getSteps())); // 금요일 걸음 수 받아오기
                        }
                        if (data.get(i).getDayOfWeek().equals("sat")) { // 토요일이면
                            predict_sat_num.setText(String.valueOf(data.get(i).getSteps())); // 토요일 걸음 수 받아오기
                        }
                        if (data.get(i).getDayOfWeek().equals("sun")) { // 일요일이면
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
}