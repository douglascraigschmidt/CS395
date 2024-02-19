package edu.vandy.recommender.common;

import edu.vandy.recommender.client.DatabaseAPI;
import edu.vandy.recommender.client.TimerAPI;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.List;
import java.util.Map;

import static edu.vandy.recommender.common.Constants.GATEWAY_BASE_URL;

/**
 * This class contains a {@code Bean} annotation that can be injected
 * into classes using the Spring {@code @Autowired} annotation.
 */
@Component
public class ClientBeans {
    /**
     * This factory method returns a new {@link RestTemplate}, which
     * enables a client to perform HTTP requests synchronously.
     *
     * @return A new {@link RestTemplate}
     */
    @Bean
    public RestTemplate getRestTemplate() {
        // Make a new RestTemplate.
        RestTemplate restTemplate = new RestTemplate();

        // Set the base URL for the RestTemplate to use
        // the API Gateway URL.

        // TODO -- you fill in here.

        // Return restTemplate.
        return restTemplate;
    }

    /**
     * Create an instance of the {@link DatabaseAPI} Retrofit client
     * that the GsonConverterFactory and make HTTP requests to the
     * {@code GatewayApplication} RESTful microservice.
     *
     * @return A {@link DatabaseAPI} instance
     */
    @Bean
    public DatabaseAPI getDatabaseAPI() {
        // TODO -- you fill in here.
        return null;
    }

    /**
     * Create an instance of the {@link TimerAPI} Retrofit client that
     * uses ScalarsConverterFactory and makes HTTP requests to the
     * {@code GatewayApplication} RESTful microservice.
     *
     * @return A {@link TimerAPI} instance
     */
    @Bean
    public TimerAPI getTimingAPI() {
        // TODO -- you fill in here.
        return null;
    }

    /**
     * Loads the {@code dataset} and returns a {@link Map} of {@link
     * String} and {@link List<Double>} objects.
     *
     * @param dataset The name of the dataset containing movie-related
     *                data
     * @return A {@link Map} of {@link String} and {@link List<Double>} objects
     */
    @Bean
    public Map<String, List<Double>> movieMap
    // The @Value annotation injects values into fields in
    // Spring-managed beans.
    (@Value("${app.dataset}") final String dataset) {
        return MovieDatasetReader
            .loadMovieData(dataset);
    }
}
