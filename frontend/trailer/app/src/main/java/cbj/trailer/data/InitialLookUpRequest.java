package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class InitialLookUpRequest {
    @SerializedName("person_id")
    private String person_id;

    public InitialLookUpRequest(String person_id) {
        this.person_id = person_id;
    }
}
