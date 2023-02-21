package edu.vandy.recommender.client;

import edu.vandy.recommender.common.Movie;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

import static edu.vandy.recommender.common.Constants.EndPoint.*;

/**
 * This interface provides the contract for the RESTful {@code
 * DatabaseController} API used in conjunction with the {@code
 * GatewayApplication}.  It defines the HTTP GET and POST methods that
 * can be used to interact with the {@code DatabaseController} API,
 * along with the expected request and response parameters for each
 * method.  However, since clients access the {@code
 * DatabaseController} API via the {@code GatewayApplication} it's
 * necessary to add a {@code "{routename}"} prefix to each URL
 * mapping, along with the corresponding {@code @Path("routename")}
 * parameter to each method.
 *
 * This interface uses Retrofit annotations that provide metadata
 * about the API, such as the type of HTTP request (i.e., {@code GET}
 * or {@code PUT}), the parameter types (which are annotated with
 * {@code Path}, {@code Body}, or {@code Query} tags), and the
 * expected response format (which are all wrapped in {@link Call}
 * objects).  Retrofit uses these annotations and method signatures to
 * generate an implementation of the interface that the client uses to
 * make HTTP requests to the API.
 */
public interface DatabaseAPI {
    /**
     * Get a {@link List} containing the requested quotes.
     *
     * @param routename The microservice that performs the request,
     *                  which is dynamically inserted into the URI via
     *                  the {@code Path} annotation
     * @return An {@link Call} object that yields a {@link List}
     *         containing all the {@link Movie} objects on success and
     *         an error message on failure
     */
    // TODO -- you fill in here.

    /**
     * Get a {@link List} containing the requested {@link Movie}
     * objects.
     *
     * @param routename The microservice that performs the request,
     *                  which is dynamically inserted into the URI via
     *                  the {@code Path} annotation
     * @param query The {@link String} to search for
     * @return A {@link Call} object that yields a {@link List}
     *         containing all the {@link Movie} objects on success and
     *         an error message on failure
     */
    // TODO -- you fill in here.

    /**
     * Search for quotes containing the given {@link List} of {@code
     * queries}.
     *
     * @param routename The microservice that performs the request,
     *                  which is dynamically inserted into the URI via
     *                  the {@code Path} annotation
     * @param queries The {@link List} of {@code queries} to search
     *                for, which is passed in the body of the {@code
     *                POST} request
     * @return A {@link Call} object that yields a {@link List}
     *         containing all the {@link Movie} objects on success and
     *         an error message on failure
     */
    // TODO -- you fill in here.

    /**
     * Get a {@link List} containing the requested quotes.
     *
     * This endpoint also records the execution run time of this call
     * via the {@code Timer} microservice.
     * 
     * @param routename The microservice that performs the request,
     *                  which is dynamically inserted into the URI via
     *                  the {@code Path} annotation
     * @return An {@link Call} object that yields a {@link List}
     *         containing all the {@link Movie} objects on success and
     *         an error message on failure
     */
    // TODO -- you fill in here.

    /**
     * Get a {@link List} containing the requested {@link Movie}
     * objects.
     *
     * This endpoint also records the execution run time of this call
     * via the {@code Timer} microservice.
     *
     * @param routename The microservice that performs the request,
     *                  which is dynamically inserted into the URI via
     *                  the {@code Path} annotation
     * @param query The {@link String} to search for
     * @return A {@link Call} object that yields a {@link List}
     *         containing all the {@link Movie} objects on success and
     *         an error message on failure
     */
    // TODO -- you fill in here.

    /**
     * Search for quotes containing the given {@link List} of {@code
     * queries}.
     *
     * This endpoint also records the execution run time of this call
     * via the {@code Timer} microservice.
     *
     * @param routename The microservice that performs the request,
     *                  which is dynamically inserted into the URI via
     *                  the {@code Path} annotation
     * @param queries The {@link List} of {@code queries} to search
     *                for, which is passed in the body of the {@code
     *                POST} request
     * @return A {@link Call} object that yields a {@link List}
     *         containing all the {@link Movie} objects on success and
     *         an error message on failure
     */
    // TODO -- you fill in here.
}
