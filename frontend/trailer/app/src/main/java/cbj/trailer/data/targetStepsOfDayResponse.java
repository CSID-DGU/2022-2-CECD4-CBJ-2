package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class targetStepsOfDayResponse {
    @SerializedName("targetSteps")
    private String[] stepsOf3weeks = new String[7];

    @SerializedName("ResultCode")
    private int resultCode;

    public String[] getTargetSteps() {
        return stepsOf3weeks;
    }
    public int getCode() {
        return resultCode;
    }
}
