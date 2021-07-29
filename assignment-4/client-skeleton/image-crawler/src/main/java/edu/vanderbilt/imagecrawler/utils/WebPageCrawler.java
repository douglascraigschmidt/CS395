package edu.vanderbilt.imagecrawler.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import edu.vanderbilt.imagecrawler.crawlers.ImageCrawler;
import edu.vanderbilt.imagecrawler.platform.Controller;

import static edu.vanderbilt.imagecrawler.utils.Crawler.Type.PAGE;
import static edu.vanderbilt.imagecrawler.utils.Student.isUndergraduate;

/**
 * This helper class works around deficiencies in the jsoup library
 * (www.jsoup.org), which doesn't make web-based crawling and local
 * filesystem crawling transparent out-of-the-box..
 */
public class WebPageCrawler implements Crawler {
    /**
     * Platform dependent function that mas a uri string to an InputStream.
     */
    private Function<String, InputStream> mMapUrlToStream;

    /**
     * Constructor required for handling platform dependent local crawling.
     *
     * @param mapUrlToStream A platform dependent function that mas a uri
     *                       string to an InputStream.
     */
    public WebPageCrawler(Function<String, InputStream> mapUrlToStream) {
        mMapUrlToStream = mapUrlToStream;
    }

    /**
     * @return A container that wraps the HTML document associated
     * with the {@code pageUri}.
     */
    public Page getPage(String uri) {
        ImageCrawler.throwExceptionIfCancelled();

        if (mMapUrlToStream != null) {
            // Web page is read from a local source requiring
            // requiring the web page to be read from an input
            // stream.

            if (Controller.loggingEnabled()) {
                System.out.println("***************************************");
                System.out.println("GET CONTAINER URI       = " + uri);
            }

            // Build a base (parent) URI from the passed uri.
            String baseUri = uri;
            if (uri.endsWith(".html") || uri.endsWith("htm")) {
                baseUri = uri.substring(0, uri.lastIndexOf('/')) + "/";
            }

            // Jsoup parser requires the base URI to end with a "/";
            if (!baseUri.endsWith("/")) {
                baseUri += "/";
            }

            // Since this is a local crawl make sure that we don't ever
            // try to get an input stream on just a directory; add the
            // index.html file if necessary to prevent that.
            if (!uri.endsWith("index.html")) {
                uri += "/index.html";
            }

            if (Controller.loggingEnabled()) {
                System.out.println("GET CONTAINER BASE URI  = " + baseUri);
                System.out.println("***************************************");
            }

            // Map the uri to an input stream and call Jsoup to
            // read in stream contents and return a DocumentPage.
            try (InputStream inputStream = mMapUrlToStream.apply(uri)) {
                return new DocumentPage(
                        Jsoup.parse(inputStream, "UTF-8", baseUri),
                        uri);
            } catch (Exception e) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("getContainer Exception: " + e);
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                throw new RuntimeException(e);
            }
        } else {
            // Web page is a remote URL.

            // This function (1) connects to a URL and gets its
            // contents and (2) converts checked exceptions to runtime
            // exceptions.
            Function<String, Document> connect =
                    ExceptionUtils.rethrowFunction(
                            url -> Jsoup.connect(url).get());

            Document apply = connect.apply(uri);
            return new DocumentPage(apply, uri);
        }
    }

    /**
     * Encapsulates/hides the JSoup Document object into a generic container.
     */
    protected class DocumentPage implements Page {
        private String uri;
        private Document document;

        protected DocumentPage(Document document, String uri) {
            if (Controller.loggingEnabled()) {
                System.out.println(">*********************************************");
                System.out.println("WebPageCrawler: constructor()");
                System.out.println("Constructed document: " + (uri == null ? "NULL" : uri));
                System.out.println("             baseURL: " + (document == null
                        ? "NULL"
                        : document.baseUri()));
                System.out.println("<*********************************************");
            }

            this.document = document;
            this.uri = uri;
        }

        @Override
        public List<WebPageElement> getPageElements(Type... types) {
            ImageCrawler.throwExceptionIfCancelled();

            List<WebPageElement> results = new ArrayList<>();

            Arrays.stream(types)
                    .flatMap(type -> {
                        if (type == PAGE) {
                            return document.select("a[href]")
                                    .stream()
                                    .map(element -> element.attr("abs:href"))
                                    .map(WebPageElement::newPageElement);
                        } else {
                            return document.select("img").stream()
                                    .map(element -> element.attr("abs:src"))
                                    .map(WebPageElement::newImageElement);
                        }
                    }).forEach(results::add);

            return results;
        }

        @Override
        public List<URL> getPageElementsAsUrls(Type... types) {
            List<URL> results = new ArrayList<>();

            if (isUndergraduate()) {
                getPageElementsAsStrings(types)
                        .forEach(url -> {
                            try {
                                results.add(new URL(url));
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            }
                        });
            } else {
                getPageElementsAsStrings(types)
                        .stream()
                        .map(ExceptionUtils.rethrowFunction(URL::new))
                        .forEach(results::add);
            }

            return results;
        }

        @Override
        public List<String> getPageElementsAsStrings(Type... types) {
            List<String> results = new ArrayList<>();

            if (isUndergraduate()) {
                getPageElements(types)
                        .forEach(element -> results.add(element.getUrl()));
            } else {
                getPageElements(types).stream()
                        .map(WebPageElement::getUrl)
                        .forEach(results::add);
            }

            return results;
        }

        private void __printSearchResultsStarting(Type type, String uri, Document doc) {
            if (!Controller.loggingEnabled()) {
                return;
            }

            System.out.println(">*********************************************");
            System.out.println("WebPageCrawler: getObjectAsString()");
            System.out.println("Searching PAGE: " + (uri == null ? "NULL" : uri));
            System.out.println("            baseURL: " + doc.baseUri());
            System.out.println("      Searching For: " + type.name());
        }

        private void __printSearchResults(List<String> results, Document doc) {
            if (!Controller.loggingEnabled()) {
                return;
            }

            System.out.println(
                    "       Result Count: " + (results == null ? "0" : results.size()));
            if (results != null && results.size() > 0) {
                System.out.print("            Results: ");
                for (int i = 0; i < results.size(); i++) {
                    if (i == 0) {
                        System.out.println(results.get(i));
                    } else {
                        System.out.println("                     " + results.get(i));
                    }
                }
            }
            System.out.println("<*********************************************");
        }
    }
}
