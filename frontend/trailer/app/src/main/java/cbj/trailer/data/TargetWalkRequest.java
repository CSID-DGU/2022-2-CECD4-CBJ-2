package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class TargetWalkRequest {
    @SerializedName("walkscore/person_id")
    private String walkscore;

    public TargetWalkRequest(String walkscore) {
        this.walkscore = walkscore;
    }


}
