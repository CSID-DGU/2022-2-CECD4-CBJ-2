package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Target;
import java.util.List;

public class TargetWalkResponse {
    @SerializedName("ResultCode")
    private int resultCode;

    @SerializedName("mid")
    private List<TargetWalk> mid;

    public int getResultCode() {return resultCode;}
    public List<TargetWalk> getMid() {return mid;}



}
