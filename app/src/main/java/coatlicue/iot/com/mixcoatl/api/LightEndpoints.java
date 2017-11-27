package coatlicue.iot.com.mixcoatl.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LightEndpoints {
    @GET("SwitchService/state")
    Call<Boolean> getState();

    @GET("SwitchService/state/{state}")
    Call<Boolean> setState(@Path("state") Boolean state);
}
