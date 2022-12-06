package cbj.trailer.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cbj.trailer.R;

public class MyPageActivity extends AppCompatActivity{
    private LinearLayout testLinearLayout;
    private TextView nickname;
    private TextView allGroupRank;
    private TextView groupRank;
    private TextView todaySteps;
    private TextView goalSteps;
    private TextView scorebyweek;
    private TextView guide;
    private String [] youtubeIdForOld = {"iGIR9WTN188", "qFLuUFM2x9A", "lXUEMdde9hM"};
    private String [] youtubeIdForMen = {"iGIR9WTN188", "iw3eEjp6wYU", "CYcLODSeC-c", "iw3eEjp6wYU", "CYcLODSeC-c", "VtCGHITZu_8", "Oz5ts01rzEo", "Iaa8YNDRbhg", "zSJYAyoojdw", "WSEtdciBPLM", "--MMq6I07b4", "plrLB3EnO1g"};
    private String [] youtubeIdForWomen = {"iGIR9WTN188", "iw3eEjp6wYU", "eWRXMBkfagA", "iw3eEjp6wYU", "eWRXMBkfagA", "1AlEo1hR_00", "W4SpM2gxGdE", "Iaa8YNDRbhg", "wNXXc1zBYYw", "3Rlx492eZNM", "T6kAvbexlaE", "kOYd2oFOojc"};
    private String healthcareForOld = "쉽게 놓칠 수 있는 건강 상식과 일상에 쉽게 적용할 수 있는 건강 관리 지식을 알아보세요! 꾸준한 관리가 건강한 삶의 비결입니다.";
    private String [] healthcareForYoung = {"이번 주는 활동량이 적을 것으로 예상됩니다. 걷기, 홈트레이닝 등 쉽게 할 수 있는 운동부터 시작해서 조금씩 운동량을 늘려보세요!",
                                            "이번 주는 지난 주보다 조금 적은 활동량이 예상됩니다. 가벼운 조깅 또는 집에서도 할 수 있는 운동을 시도해보는건 어떨까요?",
                                            "이번 주는 평소와 비슷한 활동량이 예상됩니다. 가볍게 할 수 있는 근력운동으로 기초체력으로 탄탄하게 길러보세요.",
                                            "이번 주는 활동량이 많을 것으로 예상됩니다. 피로가 많이 누적될 수 있으니 일상 속 스트레칭으로 꾸준히 피로를 풀어주세요!"};
    private String youtubeId1;
    private String youtubeId2;
    private String youtubeId3;
    private SharedPreferences preferences;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        preferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.KOREAN);
        String today = "";

        if(day.equals("월"))
            today = preferences.getString("mon_steps", "");
        else if(day.equals('화'))
            today = preferences.getString("tue_steps", "");
        else if(day.equals('수'))
            today = preferences.getString("wed_steps", "");
        else if(day.equals('목'))
            today = preferences.getString("thu_steps", "");
        else if(day.equals('금'))
            today = preferences.getString("fri_steps", "");
        else if(day.equals('토'))
            today = preferences.getString("sat_steps", "");
        else
            today = preferences.getString("sun_steps", "");

        testLinearLayout = findViewById(R.id.test_linearLayout);
        nickname = findViewById(R.id.name);
        allGroupRank = findViewById(R.id.mypage_allGroupRank);
        groupRank = findViewById(R.id.mypage_groupRank);
        todaySteps = findViewById(R.id.mypage_todaySteps);
        goalSteps = findViewById(R.id.rec_walking);
        scorebyweek = findViewById(R.id.total_score);
        guide = findViewById(R.id.rec_score_words);
        int age = Integer.parseInt(preferences.getString("userAge", ""));
        String user_gender = preferences.getString("userGender", "");
        String user_nickname = preferences.getString("userNickname", "");
        String userRank = preferences.getString("userRank", "미정");
        String userGroupRank = preferences.getString("userGroupRank", "미정");
        String userStep = preferences.getString("userStep", "");
        int score = 0;
        score+=Integer.parseInt(preferences.getString("input_mon", "3"));
        score+=Integer.parseInt(preferences.getString("input_tue", "3"));
        score+=Integer.parseInt(preferences.getString("input_wed", "3"));
        score+=Integer.parseInt(preferences.getString("input_thu", "3"));
        score+=Integer.parseInt(preferences.getString("input_fri", "3"));
        score+=Integer.parseInt(preferences.getString("input_sat", "3"));
        score+=Integer.parseInt(preferences.getString("input_sun", "3"));
        
        nickname.setText(user_nickname);
        allGroupRank.setText(userRank);
        groupRank.setText(userGroupRank);
        todaySteps.setText(userStep);
        goalSteps.setText("오늘의 추천 걸음 수는 "+today+" 걸음 입니다.:)"); //오늘 날짜 찾아서 넣기
        scorebyweek.setText("이번주 합계 "+String.valueOf(score)+"점");

        if(age >= 60){
            youtubeId1 = youtubeIdForOld[0];
            youtubeId2 = youtubeIdForOld[1];
            youtubeId3 = youtubeIdForOld[2];
            guide.setText(healthcareForOld);
        }
        else if(score <= 13){
            if(user_gender.equals("M")){
                youtubeId1 = youtubeIdForMen[0];
                youtubeId2 = youtubeIdForMen[1];
                youtubeId3 = youtubeIdForMen[2];
            }
            else{
                youtubeId1 = youtubeIdForWomen[0];
                youtubeId2 = youtubeIdForWomen[1];
                youtubeId3 = youtubeIdForWomen[2];
            }
            guide.setText(healthcareForYoung[0]);
        }
        else if(age <= 20){
            if(user_gender.equals("M")){
                youtubeId1 = youtubeIdForMen[3];
                youtubeId2 = youtubeIdForMen[4];
                youtubeId3 = youtubeIdForMen[5];
            }
            else{
                youtubeId1 = youtubeIdForWomen[3];
                youtubeId2 = youtubeIdForWomen[4];
                youtubeId3 = youtubeIdForWomen[5];
            }
            guide.setText(healthcareForYoung[1]);
        }
        else if(age <= 27){
            if(user_gender.equals("M")){
                youtubeId1 = youtubeIdForMen[6];
                youtubeId2 = youtubeIdForMen[7];
                youtubeId3 = youtubeIdForMen[8];
            }
            else{
                youtubeId1 = youtubeIdForWomen[6];
                youtubeId2 = youtubeIdForWomen[7];
                youtubeId3 = youtubeIdForWomen[8];
            }
            guide.setText(healthcareForYoung[2]);
        }
        else{
            if(user_gender.equals("M")){
                youtubeId1 = youtubeIdForMen[9];
                youtubeId2 = youtubeIdForMen[10];
                youtubeId3 = youtubeIdForMen[11];
            }
            else{
                youtubeId1 = youtubeIdForWomen[9];
                youtubeId2 = youtubeIdForWomen[10];
                youtubeId3 = youtubeIdForWomen[11];
            }
            guide.setText(healthcareForYoung[3]);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin= 30;

        YouTubePlayerView ypv1 = new YouTubePlayerView(MyPageActivity.this);
        ypv1.setLayoutParams(params);
        testLinearLayout.addView(ypv1);
        getLifecycle().addObserver(ypv1);
        ypv1.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(youtubeId1,0);
            }
        });

        YouTubePlayerView ypv2 = new YouTubePlayerView(MyPageActivity.this);
        ypv2.setLayoutParams(params);
        testLinearLayout.addView(ypv2);
        getLifecycle().addObserver(ypv2);
        ypv2.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(youtubeId2,0);
            }
        });

        YouTubePlayerView ypv3 = new YouTubePlayerView(MyPageActivity.this);
        params.bottomMargin= 100;
        ypv3.setLayoutParams(params);
        testLinearLayout.addView(ypv3);
        getLifecycle().addObserver(ypv3);
        ypv3.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(youtubeId3,0);
            }
        });
        YouTubePlayerView ypv4 = new YouTubePlayerView(MyPageActivity.this);
        ypv4.setLayoutParams(params);
        testLinearLayout.addView(ypv4);
        getLifecycle().addObserver(ypv4);
        ypv4.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo("hl-ii7W4ITg",0);
            }
        });
    }
}