package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Target;
import java.util.List;

public class TargetWalkResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("mid")
    private List<TargetWalk> walkscore;

    public int getCode(){return code;}
    public List<TargetWalk> getWalkScore() {return walkscore;}



}
