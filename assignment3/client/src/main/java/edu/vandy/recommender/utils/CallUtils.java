package edu.vandy.recommender.utils;

import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static edu.vandy.recommender.utils.ExceptionUtils.rethrowSupplier;

/**
 * This Java utility class defines method(s) that are useful
 * in conjunction with Retrofit.
 */
public class CallUtils {
    /**
     * A Java utility class should have a private constructor.
     */
    private CallUtils() {}

    /**
     * Execute the {@link Call} and return the {@link T} received from
     * the server on success and throws an {@link IOException} on
     * failure.
     *
     * @param call The {@link Call} returned from the Retrofit client
     *             API
     * @return The {@link T} received from the server on success
     */
    public static <T> T executeCall(Call<T> call){
        return rethrowSupplier(() -> {
            // Execute the call.
            Response<T> response = call.execute();

            // If the request is successful return the body
            // (which is a List).
            if (response.isSuccessful()) {
                return response.body();
            } else {
                // If there's a failure then find out what failed
                // throw IOException.
                int statusCode = response.code();
                assert response.errorBody() != null;
                String errorMessage = response.errorBody().string();
                System.out.println("Request failed with status code "
                        + statusCode
                        + ": "
                        + errorMessage);
                throw new IOException();
            }
        }).get();
    }
}
