package cbj.trailer.network;

import cbj.trailer.data.CheckIdRequest;
import cbj.trailer.data.InitialDataRequest;
import cbj.trailer.data.JoinRequest;
import cbj.trailer.data.CodeResponse;
import cbj.trailer.data.LoginRequest;
import cbj.trailer.data.LoginResponse;
import cbj.trailer.data.TargetStepsOfDayResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceApi {
    @Headers({"Content-Type: application/json"})
    @POST("/login")                                             // 로그인 API
    Call<LoginResponse> userLogin(@Body LoginRequest data);

    @Headers({"Content-Type: application/json"})
    @POST("/join")                                              // 회원가입 API
    Call<CodeResponse> userJoin(@Body JoinRequest data);

    @Headers({"Content-Type: application/json"})
    @POST("/initialData")
    Call<CodeResponse> initialData(@Body InitialDataRequest data);

    @Headers({"Content-Type: application/json"})
    @POST("/check/id")                                           // 아이디 중복검사 API
    Call<CodeResponse> userCheckID(@Body CheckIdRequest data);

}