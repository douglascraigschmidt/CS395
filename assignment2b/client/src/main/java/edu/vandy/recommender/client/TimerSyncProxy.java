package edu.vandy.recommender.client;

import edu.vandy.recommender.utils.CallUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static edu.vandy.recommender.common.Constants.Service.TIMER;

/**
 * This class is a proxy to the {@code Timer} microservice.
 */
@Component
public class TimerSyncProxy {
    /**
     * Create an instance of the {@link TimerAPI} Retrofit client,
     * which is then used to making HTTP requests to the {@code
     * GatewayApplication} RESTful microservice.
     */
    @Autowired
    TimerAPI mTimerAPI;

    /**
     * Get a {@link String} containing the recorded timing summaries
     * on success or throws {@link IOException} on failure.
     *
     * @return A {@link String} containing the timing results for all
     *         timings ordered from fastest to slowest
     */
    public String getTimings() {
        // TODO -- you fill in here by replacing 'return null' with
        // the appropriate helper method provided by CallUtils.

        return null;
    }
}

