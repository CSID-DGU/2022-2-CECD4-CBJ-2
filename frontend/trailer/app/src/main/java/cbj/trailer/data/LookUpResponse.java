package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class LookUpResponse {
    @SerializedName("ResultCode")
    private int ResultCode;

    @SerializedName("rank")
    private String[] groupRank = new String[28]; //No, 닉네임, 걸음 수, 등급 * 7

    public String[] getGroupRank(){return groupRank;}

    public int getCode() {
        return ResultCode;
    }
}
