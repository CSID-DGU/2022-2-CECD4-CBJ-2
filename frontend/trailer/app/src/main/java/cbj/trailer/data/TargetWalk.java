package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class TargetWalk {
    @SerializedName("date")
    private String date;

    @SerializedName("steps")
    private int steps;

    @SerializedName("dayOfWeek")
    private String dayOfWeek;

    public String getDate() {return date; }
    public int getSteps() {return steps; }
    public String getDayOfWeek() {return dayOfWeek; }

}
