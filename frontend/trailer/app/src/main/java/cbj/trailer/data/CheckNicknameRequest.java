package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class CheckNicknameRequest {
    @SerializedName("nickname")
    private String nickname;

    public CheckNicknameRequest(String nickname) {
        this.nickname = nickname;
    }
}
