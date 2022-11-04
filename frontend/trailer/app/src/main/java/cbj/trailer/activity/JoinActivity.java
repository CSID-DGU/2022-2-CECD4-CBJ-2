package cbj.trailer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cbj.trailer.data.CheckIdRequest;
import cbj.trailer.data.CodeResponse;
import cbj.trailer.data.JoinRequest;
import cbj.trailer.network.ServiceApi;
import cbj.trailer.R;
import cbj.trailer.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity {

    private TextView title;
    private EditText join_id;
    private EditText join_pwd1;
    private EditText join_pwd2;
    private EditText join_nickname;
    private EditText join_age;
    private Spinner join_sex;
    private EditText join_homeAddress;
    private EditText join_companyAddress;
    private CheckBox join_movieTheater;
    private CheckBox join_caffe;
    private CheckBox join_library;
    private CheckBox join_park;
    private CheckBox join_amusementPark;
    private CheckBox join_arcade;
    private Spinner join_exerciseIntensity;
    private Button join_btn;
    private Button check_id;
    private Button check_nickname;
    private String id;
    private String pwd;
    private String nickname;
    private int age;
    private String sex;
    private String homeAddress;
    private String companyAddress;
    private String category = "";
    private String exerciseIntensity;
    private boolean input_id;
    private boolean input_pwd1;
    private boolean input_nickname;
    private boolean input_age;
    private boolean input_sex;
    private boolean input_homeAddress;
    private boolean input_companyAddress;
    private boolean input_exerciseIntensity;
    private ProgressBar join_progressbar;
    private ServiceApi service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);                         // xml, java 연결

        title = (TextView)findViewById(R.id.join_main);
        String titleMessage = title.getText().toString();
        SpannableString spannableString = new SpannableString(titleMessage);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main2)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.3f), 0, 5, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannableString);

        service = RetrofitClient.getClient().create(ServiceApi.class);  // 통신을 위한 ServiceApi 생성

        join_id = findViewById(R.id.join_id);
        join_pwd1 = findViewById(R.id.join_pwd1);
        join_pwd2 = findViewById(R.id.join_pwd2);
        join_nickname = findViewById(R.id.join_nickname);
        join_age = findViewById(R.id.join_age);
        join_sex = findViewById(R.id.join_sex);
        join_homeAddress = findViewById(R.id.join_homeAddress);
        join_companyAddress = findViewById(R.id.join_companyAddress);
        join_movieTheater = findViewById(R.id.join_movieTheater);
        join_caffe = findViewById(R.id.join_caffe);
        join_library = findViewById(R.id.join_library);
        join_park = findViewById(R.id.join_park);
        join_amusementPark = findViewById(R.id.join_amusementpark);
        join_arcade = findViewById(R.id.join_arcade);
        join_exerciseIntensity = findViewById(R.id.join_exerciseIntensity);

        join_btn = findViewById(R.id.join_register);
        check_id = findViewById(R.id.check_id);
        check_nickname = findViewById(R.id.check_nickname);
        join_progressbar = findViewById(R.id.join_pbar);                // xml의 컴포넌트와 각각 연결

        input_id = false;
        input_pwd1 = false;
        input_nickname = false;
        input_age = false;
        input_sex = false;
        input_homeAddress = false;
        input_companyAddress = false;
        input_exerciseIntensity = false;


        //id 입력이 변경되었는지 확인 후 다시 아이디 검사 하도록 boolean 변수 설정
        join_id.addTextChangedListener(new TextWatcher() {              // login 액티비티에서 설명했으므로 요약 설명
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_id = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 아이디 검사 버튼
        check_id.setOnClickListener(new View.OnClickListener() {        // 버튼을 클릭 했을 때 모션을 정의
            @Override
            public void onClick(View v) {
                if(join_id.getText().toString() == null || join_id.getText().toString().equals("")) {
                    Toast.makeText(JoinActivity.this, "아이디를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(join_id.getText().toString().length() > 3)) {
                    Toast.makeText(JoinActivity.this, "아이디는 4글자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean noSpecial = join_id.getText().toString().matches("^[ㄱ-ㅎ가-힣a-zA-Z0-9]*$");
                if (!noSpecial) {
                    Toast.makeText(JoinActivity.this, "아이디에 특수문자가 들어갔습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 특수문자 사용 여부 검사

                join_progressbar.setVisibility(View.VISIBLE);           // progressbar를 활성화 하고 특수문자 확인
                checkID(join_id.getText().toString()); // 인터넷 연결 검사, 중복 검사
            }
        });

        //pwd 8자리 이상인지 확인
        join_pwd1.addTextChangedListener(new TextWatcher() {              // login 액티비티에서 설명했으므로 요약 설명
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cor_pwd = s.toString();
                if (cor_pwd.length() > 7) {
                    input_pwd1 = true;
                    pwd = cor_pwd;
                }
                else
                    input_pwd1 = false;
            }                                                           // 아이디가 4글자 이상일 때 버튼 활성화

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 닉네임 특수 문자 포함되어있는지 확인하는 검사 버튼
        check_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean noSpecial = join_nickname.getText().toString().matches("^[ㄱ-ㅎ가-힣a-zA-Z0-9]*$");
                if (!noSpecial) {
                    Toast.makeText(JoinActivity.this, "이름에 특수문자가 들어갔습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 특수문자 사용 여부 검사
                input_nickname = true;
                join_nickname.setClickable(false);
                join_nickname.setFocusable(false);        // EditText 수정여부 비활성화
                check_nickname.setEnabled(false);         // Button 비활성화
                nickname = join_nickname.getText().toString();
            }
        });

        //나이 숫자를 제대로 입력했는지 확인
        join_age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean output = true;
                String fake_age = s.toString();
                if(!fake_age.equals("")) {
                    for (int i = 0; i < fake_age.length(); i++) {
                        char tmp = fake_age.charAt(i);
                        if (!('0' <= tmp && tmp <= '9')) {
                            output = false;
                        }
                    }
                    if (output) {
                        int real_age = Integer.parseInt(fake_age);
                        if (0 < real_age && real_age < 120) {
                            input_age = true;
                            age = real_age;
                        } else
                            input_age = false;
                    }
                }
            }
        });

        //성별 선택
        String [] sex_type = getResources().getStringArray(R.array.sex);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this
        , android.R.layout.simple_spinner_item, sex_type);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        join_sex.setAdapter(adapter1);

        join_sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0) {
                    input_sex = true;
                    if(i==1)
                        sex = "M";
                    else
                        sex = "F";
                }
                else{
                    input_sex = false;
                    sex = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //집 주소 입력했는지 확인
        join_homeAddress.addTextChangedListener(new TextWatcher() {              // login 액티비티에서 설명했으므로 요약 설명
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cor_homeAddress = s.toString();
                if (!cor_homeAddress.equals("")) {
                    input_homeAddress = true;
                    homeAddress = cor_homeAddress;
                }
                else
                    input_homeAddress = false;
            }                                                           // 아이디가 4글자 이상일 때 버튼 활성화

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //회사/학교 주소 입력했는지 확인
        join_companyAddress.addTextChangedListener(new TextWatcher() {              // login 액티비티에서 설명했으므로 요약 설명
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cor_companyAddress = s.toString();
                if (!cor_companyAddress.equals("")) {
                    input_companyAddress = true;
                    companyAddress = cor_companyAddress;
                }
                else
                    input_homeAddress = false;
            }                                                           // 아이디가 4글자 이상일 때 버튼 활성화

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //원하는 운동강도 선택
        String [] exerciseIntensity_type = getResources().getStringArray(R.array.exerciseIntensityItems);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this
                , android.R.layout.simple_spinner_dropdown_item, exerciseIntensity_type);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        join_exerciseIntensity.setAdapter(adapter2);

        join_exerciseIntensity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0) {
                    input_exerciseIntensity = true;
                    exerciseIntensity = exerciseIntensity_type[i];
                }
                else{
                    input_exerciseIntensity = false;
                    sex = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 회원가입 버튼
        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_id && input_pwd1 && input_nickname && input_age && input_sex && input_homeAddress && input_companyAddress && input_exerciseIntensity){
                    if(join_movieTheater.isChecked()){
                        category+="영화관,";
                    }
                    if(join_caffe.isChecked()){
                        category+="카페,";
                    }
                    if(join_library.isChecked()){
                        category+="도서관,";
                    }
                    if(join_park.isChecked()){
                        category+="공원,";
                    }
                    if(join_amusementPark.isChecked()){
                        category+="놀이공원,";
                    }
                    if(join_arcade.isChecked()){
                        category+="오락실,";
                    }
                    if(!category.equals(""))
                        category = category.substring(0,category.length()-1);
                    if(join_pwd1.getText().toString() != null && !join_pwd1.getText().toString().equals("") && join_pwd2.getText().toString() != null && !join_pwd2.getText().toString().equals("")) {
                        if (join_pwd1.getText().toString().equals(join_pwd2.getText().toString())) {
                            join_progressbar.setVisibility(View.VISIBLE);   // progressbar를 활성화 해주고
                            startJoin(new JoinRequest(id, pwd, nickname, age, sex, homeAddress, companyAddress, category, exerciseIntensity));      // 회원가입통신 시작
                        }
                        else {
                            Toast.makeText(JoinActivity.this, "동일한 비밀번호를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(JoinActivity.this, "8자리 이상의 비밀번호를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(!input_id)
                        Toast.makeText(JoinActivity.this, "아이디 검사를 진행해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    else if(!input_pwd1)
                        Toast.makeText(JoinActivity.this, "8자리 이상의 비밀번호를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    else if(!input_nickname)
                        Toast.makeText(JoinActivity.this, "닉네임 검사를 진행해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    else if(!input_age)
                        Toast.makeText(JoinActivity.this, "정확한 나이를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    else if(!input_sex)
                        Toast.makeText(JoinActivity.this, "본인의 성별을 선택해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    else if(!input_homeAddress)
                        Toast.makeText(JoinActivity.this, "집 주소를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    else if(!input_companyAddress)
                        Toast.makeText(JoinActivity.this, "회사 혹은 학교의 주소를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(JoinActivity.this, "본인이 원하는 운동강도를 선택해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void startJoin(JoinRequest data) {                           // 회원가입을 하는 함수(이전에 톧신을 설명했으므로 요약함)
        service.userJoin(data).enqueue(new Callback<CodeResponse>() {
            @Override
            public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                CodeResponse code = response.body();                    // 응답받은 body의 객체를 넣고 code에 따라 활동이 나뉨
                join_progressbar.setVisibility(View.INVISIBLE);         // progressbar 비활성화
                if (code.getCode() == 200) {                            // 회원가입 성공이라면
                    Toast.makeText(JoinActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();                                           // 현 액티비티를 종료함
                }
            }

            @Override
            public void onFailure(Call<CodeResponse> call, Throwable t) {
                Toast.makeText(JoinActivity.this, "통신 오류 발생", Toast.LENGTH_SHORT).show();
                Log.e("통신 오류 발생", t.getMessage());
                join_progressbar.setVisibility(View.INVISIBLE);         // 통신 오류 발생시 log 출력 후 progressbar 비활성화
            }
        });
    }

    public void checkID(String checkId) {                               // ID를 검사하는 함수(앞의 설명으로 요약함)
        service.userCheckID(new CheckIdRequest(checkId)).enqueue(new Callback<CodeResponse>() {
            @Override
            public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                CodeResponse code = response.body();
                join_progressbar.setVisibility(View.INVISIBLE);         // progressbar 비활성화

                if (code.getCode() == 200) {                            // 중복이 없다면
                    Toast.makeText(JoinActivity.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                    input_id = true;
                    id = checkId;
                    // 둘 다 올바를 때 input_id를 true로 초기화

                } else {
                    Toast.makeText(JoinActivity.this, "중복된 아이디가 존재합니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CodeResponse> call, Throwable t) {
                Toast.makeText(JoinActivity.this, "아이디 검사 오류 발생", Toast.LENGTH_SHORT).show();
                Log.e("아이디 검사 오류 발생", t.getMessage());
                join_progressbar.setVisibility(View.INVISIBLE);
            }
        });
    }
}