package cbj.trailer.network;

import cbj.trailer.data.InitialDataRequest;
import cbj.trailer.data.JoinRequest;
import cbj.trailer.data.CodeResponse;
import cbj.trailer.data.LoginRequest;
import cbj.trailer.data.LoginResponse;
import cbj.trailer.data.targetStepsOfDayResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceApi {
    @POST("/login")                                             // 로그인 API
    Call<LoginResponse> userLogin(@Body LoginRequest data);

    @POST("/join")                                              // 회원가입 API
    Call<CodeResponse> userJoin(@Body JoinRequest data);

    @POST("/initial")
    Call<targetStepsOfDayResponse> initialData(@Body InitialDataRequest data);
    /**
    @GET("/check/id")                                           // 아이디 중복검사 API
    Call<CodeResponse> userCheckID(@Query("UserId") String userId);

    @GET("/check/name")                                         // 이름 중복검사 API
    Call<CodeResponse> userCheckName(@Query("UserName") String userName);
    */
}