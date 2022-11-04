package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class InitialDataRequest {
    @SerializedName("person_id")
    private String userId;

    @SerializedName("stepsOf3weeks")
    private String[] stepsOf3weeks = new String[42];
    //['2022-01-01', '2002', '2022-01-02', '1000' ...]

    public InitialDataRequest(String userId, String[] stepsOf3weeks) {
        this.userId = userId;
        for (int i=0; i<42; i++){
            this.stepsOf3weeks[i] = stepsOf3weeks[i];
        }
    }
}
