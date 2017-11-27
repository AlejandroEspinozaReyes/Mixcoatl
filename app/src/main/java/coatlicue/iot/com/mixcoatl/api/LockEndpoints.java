package coatlicue.iot.com.mixcoatl.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LockEndpoints {
    @GET("LockService/state")
    Call<Boolean> getState();

    @GET("LockService/state/{state}")
    Call<Integer> setState(@Path("state") Integer state);
}