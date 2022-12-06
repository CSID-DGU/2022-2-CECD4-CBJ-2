package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("person_id")
    private String person_id;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("age")
    private int userAge;

    @SerializedName("gender")
    private String gender;

    @SerializedName("ResultCode")
    private int ResultCode;

    public String getUserId() {
        return person_id;
    }
    public String getUserNickname() { return nickname; }
    public int getUserAge(){return userAge;}
    public String getUserGender(){return gender;}
    public int getCode() {
        return ResultCode;
    }
}
