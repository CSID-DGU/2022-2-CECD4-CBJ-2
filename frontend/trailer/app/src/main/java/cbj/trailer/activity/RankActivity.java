package cbj.trailer.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import cbj.trailer.R;
import cbj.trailer.data.InitialLookUpRequest;
import cbj.trailer.data.InitialRankListResponse;
import cbj.trailer.data.LookUpRequest;
import cbj.trailer.data.RankData;
import cbj.trailer.data.RankListResponse;
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
    private String last_last_login_time;
    private String last_login_time;
    private ProgressBar rank_progressbar;
    private boolean input_age;
    private boolean input_sex;
    private Button lookup_btn;
    private TableLayout tableLayout;
    private TextView user_nickname;
    private TextView user_rank;
    private TextView user_groupRank;
    private TextView user_walk;
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
        userStep = preferences.getString("userStep", "");
        last_last_login_time = preferences.getString("last_last_login_time", "");
        last_login_time = preferences.getString("last_login_time", "");

        user_nickname.setText(userNickname);
        user_walk.setText(userStep);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("scoreAccept", false);
        editor.commit();

        //?????? ??????
        String [] sex_type = getResources().getStringArray(R.array.rank_gender);
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
                    else if(i==2)
                        sex = "F";
                    else
                        sex = "A";
                }
                else{
                    input_sex = false;
                    sex = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });//?????? ??????

        //?????? ??????
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
                    else if(i==8)
                        age = 80;
                    else
                        age = 0;
                }
                else{
                    input_age = false;
                    age = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });//?????? ??????

        lookup_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(input_sex && input_age){
                    rank_progressbar.setVisibility(View.VISIBLE);
                    startLookup(new LookUpRequest(userId, sex, age));
                }
                else{
                    Toast.makeText(RankActivity.this, "????????? ????????? ????????? ????????? ?????????????????? ????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(isNewWeek()) {
            //????????? ?????? ??????????????? ????????? ????????? ?????? ?????? ???????????? ?????? ??????
            defaultLookUp(new InitialLookUpRequest(userId));
        }
        else{
            //????????? ??????????????? ?????? ?????? ???????????? ?????? ??????
            makeDefaultRank();
        }
        userRank = preferences.getString("userRank", "??????");
        userGroupRank = preferences.getString("userGroupRank", "??????");
        user_rank.setText(userRank);
        user_groupRank.setText(userGroupRank);
    }

    public boolean isNewWeek(){
        //??????????????? ??????
        if(last_last_login_time.equals("") || preferences.getString("default_rank", "").equals(""))
            return true;

        //????????? ?????????????????? ????????? ??????
        int baseYear = Integer.parseInt(last_last_login_time.substring(0,4));
        int baseMonth = Integer.parseInt(last_last_login_time.substring(5,7))-1;
        int baseDay = Integer.parseInt(last_last_login_time.substring(8)); //1??? ???, 7??? ???

        int targetYear = Integer.parseInt(last_login_time.substring(0,4));
        int targetMonth = Integer.parseInt(last_login_time.substring(5,7))-1;
        int targetDay = Integer.parseInt(last_login_time.substring(8)); //1??? ???, 7??? ???

        Calendar baseCal = new GregorianCalendar(baseYear, baseMonth, baseDay);
        Calendar targetCal = new GregorianCalendar(targetYear, targetMonth, targetDay);
        long diffSec = (targetCal.getTimeInMillis() - baseCal.getTimeInMillis())/1000;
        long diffDays = diffSec / (24*60*60);

        //??? : 2, ??? : 3, ....., ??? : 7, ??? : 8
        if(baseDay == 1)
            baseDay = 8;
        if(targetDay==1)
            targetDay = 8;

        // ?????? ?????????????????? 7?????? ???????????? ???????????? ????????? ????????? ????????? ????????? ?????? true ??????
        if(diffDays >= 7 || targetDay - baseDay < 0)
            return true;
        else
            return false;
    }

    public void startLookup(LookUpRequest data) {
        service.userRank(data).enqueue(new Callback<RankListResponse>() {
            @Override
            public void onResponse(Call<RankListResponse> call, Response<RankListResponse> response) {
                RankListResponse code = response.body();                    // ???????????? body??? ????????? ?????? code??? ?????? ????????? ??????
                rank_progressbar.setVisibility(View.INVISIBLE);         // progressbar ????????????
                if (code.getCode() == 200) {                            // ?????? ??????
                    //?????? ??????
                    List<RankData> data = code.getRank();
                    int count = 0;
                    tableLayout.removeViews(1,tableLayout.getChildCount()-1);

                    //?????? ?????? ?????? ????????? ?????? 7??? ????????? ??????
                    if(data.size() < 7){
                        while(count<data.size()){
                            TableRow tableRow = new TableRow(RankActivity.this);
                            tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                            //?????? ?????? ??????
                            TextView textView = new TextView(RankActivity.this);
                            textView.setText(String.valueOf(data.get(count).getRankIndex()));
                            textView.setGravity(Gravity.LEFT);
                            textView.setTextSize(20);
                            tableRow.addView(textView);

                            //????????? ?????? ??????
                            TextView textView1 = new TextView(RankActivity.this);
                            textView1.setText(data.get(count).getUserNickName());
                            textView1.setGravity(Gravity.LEFT);
                            textView1.setTextSize(20);
                            tableRow.addView(textView1);

                            //?????? ??? ?????? ??????
                            TextView textView2 = new TextView(RankActivity.this);
                            textView2.setText(String.valueOf(data.get(count).getSteps()));
                            textView2.setGravity(Gravity.LEFT);
                            textView2.setTextSize(20);
                            tableRow.addView(textView2);

                            //?????? ?????? ??????
                            TextView textView3 = new TextView(RankActivity.this);
                            textView3.setText(data.get(count).getGrade());
                            textView3.setGravity(Gravity.LEFT);
                            textView3.setTextSize(20);
                            tableRow.addView(textView3);

                            tableLayout.addView(tableRow);
                            count+=1;
                        }
                    }
                    //?????? ?????? ?????? ????????? ?????? 7??? ????????? ??????
                    else{
                        while(count < 7){
                            if(count == 3 || count == 4){
                                TableRow tableRow1 = new TableRow(RankActivity.this);
                                tableRow1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                for (int i = 0; i < 4; i++) {
                                    TextView textView = new TextView(RankActivity.this);
                                    textView.setText(":");
                                    textView.setGravity(Gravity.LEFT);
                                    textView.setTextSize(20);
                                    tableRow1.addView(textView);
                                }
                                tableLayout.addView(tableRow1);
                            }
                            TableRow tableRow = new TableRow(RankActivity.this);
                            tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            //?????? ?????? ??????
                            TextView textView = new TextView(RankActivity.this);
                            textView.setText(String.valueOf(data.get(count).getRankIndex()));
                            textView.setGravity(Gravity.LEFT);
                            textView.setTextSize(20);
                            tableRow.addView(textView);

                            //????????? ?????? ??????
                            TextView textView1 = new TextView(RankActivity.this);
                            textView1.setText(data.get(count).getUserNickName());
                            textView1.setGravity(Gravity.LEFT);
                            textView1.setTextSize(20);
                            tableRow.addView(textView1);

                            //?????? ??? ?????? ??????
                            TextView textView2 = new TextView(RankActivity.this);
                            textView2.setText(String.valueOf(data.get(count).getSteps()));
                            textView2.setGravity(Gravity.LEFT);
                            textView2.setTextSize(20);
                            tableRow.addView(textView2);

                            //?????? ?????? ??????
                            TextView textView3 = new TextView(RankActivity.this);
                            textView3.setText(data.get(count).getGrade());
                            textView3.setGravity(Gravity.LEFT);
                            textView3.setTextSize(20);
                            tableRow.addView(textView3);

                            tableLayout.addView(tableRow);
                            count+=1;
                        }
                        rank_progressbar.setVisibility(View.INVISIBLE);         // ?????? ?????? ????????? log ?????? ??? progressbar ????????????
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
            public void onFailure(Call<RankListResponse> call, Throwable t) {
                Toast.makeText(RankActivity.this, "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
                Log.e("?????? ?????? ??????", t.getMessage());
                rank_progressbar.setVisibility(View.INVISIBLE);         // ?????? ?????? ????????? log ?????? ??? progressbar ????????????
            }
        });
    }

    public void defaultLookUp(InitialLookUpRequest data){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                service.defaultRank(data).enqueue(new Callback<InitialRankListResponse>() {
                    @Override
                    public void onResponse(Call < InitialRankListResponse > call, Response < InitialRankListResponse > response){
                        InitialRankListResponse code = response.body();                    // ???????????? body??? ????????? ?????? code??? ?????? ????????? ??????
                        rank_progressbar.setVisibility(View.INVISIBLE);         // progressbar ????????????
                        if (code.getCode() == 200) {                            // ?????? ??????
                            //?????? ??????
                            List<RankData> data = code.getRank();
                            String temp = "";

                            int count = 0;
                            //?????? ?????? ?????? ????????? ?????? 7??? ????????? ??????
                            if (data.size() < 7) {
                                while (count < data.size()) {
                                    TableRow tableRow = new TableRow(RankActivity.this);
                                    tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                    //?????? ?????? ??????
                                    TextView textView = new TextView(RankActivity.this);
                                    textView.setText(String.valueOf(data.get(count).getRankIndex()));
                                    textView.setGravity(Gravity.LEFT);
                                    textView.setTextSize(20);
                                    tableRow.addView(textView);
                                    temp+=String.valueOf(data.get(count).getRankIndex());
                                    temp+="-";

                                    //????????? ?????? ??????
                                    TextView textView1 = new TextView(RankActivity.this);
                                    textView1.setText(data.get(count).getUserNickName());
                                    textView1.setGravity(Gravity.LEFT);
                                    textView1.setTextSize(20);
                                    tableRow.addView(textView1);
                                    temp+=data.get(count).getUserNickName();
                                    temp+="-";

                                    //?????? ??? ?????? ??????
                                    TextView textView2 = new TextView(RankActivity.this);
                                    textView2.setText(String.valueOf(data.get(count).getSteps()));
                                    textView2.setGravity(Gravity.LEFT);
                                    textView2.setTextSize(20);
                                    tableRow.addView(textView2);
                                    temp+=String.valueOf(data.get(count).getSteps());
                                    temp+="-";

                                    //?????? ?????? ??????
                                    TextView textView3 = new TextView(RankActivity.this);
                                    textView3.setText(data.get(count).getGrade());
                                    textView3.setGravity(Gravity.LEFT);
                                    textView3.setTextSize(20);
                                    tableRow.addView(textView3);
                                    temp+=data.get(count).getGrade();
                                    temp+="-";

                                    tableLayout.addView(tableRow);
                                    count += 1;
                                }
                            }
                            //?????? ?????? ?????? ????????? ?????? 7??? ????????? ??????
                            else {
                                while (count < 7) {
                                    if (count == 3 || count == 4) {
                                        TableRow tableRow1 = new TableRow(RankActivity.this);
                                        tableRow1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        for (int i = 0; i < 4; i++) {
                                            TextView textView = new TextView(RankActivity.this);
                                            textView.setText(":");
                                            textView.setGravity(Gravity.LEFT);
                                            textView.setTextSize(20);
                                            tableRow1.addView(textView);
                                        }
                                        tableLayout.addView(tableRow1);
                                    }
                                    TableRow tableRow = new TableRow(RankActivity.this);
                                    tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    //?????? ?????? ??????
                                    TextView textView = new TextView(RankActivity.this);
                                    textView.setText(String.valueOf(data.get(count).getRankIndex()));
                                    textView.setGravity(Gravity.LEFT);
                                    textView.setTextSize(20);
                                    tableRow.addView(textView);
                                    temp+=String.valueOf(data.get(count).getRankIndex());
                                    temp+="-";

                                    //????????? ?????? ??????
                                    TextView textView1 = new TextView(RankActivity.this);
                                    textView1.setText(data.get(count).getUserNickName());
                                    textView1.setGravity(Gravity.LEFT);
                                    textView1.setTextSize(20);
                                    tableRow.addView(textView1);
                                    temp+=data.get(count).getUserNickName();
                                    temp+="-";

                                    //?????? ??? ?????? ??????
                                    TextView textView2 = new TextView(RankActivity.this);
                                    textView2.setText(String.valueOf(data.get(count).getSteps()));
                                    textView2.setGravity(Gravity.LEFT);
                                    textView2.setTextSize(20);
                                    tableRow.addView(textView2);
                                    temp+=String.valueOf(data.get(count).getSteps());
                                    temp+="-";


                                    //?????? ?????? ??????
                                    TextView textView3 = new TextView(RankActivity.this);
                                    textView3.setText(data.get(count).getGrade());
                                    textView3.setGravity(Gravity.LEFT);
                                    textView3.setTextSize(20);
                                    tableRow.addView(textView3);
                                    temp+=data.get(count).getGrade();
                                    temp+="-";

                                    tableLayout.addView(tableRow);
                                    count += 1;
                                }
                                rank_progressbar.setVisibility(View.INVISIBLE);         // ?????? ?????? ????????? log ?????? ??? progressbar ????????????
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

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("default_rank", temp.substring(0,temp.length()-1));
                            editor.putString("userRank", code.getAllUserRank());
                            editor.putString("userGroupRank", code.getUserGroupRank());
                            editor.commit();
                            user_rank.setText(code.getAllUserRank());
                            user_groupRank.setText(code.getUserGroupRank());
                        }
                    }

                    @Override
                    public void onFailure (Call < InitialRankListResponse > call, Throwable t){
                        Toast.makeText(RankActivity.this, "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
                        Log.e("?????? ?????? ??????", t.getMessage());
                        rank_progressbar.setVisibility(View.INVISIBLE);         // ?????? ?????? ????????? log ?????? ??? progressbar ????????????
                    }
                });
            }
        },2000);
    }

    public void makeDefaultRank() {
        String[] default_rank = preferences.getString("default_rank", "").split("-");
        int count = 0;
        //?????? ?????? ?????? ????????? ?????? 7??? ????????? ??????
        if (default_rank.length != 28) {
            int limit = default_rank.length/4;
            while (count < limit) {
                TableRow tableRow = new TableRow(RankActivity.this);
                tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                for (int i = 0; i < 4; i++) {
                    TextView textView = new TextView(RankActivity.this);
                    textView.setText(default_rank[4 * count + i]);
                    textView.setGravity(Gravity.LEFT);
                    textView.setTextSize(20);
                    tableRow.addView(textView);
                }
                tableLayout.addView(tableRow);
                count += 1;
            }
        }
        //?????? ?????? ?????? ????????? ?????? 7??? ????????? ??????
        else {
            while (count < 7) {
                if (count == 3 || count == 4) {
                    TableRow tableRow1 = new TableRow(RankActivity.this);
                    tableRow1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    for (int i = 0; i < 4; i++) {
                        TextView textView = new TextView(RankActivity.this);
                        textView.setText(":");
                        textView.setGravity(Gravity.LEFT);
                        textView.setTextSize(20);
                        tableRow1.addView(textView);
                    }
                    tableLayout.addView(tableRow1);
                }
                TableRow tableRow = new TableRow(RankActivity.this);
                tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                for (int i = 0; i < 4; i++) {
                    TextView textView = new TextView(RankActivity.this);
                    textView.setText(default_rank[4 * count + i]);
                    textView.setGravity(Gravity.LEFT);
                    textView.setTextSize(20);
                    tableRow.addView(textView);
                }
                tableLayout.addView(tableRow);
                count += 1;
            }
        }
    }
}
