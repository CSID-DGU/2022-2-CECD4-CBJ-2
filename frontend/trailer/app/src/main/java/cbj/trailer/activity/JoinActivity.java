package cbj.trailer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import cbj.trailer.data.CodeResponse;
import cbj.trailer.data.JoinRequest;
import cbj.trailer.network.ServiceApi;
import cbj.trailer.R;
import cbj.trailer.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity {

    private EditText join_id;
    private EditText join_pwd;
    private EditText join_name;
    private Button join_btn;
    private Button check_id;
    private Button check_name;
    private String id;
    private String pwd;
    private String name;
    private int noNest_id;
    private int noNest_name;
    private boolean input_id;
    private boolean input_pwd;
    private boolean input_name;
    private ProgressBar join_progressbar;
    private ServiceApi service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);                         // xml, java 연결

        service = RetrofitClient.getClient().create(ServiceApi.class);  // 통신을 위한 ServiceApi 생성

        join_id = findViewById(R.id.join_id);
        join_pwd = findViewById(R.id.join_pwd);
        join_name = findViewById(R.id.join_name);

        join_btn = findViewById(R.id.join_resigter);
        check_id = findViewById(R.id.check_id);
        check_name = findViewById(R.id.check_name);
        join_progressbar = findViewById(R.id.join_pbar);                // xml의 컴포넌트와 각각 연결

        input_id = false;
        input_pwd = false;
        input_name = false;
        noNest_id = 0;
        noNest_name = 0;                                                // EditText에 문자가 있는지, 중복되는지 확인하는 변수 초기화

        join_btn.setEnabled(false);
        check_id.setEnabled(false);
        check_name.setEnabled(false);                                   // 버튼 비활성화

        join_id.addTextChangedListener(new TextWatcher() {              // login 액티비티에서 설명했으므로 요약 설명
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cor_id = s.toString();

                if (cor_id.length() > 3)
                    check_id.setEnabled(true);
                else
                    check_id.setEnabled(false);
            }                                                           // 아이디가 4글자 이상일 때 버튼 활성화

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        join_name.addTextChangedListener(new TextWatcher() {            // login 액티비티에서 설명했으므로 요약 설명
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cor_name = s.toString();

                if (cor_name.length() > 0)
                    check_name.setEnabled(true);
                else
                    check_name.setEnabled(false);
            }                                                           // 이름이 1글자 이상일 때 버튼 활성화

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /**
        // 아이디 검사 버튼
        check_id.setOnClickListener(new View.OnClickListener() {        // 버튼을 클릭 했을 때 모션을 정의
            @Override
            public void onClick(View v) {
                boolean noSpecial = join_id.getText().toString().matches("^[ㄱ-ㅎ가-힣a-zA-Z0-9]*$");
                if (!noSpecial) {
                    Toast.makeText(JoinActivity.this, "아이디에 특수문자가 들어갔습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 특수문자 사용 여부 검사

                join_progressbar.setVisibility(View.VISIBLE);           // progressbar를 활성화 하고 특수문자 확인
                checkID(join_id.getText().toString());
                // 인터넷 연결 검사, 중복 검사
            }
        });

        // 이름 검사 버튼
        check_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean noSpecial = join_name.getText().toString().matches("^[ㄱ-ㅎ가-힣a-zA-Z0-9]*$");
                if (!noSpecial) {
                    Toast.makeText(JoinActivity.this, "이름에 특수문자가 들어갔습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 특수문자 사용 여부 검사

                join_progressbar.setVisibility(View.VISIBLE);           // progressbar를 활성화 하고 특수문자 확인
                checkName(join_name.getText().toString());
                // 인터넷 연결 검사, 중복 검사
            }
        });*/

        // 회원가입 버튼
        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwd = join_pwd.getText().toString();            // 비밀번호를 변수에 넣어주고

                if (pwd.length() < 8) {
                    Toast.makeText(JoinActivity.this, "비밀번호가 8자리 미만입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }                                               // 8자리 미만이라면 아무 작업을 안함

                id = join_id.getText().toString();              // 8자리 이상일 때 id를 변수에 넣어줌
                name = join_name.getText().toString();          // 8자리 이상일 때 name를 변수에 넣어줌

                join_progressbar.setVisibility(View.VISIBLE);   // progressbar를 활성화 해주고
                startJoin(new JoinRequest(id, pwd, name));      // 회원가입통신 시작
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

    /**
    public void checkID(String checkId) {                               // ID를 검사하는 함수(앞의 설명으로 요약함)
        service.userCheckID(checkId).enqueue(new Callback<CodeResponse>() {
            @Override
            public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                CodeResponse code = response.body();
                join_progressbar.setVisibility(View.INVISIBLE);         // progressbar 비활성화

                if (code.getCode() == 200) {                            // 중복이 없다면
                    Toast.makeText(JoinActivity.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                    join_id.setClickable(false);
                    join_id.setFocusable(false);        // EditText 수정여부 비활성화
                    check_id.setEnabled(false);         // Button 비활성화

                    input_id = true;
                    // 둘 다 올바를 때 input_id를 true로 초기화

                    if (input_name)                     // 만약 input_name도 true라면
                        join_btn.setEnabled(true);      // 회원가입 버튼 활성화
                    else                                // false라면
                        join_btn.setEnabled(false);     // 회원가입 버튼 비활성화
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

    public void checkName(String checkName) {                           // Name를 검사하는 함수(앞의 설명으로 요약함)
        service.userCheckName(checkName).enqueue(new Callback<CodeResponse>() {
            @Override
            public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                CodeResponse code = response.body();
                join_progressbar.setVisibility(View.INVISIBLE);         // progressbar 비활성화

                if (code.getCode() == 200) {                            // 중복이 없다면
                    Toast.makeText(JoinActivity.this, "사용 가능한 이름입니다.", Toast.LENGTH_SHORT).show();
                    join_name.setClickable(false);
                    join_name.setFocusable(false);        // EditText 수정여부 비활성화
                    check_name.setEnabled(false);         // Button 비활성화

                    input_name = true;
                    // 둘 다 올바를 때 input_name를 true로 초기화

                    if (input_id)                   // 만약 input_id도 true라면
                        join_btn.setEnabled(true);  // 회원가입 버튼 활성화
                    else                            // false라면
                        join_btn.setEnabled(false); // 회원가입 버튼 비활성화
                } else {
                    Toast.makeText(JoinActivity.this, "중복된 이름이 존재합니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CodeResponse> call, Throwable t) {
                Toast.makeText(JoinActivity.this, "이름 검사 오류 발생", Toast.LENGTH_SHORT).show();
                Log.e("이름 검사 오류 발생", t.getMessage());
                join_progressbar.setVisibility(View.INVISIBLE);
            }
        });
    }
     */
}