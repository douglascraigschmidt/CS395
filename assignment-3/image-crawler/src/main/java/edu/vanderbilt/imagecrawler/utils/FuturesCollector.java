package edu.vanderbilt.imagecrawler.utils;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Implements a custom collector that converts a stream of
 * CompletableFuture objects into a single CompletableFuture that is
 * triggered when all the futures in the stream complete.
 *
 * See
 * http://www.nurkiewicz.com/2013/05/java-8-completablefuture-in-action.html
 * for more info.
 */
public class FuturesCollector<T>
       implements Collector<CompletableFuture<T>,
  		  Array<CompletableFuture<T>>,
                  CompletableFuture<Array<T>>> {
    /**
     * A function that creates and returns a new mutable result
     * container that will hold all the CompletableFutures in the
     * stream.
     *
     * @return a function which returns a new, mutable result container
     */
    @Override
    public Supplier<Array<CompletableFuture<T>>> supplier() {
        return UnsynchronizedArray::new;
    }

    /**
     * A function that folds a CompletableFuture into the mutable
     * result container.
     *
     * @return a function which folds a value into a mutable result container
     */
    @Override
    public BiConsumer<Array<CompletableFuture<T>>, CompletableFuture<T>> accumulator() {
        return Array::add;
    }

    /**
     * A function that accepts two partial results and merges them.
     * The combiner function may fold state from one argument into the
     * other and return that, or may return a new result container.
     *
     * @return a function which combines two partial results into a combined
     * result
     */
    @Override
    public BinaryOperator<Array<CompletableFuture<T>>> combiner() {
        return (Array<CompletableFuture<T>> one,
                Array<CompletableFuture<T>> another) -> {
            one.addAll(another);
            return one;
        };
    }

    /**
     * Perform the final transformation from the intermediate
     * accumulation type {@code A} to the final result type {@code R}.
     *
     * @return a function which transforms the intermediate result to
     * the final result
     */
    @Override
    public Function<Array<CompletableFuture<T>>, CompletableFuture<Array<T>>> finisher() {
        return futures
            -> CompletableFuture
            // Use CompletableFuture.allOf() to obtain a
            // CompletableFuture that will itself complete when all
            // CompletableFutures in futures have completed.
            .allOf(futures
                   .stream()
                   .toArray(CompletableFuture[]::new))

            // When all futures have completed get a CompletableFuture
            // to an array of joined elements of type T.
            .thenApply(v -> futures
                       // Convert futures into a stream of completable
                       // futures.
                       .stream()

                       // Use map() to join() all completablefutures
                       // and yield objects of type T.  Note that
                       // join() should never block.
                       .map(CompletableFuture::join)

                       // Collect the results of type T into an array.
                       .collect(ArrayCollector.toArray()));
    }

    /**
     * Returns a {@code Set} of {@code Collector.Characteristics}
     * indicating the characteristics of this Collector.  This set
     * should be immutable.
     *
     * @return An immutable set of collector characteristics, which in
     * this case is simply UNORDERED
     */
    @SuppressWarnings("unchecked")
    @Override
    public Set characteristics() {
        return Collections.singleton(Characteristics.UNORDERED);
    }

    /**
     * This static factory method creates a new FuturesCollector.
     *
     * @return A new FuturesCollector()
     */
    public static <T> Collector<CompletableFuture<T>, ?, CompletableFuture<Array<T>>>
        toFuture() {
        return new FuturesCollector<>();
    }
}
