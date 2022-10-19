package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class CheckIdRequest {
    @SerializedName("UserId")
    private String userId;

    public CheckIdRequest(String userId) {
        this.userId = userId;
    }
}