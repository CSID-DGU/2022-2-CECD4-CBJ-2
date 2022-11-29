package cbj.trailer.network;

import cbj.trailer.data.CheckIdRequest;
import cbj.trailer.data.CheckNicknameRequest;
import cbj.trailer.data.InitialDataRequest;
import cbj.trailer.data.InitialLookUpRequest;
import cbj.trailer.data.InitialRankListResponse;
import cbj.trailer.data.JoinRequest;
import cbj.trailer.data.CodeResponse;
import cbj.trailer.data.LoginRequest;
import cbj.trailer.data.LoginResponse;
import cbj.trailer.data.LookUpRequest;

import cbj.trailer.data.RankListResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

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

    @Headers({"Content-Type: application/json"})
    @POST("/check/nickname")                                           // 아이디 중복검사 API
    Call<CodeResponse> userCheckNickname(@Body CheckNicknameRequest data);

    @Headers({"Content-Type: application/json"})
    @POST("/initialrank")                                           // 랭크 조회 API
    Call<InitialRankListResponse> defaultRank(@Body InitialLookUpRequest data);

    @Headers({"Content-Type: application/json"})
    @POST("/rank")                                           // 그룹화된 랭크 조회 API
    Call<RankListResponse> userRank(@Body LookUpRequest data);

}