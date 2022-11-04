package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("person_id")
    private String userId;

    @SerializedName("nickname")
    private String userNickname;

    @SerializedName("ResultCode")
    private int resultCode;

    public String getUserId() {
        return userId;
    }
    public String getUserNickname() { return userNickname; }
    public int getCode() {
        return resultCode;
    }
}
