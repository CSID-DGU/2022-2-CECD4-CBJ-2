package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RankListResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("rank")
    private List<RankData> rank;

    public int getCode(){return code;}
    public List<RankData> getRank() {return rank;}
}
