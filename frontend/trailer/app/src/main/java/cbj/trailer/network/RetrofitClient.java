package cbj.trailer.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private final static String BASE_URL = ""; //서버 url
    private static Retrofit retrofit = null;

    private RetrofitClient() {
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()                           // 통로를 만들 작업인원을 생성
                    .baseUrl(BASE_URL)                                  // 통로의 목적지
                    .addConverterFactory(GsonConverterFactory.create()) // 통로를 통해 전달할 택배를 해석할 정보
                    .build();                                           // 통로를 만듦
        }
        return retrofit;                                                // 만들어진 통로 리턴
    }
}