package cbj.trailer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import cbj.trailer.R;
import cbj.trailer.data.TargetWalk;
import cbj.trailer.data.TargetWalkRequest;
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

    private EditText int_mon;
    private EditText int_tue;
    private EditText int_wed;
    private EditText int_thur;
    private EditText int_fri;
    private EditText int_sat;
    private EditText int_sun;

    private int mon;
    private int tue;
    private int wed;
    private int thur;
    private int fri;
    private int sat;
    private int sun;

    private boolean input_sun;
    private boolean input_mon;
    private boolean input_tue;
    private boolean input_wed;
    private boolean input_thur;
    private boolean input_fri;
    private boolean input_sat;

    private Button delete;
    private Button save;
    private ServiceApi service;
    private SharedPreferences preferences;



    protected void onCreate (Bundle savedInstance){
//        Bundle savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dialog);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);

        int_mon = findViewById(R.id.mon);
        int_tue = findViewById(R.id.tue);
        int_wed = findViewById(R.id.wed);
        int_thur = findViewById(R.id.thur);
        int_fri = findViewById(R.id.fri);
        int_sat = findViewById(R.id.sat);
        int_sun = findViewById(R.id.sun);

        predict_mon_num = findViewById(R.id.predict_mon_num);
        predict_tue_num = findViewById(R.id.predict_tue_num);
        predict_wed_num = findViewById(R.id.predict_wed_num);
        predict_thur_num = findViewById(R.id.predict_thur_num);
        predict_fri_num = findViewById(R.id.predict_fri_num);
        predict_sat_num = findViewById(R.id.predict_sat_num);
        predict_sun_num = findViewById(R.id.predict_sun_num);


        delete = findViewById(R.id.btn_delete);
        save = findViewById(R.id.btn_save);

        input_mon = false;
        input_tue = false;
        input_wed = false;
        input_thur = false;
        input_fri = false;
        input_sat = false;
        input_sun = false;

        int_mon.addTextChangedListener(new TextWatcher() {             // EditText에서 문자의 변화를 감지하는 함수
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    // 바뀌기 전에는 할 필요 없음
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {       // text가 바뀌고 있는 중 모션

            }
            @Override
            public void afterTextChanged(Editable s) {                  // text가 바뀐 후에는 할 필요 없음(어차피 바뀌면서 함)
                String str_mon = s.toString();
                if (str_mon.length() > 0) {                              // 만약 입력된 문자가 있다면
                    input_mon = true;                                    // input_id을 true로 초기화하고
                }
                else {
                    mon = 3;
                    input_mon = true;                                    // input_id을 true로 초기화하고
                }

                if (input_mon)                                      // password도 입력이 되어 있다면
                    save.setEnabled(true);                     // 버튼을 활성화 해줌
                else                                                // 입력이 안되어 있다면
                    save.setEnabled(false);
            }
        });

        int_tue.addTextChangedListener(new TextWatcher() {             // EditText에서 문자의 변화를 감지하는 함수
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    // 바뀌기 전에는 할 필요 없음
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {       // text가 바뀌고 있는 중 모션

            }
            @Override
            public void afterTextChanged(Editable s) {                  // text가 바뀐 후에는 할 필요 없음(어차피 바뀌면서 함)
                String str_tue = s.toString();
                if (str_tue.length() > 0) {                              // 만약 입력된 문자가 있다면
                    input_tue = true;                                    // input_id을 true로 초기화하고
                }
                else {
                    tue = 3;
                    input_tue = true;                                    // input_id을 true로 초기화하고
                }

                if (input_tue)                                      // password도 입력이 되어 있다면
                    save.setEnabled(true);                     // 버튼을 활성화 해줌
                else                                                // 입력이 안되어 있다면
                    save.setEnabled(false);
            }
        });

        int_wed.addTextChangedListener(new TextWatcher() {             // EditText에서 문자의 변화를 감지하는 함수
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    // 바뀌기 전에는 할 필요 없음
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {       // text가 바뀌고 있는 중 모션

            }
            @Override
            public void afterTextChanged(Editable s) {                  // text가 바뀐 후에는 할 필요 없음(어차피 바뀌면서 함)
                String str_wed = s.toString();
                if (str_wed.length() > 0) {                              // 만약 입력된 문자가 있다면
                    input_wed = true;                                    // input_id을 true로 초기화하고
                }
                else {
                    wed = 3;
                    input_wed = true;                                    // input_id을 true로 초기화하고
                }

                if (input_wed)                                      // password도 입력이 되어 있다면
                    save.setEnabled(true);                     // 버튼을 활성화 해줌
                else                                                // 입력이 안되어 있다면
                    save.setEnabled(false);
            }
        });

        int_thur.addTextChangedListener(new TextWatcher() {             // EditText에서 문자의 변화를 감지하는 함수
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    // 바뀌기 전에는 할 필요 없음
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {       // text가 바뀌고 있는 중 모션

            }
            @Override
            public void afterTextChanged(Editable s) {                  // text가 바뀐 후에는 할 필요 없음(어차피 바뀌면서 함)
                String str_thur = s.toString();
                if (str_thur.length() > 0) {                              // 만약 입력된 문자가 있다면
                    input_thur = true;                                    // input_id을 true로 초기화하고
                }
                else {
                    thur = 3;
                    input_thur = true;                                    // input_id을 true로 초기화하고
                }

                if (input_thur)                                      // password도 입력이 되어 있다면
                    save.setEnabled(true);                     // 버튼을 활성화 해줌
                else                                                // 입력이 안되어 있다면
                    save.setEnabled(false);
            }
        });

        int_fri.addTextChangedListener(new TextWatcher() {             // EditText에서 문자의 변화를 감지하는 함수
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    // 바뀌기 전에는 할 필요 없음
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {       // text가 바뀌고 있는 중 모션

            }
            @Override
            public void afterTextChanged(Editable s) {                  // text가 바뀐 후에는 할 필요 없음(어차피 바뀌면서 함)
                String str_fri = s.toString();
                if (str_fri.length() > 0) {                              // 만약 입력된 문자가 있다면
                    input_fri = true;                                    // input_id을 true로 초기화하고
                }
                else {
                    fri = 3;
                    input_fri = true;                                    // input_id을 true로 초기화하고
                }

                if (input_fri)                                      // password도 입력이 되어 있다면
                    save.setEnabled(true);                     // 버튼을 활성화 해줌
                else                                                // 입력이 안되어 있다면
                    save.setEnabled(false);
            }
        });

        int_sat.addTextChangedListener(new TextWatcher() {             // EditText에서 문자의 변화를 감지하는 함수
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    // 바뀌기 전에는 할 필요 없음
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {       // text가 바뀌고 있는 중 모션

            }
            @Override
            public void afterTextChanged(Editable s) {                  // text가 바뀐 후에는 할 필요 없음(어차피 바뀌면서 함)
                String str_sat = s.toString();
                if (str_sat.length() > 0) {                              // 만약 입력된 문자가 있다면
                    input_sat = true;                                    // input_id을 true로 초기화하고
                }
                else {
                    sat = 3;
                    input_sat = true;                                    // input_id을 true로 초기화하고
                }

                if (input_sat)                                      // password도 입력이 되어 있다면
                    save.setEnabled(true);                     // 버튼을 활성화 해줌
                else                                                // 입력이 안되어 있다면
                    save.setEnabled(false);
            }
        });

        int_sun.addTextChangedListener(new TextWatcher() {             // EditText에서 문자의 변화를 감지하는 함수
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                String str_sun = s.toString();
                if (str_sun.length() > 0) {
                    input_sun = true;
                }
                else {
                    sun = 3;
                    input_sun = true;
                }

                if (input_sun)
                    save.setEnabled(true);
                else
                    save.setEnabled(false);
            }
        });
    }

    public void TargetWalk(TargetWalkRequest data){
        service.userTargetWalk(data).enqueue(new Callback<TargetWalkResponse>() {
            @Override
            public void onResponse(Call<TargetWalkResponse> call, Response<TargetWalkResponse> response){
                TargetWalkResponse code = response.body();
                if (code.getCode() == 200){
                    List<TargetWalk> data = code.getWalkScore();
                    predict_mon_num = data.

                }

            }

            @Override
            public void onFailure(Call<TargetWalkResponse> call, Throwable t) {

            }
        });
    }
}