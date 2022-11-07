package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("UserId")
    private String userId;

    @SerializedName("UserPassWord")
    private String userPassWord;

    public LoginRequest(String userId, String userPassWord) {
        this.userId = userId;
        this.userPassWord = userPassWord;
    }
}
