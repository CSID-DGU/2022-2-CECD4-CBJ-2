package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;
public class JoinRequest {
    @SerializedName("UserId")
    private String userId;

    @SerializedName("UserPwd")
    private String userPwd;

    @SerializedName("UserNickName")
    private String userNickName;

    @SerializedName("UserAge")
    private int userAge;

    @SerializedName("UserSex")
    private String userSex;

    @SerializedName("UserHomeAddress")
    private String userHomeAddress;

    @SerializedName("UserCompanyAddress")
    private String userCompanyAddress;

    @SerializedName("UserCategory")
    private String userCategory;

    @SerializedName("UserExerciseIntensity")
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