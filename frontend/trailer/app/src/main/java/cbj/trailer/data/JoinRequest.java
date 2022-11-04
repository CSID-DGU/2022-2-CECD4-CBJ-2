package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;
public class JoinRequest {
    @SerializedName("person_id")
    private String userId;

    @SerializedName("UserPwd")
    private String userPwd;

    @SerializedName("nickname")
    private String userNickName;

    @SerializedName("age")
    private int userAge;

    @SerializedName("gender")
    private String userSex;

    @SerializedName("home_address")
    private String userHomeAddress;

    @SerializedName("comp_address")
    private String userCompanyAddress;

    @SerializedName("category")
    private String userCategory;

    @SerializedName("strength")
    private String userExerciseIntensity;

    public JoinRequest(String userId, String userPwd, String userNickName, int userAge, String userSex, String userHomeAddress, String userCompanyAddress, String userCategory, String userExerciseIntensity) {
        this.userId = userId;
        this.userPwd = userPwd;
        this.userNickName = userNickName;
        this.userAge = userAge;
        this.userSex = userSex;
        this.userHomeAddress = userHomeAddress;
        this.userCompanyAddress = userCompanyAddress;
        this.userCategory = userCategory;
        this.userExerciseIntensity = userExerciseIntensity;
    }
}