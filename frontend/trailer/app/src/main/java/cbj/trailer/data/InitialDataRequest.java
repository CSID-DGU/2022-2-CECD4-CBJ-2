package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class InitialDataRequest {
    @SerializedName("person_id")
    private String person_id;

    @SerializedName("stepsOf3weeks")
    private String[] stepsOf3weeks = new String[42];
    //['2022-01-01', '2002', '2022-01-02', '1000' ...]

    public InitialDataRequest(String person_id, String[] stepsOf3weeks) {
        this.person_id = person_id;
        for (int i=0; i<42; i++){
            this.stepsOf3weeks[i] = stepsOf3weeks[i];
        }
    }
}
