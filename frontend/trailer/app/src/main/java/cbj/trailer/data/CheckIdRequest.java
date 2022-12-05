package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class CheckIdRequest {
    @SerializedName("person_id")
    private String person_id;

    public CheckIdRequest(String person_id) {
        this.person_id = person_id;
    }
}