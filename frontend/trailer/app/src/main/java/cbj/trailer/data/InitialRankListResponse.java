package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InitialRankListResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("rank")
    private List<RankData> rank;

    @SerializedName("userGroupRank")
    private String userGroupRank;

    @SerializedName("allUserRank")
    private String allUserRank;

    public int getCode(){return code;}
    public List<RankData> getRank() {return rank;}
    public String getUserGroupRank() {return userGroupRank;}
    public String getAllUserRank() {return allUserRank;}
}
