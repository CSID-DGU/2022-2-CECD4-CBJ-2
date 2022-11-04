package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("person_id")
    private String userId;

    @SerializedName("password")
    private String userPassWord;

    public LoginRequest(String userId, String userPassWord) {
        this.userId = userId;
        this.userPassWord = userPassWord;
    }
}
