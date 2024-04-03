package edu.vandy.recommender.common;

import edu.vandy.recommender.common.model.Movie;
import edu.vandy.recommender.common.model.Ranking;

import java.util.Collection;
import java.util.List;

/**
 * This Java utility class defines static methods that convert between
 * various representations, e.g., {@link Movie} objects to {@link
 * String} titles, {@link String} titles to {@link Ranking} objects
 * and vice versa, etc.
 */
public final class Converters {
    /**
     * Extract out the {@link Movie} titles from a {@link List}
     * of {@link Movie} objects.
     *
     * @param matchingMovies A {@link Collection} of {@link Movie}
     *                       objects
     * @return A {@link List} containing just the titles of
     *         the {@link Movie} objects
     */
    public static List<String> movies2titles
        (Collection<Movie> matchingMovies) {
        return matchingMovies
            // Convert the List to a Stream.
            .stream()

            // Extract the String title.
            .map(Movie::getTitle)

            // Convert the Stream to a List.
            .toList();
    }

    /**
     * Extract out the {@link Ranking} titles from a {@link List}
     * of {@link Ranking} objects.
     *
     * @param matchingMovies A {@link List} of {@link Movie} objects
     * @return A {@link List} containing just the titles of
     * the {@link Movie} objects
     */
    public static List<String> rankings2titles
        (Collection<Ranking> matchingMovies) {
        return matchingMovies
            // Convert the List to a Stream.
            .stream()

            // Extract the String title.
            .map(Ranking::getTitle)

            // Convert the Stream to a List.
            .toList();
    }

    /**
     * Create a {@link List} of {@link Ranking} objects from
     * a {@link List} of movie titles.
     *
     * @param movies A {@link List} of movie titles
     * @return A {@link List} containing {@link Ranking} objects
     */
    public static List<Ranking> titles2Rankings
        (Collection<String> movies) {
        return movies
            // Convert the List to a Stream.
            .stream()

            // Create a Ranking to encapsulate the title.
            .map(title ->
                 new Ranking(title, 0.0))

            // Convert the Stream to a List.
            .toList();
    }
}
