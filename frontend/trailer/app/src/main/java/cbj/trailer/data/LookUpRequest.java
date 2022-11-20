package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class LookUpRequest {
    @SerializedName("person_id")
    private String person_id;

    @SerializedName("gender")
    private String gender;

    @SerializedName("age")
    private int age;

    public LookUpRequest(String person_id, String gender, int age) {
        this.person_id = person_id;
        this.age = age;
        this.gender = gender;
    }
}