package cbj.trailer.data;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("person_id")
    private String person_id;

    @SerializedName("password")
    private String password;

    public LoginRequest(String person_id, String password) {
        this.person_id = person_id;
        this.password = password;
    }
}
