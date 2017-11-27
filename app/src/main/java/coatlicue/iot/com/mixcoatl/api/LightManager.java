package coatlicue.iot.com.mixcoatl.api;

import retrofit2.Call;
import retrofit2.Callback;

public class LightManager {
    public static Call<Boolean> getState(Callback<Boolean> listener) {
        Call<Boolean> call = APIManager.getLightEndpoints().getState();
        call.enqueue(listener);
        return call;
    }

    public static Call<Boolean> setState(Callback<Boolean> listener, Boolean state) {
        Call<Boolean> call = APIManager.getLightEndpoints().setState(state);
        call.enqueue(listener);
        return call;
    }
}