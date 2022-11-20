package cbj.trailer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cbj.trailer.R;
import cbj.trailer.data.LookUpRequest;
import cbj.trailer.data.LookUpResponse;
import cbj.trailer.network.RetrofitClient;
import cbj.trailer.network.ServiceApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankActivity  extends AppCompatActivity {
    private Spinner rank_age;
    private Spinner rank_sex;
    private int age;
    private String sex;
    private String userId;
    private String userNickname;
    private String userRank;
    private String userGroupRank;
    private String userStep;
    private ProgressBar rank_progressbar;
    private boolean input_age;
    private boolean input_sex;
    private Button lookup_btn;
    private TableLayout tableLayout;
    private TextView user_nickname;
    private TextView user_rank;
    private TextView user_groupRank;
    private TextView user_walk;
    private String [] ranking;
    private SharedPreferences preferences;
    private ServiceApi service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        rank_sex = findViewById(R.id.rank_sex);
        rank_age = findViewById(R.id.rank_age);
        lookup_btn = findViewById(R.id.rank_lookup);
        rank_progressbar = findViewById(R.id.rank_pbar);
        tableLayout = findViewById(R.id.tableLayout);
        user_nickname = findViewById(R.id.user_nickname);
        user_rank = findViewById(R.id.user_rank);
        user_groupRank = findViewById(R.id.user_groupRank);
        user_walk = findViewById(R.id.user_walk);
        input_age = false;
        input_sex = false;

        preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);

        userId = preferences.getString("userId", "");
        userNickname = preferences.getString("userNickname", "");
        userRank = preferences.getString("userRank", "미정");
        userGroupRank = preferences.getString("userGroupRank", "미정");
        userStep = preferences.getString("userStep", "미정");

        //성별 선택
        String [] sex_type = getResources().getStringArray(R.array.sex);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this
                , android.R.layout.simple_spinner_item, sex_type);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rank_sex.setAdapter(adapter1);

        rank_sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });//성별 선택

        //나이 선택
        String [] age_type = getResources().getStringArray(R.array.age_group);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this
                , android.R.layout.simple_spinner_item, age_type);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rank_age.setAdapter(adapter2);

        rank_age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0) {
                    input_age = true;
                    if(i==1)
                        age = 10;
                    else if(i==2)
                        age = 20;
                    else if(i==3)
                        age = 30;
                    else if(i==4)
                        age = 40;
                    else if(i==5)
                        age = 50;
                    else if(i==6)
                        age = 60;
                    else if(i==7)
                        age = 70;
                    else
                        age = 80;
                }
                else{
                    input_age = false;
                    age = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });//나이 선택

        lookup_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(input_sex && input_age){
                    rank_progressbar.setVisibility(View.VISIBLE);
                    startLookup(new LookUpRequest(userId, sex, age));
                }
                else{
                    Toast.makeText(RankActivity.this, "랭킹을 조회할 성별과 나이를 선택해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void startLookup(LookUpRequest data) {                           // 회원가입을 하는 함수(이전에 톧신을 설명했으므로 요약함)
        service.userRank(data).enqueue(new Callback<LookUpResponse>() {
            @Override
            public void onResponse(Call<LookUpResponse> call, Response<LookUpResponse> response) {
                LookUpResponse code = response.body();                    // 응답받은 body의 객체를 넣고 code에 따라 활동이 나뉨
                rank_progressbar.setVisibility(View.INVISIBLE);         // progressbar 비활성화
                if (code.getCode() == 200) {                            // 랭크 조회
                    //표에 넣기
                    ranking = code.getGroupRank();
                    int count = 0;
                    //현재 해당 그룹 사용자 수가 7명 미만인 경우
                    if(ranking[27].equals("-1")){
                        while(count<7){
                            if(ranking[count].equals("-1")){
                                break;
                            }
                            else{
                                TableRow tableRow = new TableRow(RankActivity.this);
                                tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                for(int i=0; i<4; i++){
                                    TextView textView = new TextView(RankActivity.this);
                                    textView.setText(ranking[4*count+i]);
                                    textView.setGravity(Gravity.CENTER);
                                    textView.setTextSize(20);
                                    tableRow.addView(textView);
                                }
                                tableLayout.addView(tableRow);
                            }
                            count+=1;
                        }
                    }
                    //현재 해당 그룹 사용자 수가 7명 이상인 경우
                    else{
                        while(count < 7){
                            if(count == 3 || count == 5){
                                TableRow tableRow = new TableRow(RankActivity.this);
                                tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                for (int i = 0; i < 4; i++) {
                                    TextView textView = new TextView(RankActivity.this);
                                    textView.setText(":");
                                    textView.setGravity(Gravity.CENTER);
                                    textView.setTextSize(20);
                                    tableRow.addView(textView);
                                }
                                tableLayout.addView(tableRow);
                            }
                            else {
                                TableRow tableRow = new TableRow(RankActivity.this);
                                tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                for (int i = 0; i < 4; i++) {
                                    TextView textView = new TextView(RankActivity.this);
                                    textView.setText(ranking[4 * count + i]);
                                    textView.setGravity(Gravity.CENTER);
                                    textView.setTextSize(20);
                                    tableRow.addView(textView);
                                }
                                tableLayout.addView(tableRow);
                            }
                            count+=1;
                        }
                        /**
                        while(count < 7){
                            if(count == 3 || count == 5){
                                TableRow tableRow = new TableRow(RankActivity.this);
                                tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                TextView textView = new TextView(RankActivity.this);
                                textView.setText(":");
                                textView.setGravity(Gravity.CENTER);
                                textView.setTextSize(20);
                                tableRow.addView(textView);
                                tableLayout.addView(tableRow);
                            }
                            else {
                                TableRow tableRow = new TableRow(RankActivity.this);
                                tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                for (int i = 0; i < 4; i++) {
                                    TextView textView = new TextView(RankActivity.this);
                                    textView.setText(ranking[4 * count + i]);
                                    textView.setGravity(Gravity.CENTER);
                                    textView.setTextSize(20);
                                    tableRow.addView(textView);
                                }
                                tableLayout.addView(tableRow);
                            }
                            count+=1;
                        }*/
                    }

                }
            }

            @Override
            public void onFailure(Call<LookUpResponse> call, Throwable t) {
                Toast.makeText(RankActivity.this, "통신 오류 발생", Toast.LENGTH_SHORT).show();
                Log.e("통신 오류 발생", t.getMessage());
                rank_progressbar.setVisibility(View.INVISIBLE);         // 통신 오류 발생시 log 출력 후 progressbar 비활성화
            }
        });
    }

}
