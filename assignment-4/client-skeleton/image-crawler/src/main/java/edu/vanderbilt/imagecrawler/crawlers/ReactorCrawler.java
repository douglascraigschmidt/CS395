package edu.vanderbilt.imagecrawler.crawlers;

import org.reactivestreams.Publisher;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import edu.vanderbilt.imagecrawler.utils.Crawler;
import edu.vanderbilt.imagecrawler.utils.Image;
import edu.vanderbilt.imagecrawler.web.RemoteDataSource;
import edu.vanderbilt.imagecrawler.web.TransformedImage;
import okhttp3.MultipartBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static edu.vanderbilt.imagecrawler.utils.Crawler.Type.IMAGE;
import static edu.vanderbilt.imagecrawler.utils.Crawler.Type.PAGE;

/**
 * This class uses Project Reactor Flux and Mono reactive types to
 * perform a concurrent image crawl starting from a root Uri.  Images
 * from HTML pages reachable from the root Uri are downloaded from a
 * remote web server or from the local file system, transformed, and
 * then stored in files that can be displayed to the user.
 */
public class ReactorCrawler
        extends ImageCrawler {
    /**
     * Recursively crawls the given page and returns the total number
     * of processed images.
     *
     * @param pageUri The URI that's being crawled at this point
     * @param depth   The current depth of the recursive processing
     * @return The number of images processed at this depth
     */
    @Override
    protected int performCrawl(String pageUri, int depth) {
        // Call the crawlPageAsync() helper method to perform the
        // crawl asynchronously, obtain a count of the number of
        // images downloaded/processed, wait until all the processing
        // is done, and return the result as an int value.  If any
        // error/exception is encountered return 0.

        // TODO -- you fill in here replacing this statement with your
        // solution.
        return 0;
                // Crawl the page using the specified uri and depth.

                        // Count the number of elements in the stream of
                        // observables.

                        // Return 0 if any exception is encountered.

                        // Block to get the result.

                // Convert the result to an int primitive.
    }

    /**
     * Recursively crawls the web page identified by the passed {@code
     * pageUri} and downloads and transforms all discovered images.
     * The recursion ends when the passed depth is exceeded.
     *
     * @param pageUri The uri to crawl
     * @param depth   The maximum depth of the crawl
     * @return A {@link Flux} that emits transformed images
     */
    protected Flux<Image> crawlPageAsync(String pageUri, int depth) {
        // Return a Flux stream of images from this page and all page
        // links recursively reachable from it.  Return an empty
        // Observable if the depth limit of the web crawling is
        // reached or if the pageUri has already been visited.  This
        // method should use Flux operators like just(), filter(),
        // map(), and flatMap() (this latter operator should call
        // imagesOnPageAndPageLinksAsync()).

        // TODO -- you fill in here replacing this statement with your
        // solution.
        return null;
                // Create an Observable that emits this pageUri.

                // Filter out page if it exceeds the depth or has already
                // been visited.

                // Map the url to a page.

                // Apply the flatMap() concurrency idiom to convert each
                // page to an Observable stream of images asynchronously.
    }

    /**
     * Returns all images on the page and its linked pages.
     *
     * @param page  The crawler page to crawl
     * @param depth The maximum depth of the crawl
     * @return A {@link Flux} that emits all images on the page and its linked pages
     */
    protected Flux<Image> imagesOnPageAndPageLinksAsync(Crawler.Page page,
                                                        int depth) {
        // Return a Flux stream consisting of all images on this page
        // and any images linked on it.  This method should call
        // imagesOnPageLinks() and imagesOnPage() and use Flux
        // operators mergeWith() and subscribeOn() to merge all the
        // images into a single Flux stream via a parallel Scheduler.

        // TODO -- you fill in here replacing this statement with your
        // solution.
        return null;
        // Run computations in the parallel scheduler.

        // Merge images with an Observable stream of all images on
        // this page.
    }

    /**
     * Find all page links on the passed page and recursively crawl
     * each page to download and transform all discovered images.
     *
     * @param page The page to search for page links
     * @return A {@link Flux} that emits non-null image transforms
     */
    protected Flux<Image> imagesOnPageLinksAsync(Crawler.Page page,
                                                 int depth) {
        // Create and return a Flux stream consisting of images that
        // have been downloaded and transformed.  This method should
        // call crawlPageAsync() recursively and use Flux operators
        // like fromIterable(), flatMap(), and subscribeOn() to
        // perform the processing on the parallel Scheduler.

        // TODO -- you fill in here replacing this statement with your
        // solution.
        return null;
                // Convert the list of page links into an Observable
                // stream of page links.

                // Apply the flatMap() concurrency idiom to map each page
                // to a stream of images that are downloaded and
                // transformed concurrently via the parallel scheduler.
    }

    /**
     * Find, download, and transform all images on the given page.
     *
     * @param page The page to search for image urls
     * @return A {@link Flux} that emits image transforms
     */
    protected Flux<Image> imagesOnPageAsync(Crawler.Page page) {
        // Create and return a Flux stream for this page using the
        // Flux flatMap() concurrency idiom.  It should call helper
        // methods getPageElementsAsUrls(), downloadImageAsync(), and
        // transformImageAsync(), as well as use Flux operators like
        // flatmap() and fromIterable() perform processing on the
        // parallel scheduler.  The flatMap() operator can be called
        // more than once.

        // TODO -- you fill in here replacing this statement with your
        // solution.
        return null;
                // Convert the list of images in the page into an
                // Flux stream.

                // Apply the flatMap() concurrency idiom to download
                // the stream of images in parallel.

                // Again apply the flatMap() concurrency idiom to convert
                // the stream of downloaded images into a stream of images
                // are transformed in parallel.
    }

    /**
     * Asynchronously download and return the image at the given
     * {@code url}
     *
     * @param url The URL to download
     * @return A {@link Mono} that emits a single downloaded image
     */
    protected Mono<Image> downloadImageAsync(URL url) {
        // Create and return a Mono instance that emits the download
        // image at the specified URL. It should call the helper
        // method getOrDownloadImage(), as well as use Mono operators
        // like fromCallable(), mapNotNull(), and subscribeOn() to
        // perform processing on the parallel scheduler.

        // TODO -- you fill in here replacing this statement with your
        // solution.
        return null;
                // Create an Observable that emits the URL.

                // Run computation in the parallel scheduler.

                // Map the image URL to a possibly downloaded image,
                // ignoring any null values that may occur.
    }

    /**
     * Routes this transform image request to either a local or
     * remote transform image handler.
     *
     * @param image The image to transform
     * @return A {@link Flux} that emits non-null transformed images
     */
    protected Flux<Image> transformImageAsync(Image image) {
        // Check super class flag to determine if transforms should be
        // run locally or on a remote server using microservices.
        return runRemoteTransforms()
                ? transformImageRemotely(image)
                : transformImageLocally(image);
    }

    /**
     * Locally applies the current set of crawler transforms on the
     * passed image and returns an Observable stream of all successfully
     * transformed images.
     *
     * @param image The image to transform
     * @return A {@link Flux} that emits non-null transformed images
     */
    protected Flux<Image> transformImageLocally(Image image) {
        // Run transforms locally returning a Flux stream of
        // transformed images.  Return a Flux stream of transformed
        // images.  This method should call the createNewCacheItem()
        // and applyTransform() helper methods, as well as use Flux
        // operators like fromIterable(), filter(), and mapNotNull().

        // TODO -- you fill in here replacing this statement with
        // your solution.
        return null;
                // Convert the List of transforms into an stream of
                // transforms.

                // Run computations in the parallel scheduler.

                // Only transform images that haven't already been
                // transformed.

                // Apply the transform on the image, ignoring any
                // null values that are returned.
    }

    /**
     * Calls remote server to perform transforms on the passed {@code image}.
     *
     * @param image Source image to transform remotely
     * @return A {@link Flux} that emits transformed {@link Image} objects
     */
    protected Flux<Image> transformImageRemotely(Image image) {
        // Run transforms remotely returning a Flux stream of
        // transformed images.  Return a Flux stream of transformed
        // images.  This method should call the
        // buildMultipartBodyPart(), getTransformNames(),
        // applyReactorTransforms(), and createImage() helper methods, as
        // well as use Mono and Flux operators, such as flatMapMany()
        // and map(), respectively.

        // Get remote microservices API instance.
        RemoteDataSource.TransformApi api = getRemoteDataSource().getApi();

        // Get the remote data source instance from the super class.
        RemoteDataSource remoteDataSource = getRemoteDataSource();

        // Call RemoteDataSource helper to build a MultipartBody.Part
        // containing the image bytes.
        // TODO -- you fill in here replacing null with your solution.
        MultipartBody.Part imagePart = null;

        // Call super class helper method to get the list of transforms.
        // TODO -- you fill in here replacing null with your solution.
        List<String> transformNames = null;

        // Call api method to build a Flux stream of transforms
        // that, once subscribed to, will using microservices to
        // concurrently run all transform operations.
        // TODO -- you fill in here replacing this statement with your
        // solution.
        return null;
                // Call API to perform transformations on the image.

                // Convert Mono<List<TransformedImage>> to
                // Flux<TransformedImage>.

                // Create an Image instance to return to caller.
    }
}
