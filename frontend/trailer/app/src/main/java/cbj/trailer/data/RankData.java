package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class RankData {
    @SerializedName("rankIndex")
    private int rankIndex;

    @SerializedName("nickname")
    private String userNickName;

    @SerializedName("steps")
    private int steps;

    @SerializedName("grade")
    private String grade;

    public int getRankIndex(){return rankIndex;}
    public String getUserNickName(){return userNickName;}
    public int getSteps(){return steps;}
    public String getGrade(){return grade;}
}
