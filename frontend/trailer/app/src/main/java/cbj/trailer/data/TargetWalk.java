package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class TargetWalk {
    @SerializedName("person_id")
    private String person_id;

    @SerializedName("mon")
    private int mon;

    @SerializedName("tue")
    private int tue;

    @SerializedName("wed")
    private int wed;

    @SerializedName("thur")
    private int thur;

    @SerializedName("fri")
    private int fri;

    @SerializedName("sat")
    private int sat;

    @SerializedName("sun")
    private int sun;


    public String getPerson_id(){return person_id;}
    public int getMon(){return mon;}
    public int getTue(){return tue;}
    public int getWed(){return wed;}
    public int getThur(){return thur;}
    public int getFri(){return fri;}
    public int getSat(){return sat;}
    public int getSun(){return sun;}


}
