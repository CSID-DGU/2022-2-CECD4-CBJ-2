package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class InitialDataRequest {
    @SerializedName("stepsOf3weeks")
    private String[] stepsOf3weeks = new String[42];

    public InitialDataRequest(String[] stepsOf3weeks) {
        for (int i=0; i<42; i++){
            this.stepsOf3weeks[i] = stepsOf3weeks[i];
        }
    }
}
