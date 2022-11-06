package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("person_id")
    private String person_id;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("ResultCode")
    private int ResultCode;

    public String getUserId() {
        return person_id;
    }
    public String getUserNickname() { return nickname; }
    public int getCode() {
        return ResultCode;
    }
}
