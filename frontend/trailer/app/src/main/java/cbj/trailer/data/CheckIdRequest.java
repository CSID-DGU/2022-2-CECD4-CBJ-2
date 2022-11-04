package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class CheckIdRequest {
    @SerializedName("person_id")
    private String userId;

    public CheckIdRequest(String userId) {
        this.userId = userId;
    }
}