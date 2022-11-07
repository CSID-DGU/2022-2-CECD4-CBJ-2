package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class TargetStepsOfDayResponse {
    @SerializedName("targetSteps")
    private String[] stepsOfweek = new String[7];

    @SerializedName("ResultCode")
    private int resultCode;

    public String[] getTargetSteps() {
        return stepsOfweek;
    }
    public int getCode() {
        return resultCode;
    }
}
