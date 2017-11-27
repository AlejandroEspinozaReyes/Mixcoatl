package coatlicue.iot.com.mixcoatl.api;

import retrofit2.Call;
import retrofit2.Callback;

public class LockManager {
    public static Call<Boolean> getState(Callback<Boolean> listener) {
        Call<Boolean> call = APIManager.getLockEndpoints().getState();
        call.enqueue(listener);
        return call;
    }

    public static Call<Integer> setState(Callback<Integer> listener, Integer state) {
        Call<Integer> call = APIManager.getLockEndpoints().setState(state);
        call.enqueue(listener);
        return call;
    }
}