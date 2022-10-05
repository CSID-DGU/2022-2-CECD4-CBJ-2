package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("UserName")
    private String userName;

    @SerializedName("ResultCode")
    private int resultCode;

    public String getUserName() {
        return userName;
    }
    public int getCode() {
        return resultCode;
    }
}
