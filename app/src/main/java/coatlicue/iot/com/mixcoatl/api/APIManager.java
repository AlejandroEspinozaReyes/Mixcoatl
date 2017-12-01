package coatlicue.iot.com.mixcoatl.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIManager {
    private static final String API_BASE_URL = "http://172.20.140.2:8080/Quetzalcoatl/iot/";
    private static OkHttpClient client;
    private static Retrofit mRetrofit;
    private static Gson mGson = new GsonBuilder().create();

    public static OkHttpClient getHttpClientBuilder() {
        if (client == null) {
            OkHttpClient.Builder mHttpClient = new OkHttpClient.Builder();
            mHttpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    // Request customization: add request headers
                    Request request = chain.request().newBuilder()
                            //.header(Configurations.API_CONTENT_TYPE_KEY, Configurations.API_APPLICATION_JSON)
                            //.header(Configurations.API_RESPONSE_TYPE_KEY, Configurations.API_APPLICATION_JSON)
                            //.header(Configurations.API_AUTH_KEY, Configurations.API_AUTH_VALUE)
                            .build();

                    return chain.proceed(request);
                }
            });
            client = mHttpClient.build();
        }
        return client;
    }

    public static Retrofit getRetrofit() {
        if (mRetrofit == null) {
            Retrofit.Builder builder =
                    new Retrofit.Builder()
                            .baseUrl(API_BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(mGson))
                    ;
            mRetrofit = builder.client(getHttpClientBuilder()).build();
        }

        return mRetrofit;
    }

    public static LightEndpoints getLightEndpoints() {
        return getRetrofit().create(LightEndpoints.class);
    }

    public static LockEndpoints getLockEndpoints() {
        return getRetrofit().create(LockEndpoints.class);
    }
}