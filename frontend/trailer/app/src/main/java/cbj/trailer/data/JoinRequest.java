package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;
public class JoinRequest {
    @SerializedName("UserId")
    private String userId;

    @SerializedName("UserPassWord")
    private String userPassWord;

    public JoinRequest(String userId, String userPassWord, String userName) {
        this.userId = userId;
        this.userPassWord = userPassWord;
    }
}
