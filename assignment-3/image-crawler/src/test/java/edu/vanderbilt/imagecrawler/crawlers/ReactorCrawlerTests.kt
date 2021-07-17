package edu.vanderbilt.imagecrawler.crawlers

import admin.AssignmentTests
import admin.injectInto
import edu.vanderbilt.imagecrawler.transforms.Transform
import edu.vanderbilt.imagecrawler.utils.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import org.assertj.core.api.Assertions.assertThat
import reactor.core.publisher.Flux
import kotlin.collections.ArrayList
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import org.junit.Test
import java.net.URL
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ReactorCrawlerTests : AssignmentTests() {
    @SpyK
    var spyCrawler: ReactorCrawler =
        ReactorCrawler()

    @MockK
    lateinit var mockImageFlux: Flux<Image>

    @MockK
    lateinit var mockOptionalImageFlux: Flux<Optional<Image>>

    @MockK
    lateinit var mockOptionalImageMono: Mono<Optional<Image>>

    @MockK
    lateinit var mockImageMono: Mono<Image>

    @MockK
    lateinit var mockTransformFlux: Flux<Transform>

    @MockK
    lateinit var mockImage: Image

    @MockK
    lateinit var mockLongMono: Mono<Long>

    @MockK
    lateinit var mockTransforms: List<Transform>

    @MockK
    lateinit var mockStringArray: List<String>

    @MockK
    lateinit var mockCrawlerPageFlux: Flux<Crawler.Page>

    @MockK
    lateinit var mockUrlFlux: Flux<URL>

    @MockK
    lateinit var mockUrlMono: Mono<URL>

    @MockK
    lateinit var mockPage: Crawler.Page

    @MockK
    lateinit var mockStringFlux: Flux<String>

    @Test
    fun performCrawl() {
        every { mockImageFlux.count() } returns mockLongMono
        every { mockLongMono.block() } returns 1
        every { mockLongMono.onErrorReturn(any()) } returns mockLongMono
        every { spyCrawler.crawlPageAsync(any(), any()) } returns mockImageFlux

        // SUT
        spyCrawler.performCrawl("test", 3)

        verify { mockImageFlux.count() }
        verify { spyCrawler.crawlPageAsync(any(), 3) }
        verify { mockLongMono.block() }
        verify { mockLongMono.onErrorReturn(0L) }
    }

    @Test
    fun `crawlPageAsync uses the expected Project Reactor outer chained method calls`() {
        mockkStatic(Flux::class)
        val mockWebPageCrawler = mockk<WebPageCrawler>()
        mockWebPageCrawler.injectInto(spyCrawler)
        every { mockCrawlerPageFlux.flatMap<Image>(any()) } returns mockImageFlux
        every { mockStringFlux.map<Crawler.Page>(any()) } returns mockCrawlerPageFlux
        every { mockStringFlux.filter(any()) } returns mockStringFlux
        every { Flux.just<String>(any()) } returns mockStringFlux

        // SUT
        spyCrawler.crawlPageAsync("asdf", 3).toStream().forEach() {}

        verify { mockCrawlerPageFlux.flatMap<Image>(any()) }
        verify { mockStringFlux.filter(any()) }
        verify { Flux.just<String>(any()) }
        verify { mockStringFlux.map<Crawler.Page>(any()) }
    }

    @Test
    fun `crawlPageAsync uses the expected Project Reactor inner lambda calls`() {
        val mockWebPageCrawler = mockk<WebPageCrawler>()
        spyCrawler.mMaxDepth = Int.MAX_VALUE
        mockWebPageCrawler.injectInto(spyCrawler)
        val mockHashSet = mockk<ConcurrentHashSet<String>>()
        mockHashSet.injectInto(spyCrawler)
        every { mockHashSet.putIfAbsent(any()) } returns true
        every { mockWebPageCrawler.getPage(any()) } returns mockPage

        // SUT
        spyCrawler.crawlPageAsync("asdf", 3).toStream().forEach() {}

        verify { mockHashSet.putIfAbsent(any()) }
        verify { mockWebPageCrawler.getPage(any()) }
        verify { spyCrawler.imagesOnPageLinksAsync(mockPage, 3) }
    }

    @Test
    fun `imagesOnPageAndPageLinksAsync uses the expected Project Reactor outer chained method calls`() {
        every { mockImageFlux.mergeWith(mockImageFlux) } returns mockImageFlux
        every { spyCrawler.imagesOnPageLinksAsync(any(), any()) } returns mockImageFlux
        every { spyCrawler.imagesOnPageAsync(any()) } returns mockImageFlux
        every { mockImageFlux.subscribeOn(any()) } returns mockImageFlux

        // SUT
        assertNotNull(spyCrawler.imagesOnPageAndPageLinksAsync(mockPage, 0))

        verify { mockImageFlux.mergeWith(mockImageFlux) }
        verify { spyCrawler.imagesOnPageAsync(mockPage) }
        verify { mockImageFlux.subscribeOn(Schedulers.parallel()) }
        verify { spyCrawler.imagesOnPageLinksAsync(mockPage, 0) }
    }

    @Test
    fun `imagesOnPageAndPageLinksAsync uses the expected Project Reactor inner chained method calls`() {
        every { spyCrawler.imagesOnPageAsync(any()) } returns Flux.just(mockImage)
        every { spyCrawler.imagesOnPageLinksAsync(any(), any()) } returns Flux.just(mockImage)

        // SUT
        assertNotNull(spyCrawler.imagesOnPageAndPageLinksAsync(mockPage, 3))

        verify { spyCrawler.imagesOnPageAsync(mockPage) }
        verify { spyCrawler.imagesOnPageLinksAsync(mockPage, 3) }
    }

    @Test
    fun `imagesOnPageLinksAsync uses the expected Project Reactor outer chained method calls`() {
        mockkStatic(Flux::class)
        val spyImageFlux = spyk<Flux<Image>>()
        every { mockStringFlux.flatMap<Image>(any()) } returns spyImageFlux
        every { Flux.fromIterable<String>(any()) } returns mockStringFlux
        every { mockPage.getPageElementsAsStrings(Crawler.Type.PAGE) } returns mockStringArray

        // SUT
        assertNotNull(spyCrawler.imagesOnPageLinksAsync(mockPage, 3))

        verify(exactly = 1) { Flux.fromIterable<String>(any()) }
        verify(exactly = 1) { mockStringFlux.flatMap<Image>(any()) }
        verify(exactly = 1) { mockPage.getPageElementsAsStrings(Crawler.Type.PAGE) }
        verify(exactly = 0) { spyImageFlux.subscribeOn(any()) }
    }

    @Test
    fun `imagesOnPageLinksAsync uses the expected Project Reactor inner chained method calls`() {
        every {
            mockPage.getPageElementsAsStrings(any())
        } returns ArrayList(listOf("a", "b", "c"))

        val o = mockk<Flux<Image>>()

        every {
            o.subscribeOn(any())
        } returns Flux.just(mockImage, mockImage, mockImage)

        every { spyCrawler.crawlPageAsync(any(), any()) } returns o

        // SUT
        assertNotNull(spyCrawler.imagesOnPageLinksAsync(mockPage, 3).toStream().forEach() {})

        verify(exactly = 1) { mockPage.getPageElementsAsStrings(any()) }
        verify(exactly = 3) { spyCrawler.crawlPageAsync(any(), 4) }
        verify(exactly = 3) { o.subscribeOn(Schedulers.parallel()) }
    }

    @Test
    fun `imagesOnPageAsync uses the expected Project Reactor outer chained method calls`() {
        mockkStatic(Flux::class)
        every { mockImageFlux.flatMap<Image>(any()) } returns mockImageFlux
        every { mockUrlFlux.flatMap<Image>(any()) } returns mockImageFlux
        every { Flux.fromIterable<URL>(any()) } returns mockUrlFlux

        // SUT
        assertThat(spyCrawler.imagesOnPageAsync(mockPage)).isSameAs(mockImageFlux)

        verify { mockUrlFlux.flatMap<Image>(any()) }
        verify { mockImageFlux.flatMap<Image>(any()) }
        verify { Flux.fromIterable<URL>(any()) }
        verify(exactly = 0) { mockImageFlux.subscribeOn(any()) }
    }

    @Test
    fun `imagesOnPageAsync must filter nulls`() {
        mockkStatic(Optional::class)

        val url1 = URL("http://www.dummy.url-1")
        val url2 = URL("http://www.dummy.url-2")
        val url3 = URL("http://www.dummy.url-3")
        val urls = listOf(url1, url2, url3)
        every { mockPage.getPageElementsAsUrls(any()) } returns ArrayList(urls)
        every { spyCrawler.getOrDownloadImage(url1) } returns mockImage
        every { spyCrawler.getOrDownloadImage(url2) } returns null
        every { spyCrawler.getOrDownloadImage(url3) } returns mockImage
        every { spyCrawler.transformImageAsync(any()) } answers {
            Flux.just(it.invocation.args[0] as Image)
        }

        // SUT
        assertEquals(2, spyCrawler.imagesOnPageAsync(mockPage).toStream().count())

        verify(exactly = 3) { spyCrawler.getOrDownloadImage(any()) }
        verify(exactly = 2) { spyCrawler.transformImageAsync(any()) }
    }

    @Test
    fun `imagesOnPageAsync must correctly use the Optional class`() {
        mockkStatic(Optional::class)
        val spyOptional = spyk<Optional<Image>>()
        val spyNullOptional = spyk<Optional<Image>>()

        every { Optional.ofNullable<Image>(any()) } answers {
            if (invocation.args[0] == null) {
                spyNullOptional
            } else {
                spyOptional
            }
        }
        every { spyOptional.isPresent } returns true
        every { spyOptional.get() } returns mockImage
        every { spyNullOptional.isPresent } returns false

        val url1 = URL("http://www.dummy.url-1")
        val url2 = URL("http://www.dummy.url-2")
        val url3 = URL("http://www.dummy.url-3")
        val urls = listOf(url1, url2, url3)
        every { mockPage.getPageElementsAsUrls(any()) } returns ArrayList(urls)
        every { spyCrawler.getOrDownloadImage(url1) } returns mockImage
        every { spyCrawler.getOrDownloadImage(url2) } returns null
        every { spyCrawler.getOrDownloadImage(url3) } returns mockImage
        every { spyCrawler.transformImageAsync(any()) } answers {
            Flux.just(it.invocation.args[0] as Image)
        }

        // SUT
        assertEquals(2, spyCrawler.imagesOnPageAsync(mockPage).toStream().count())

        verify(exactly = 3) { spyCrawler.getOrDownloadImage(any()) }
        verify(exactly = 2) { spyCrawler.transformImageAsync(any()) }
        verify(exactly = 6) { Optional.ofNullable<Image>(any()) }
        verify(exactly = 2) { spyOptional.isPresent }
        verify(exactly = 4) { spyNullOptional.isPresent }
        verify(exactly = 2) { spyOptional.get() }
    }

    @Test
    fun `downloadImageAsync uses the expected Project Reactor chained method calls`() {
        mockkStatic(Mono::class)

        val url = URL("http://www.dummy.url-1")
        every { spyCrawler.getOrDownloadImage(any()) } throws IllegalStateException(
            "getOrDownloadImage() is called from within the wrong Project Reactor chain method."
        )

        every { Mono.fromCallable<URL>(any()) } answers { mockUrlMono }
        every { mockUrlMono.subscribeOn(any()) } answers { mockUrlMono }
        every { mockUrlMono.map<Optional<Image>>(any()) } answers { mockOptionalImageMono }
        every { mockOptionalImageMono.filter(any()) } answers { mockOptionalImageMono }
        every { mockOptionalImageMono.map<Image>(any()) } answers { mockImageMono }

        // SUT
        assertThat(spyCrawler.downloadImageAsync(url)).isSameAs(mockImageMono)

        verify(exactly = 1) { mockOptionalImageMono.filter(any()) }
        verify(exactly = 1) { mockUrlMono.map<Optional<Image>>(any()) }
        verify(exactly = 1) { Mono.fromCallable<URL>(any()) }
        verify(exactly = 1) { mockUrlMono.subscribeOn(any()) }
        verify(exactly = 1) { mockOptionalImageMono.map<Image>(any()) }
    }

    @Test
    fun `transformImageAsync uses the expected Project Reactor outer chained method calls`() {
        mockkStatic(Flux::class)
        every { Flux.fromIterable<Transform>(any()) } returns mockTransformFlux
        every { mockOptionalImageFlux.map<Image>(any()) } returns mockImageFlux
        every { mockTransformFlux.filter(any()) } returns mockTransformFlux
        every { mockTransformFlux.map<Optional<Image>>(any()) } returns mockOptionalImageFlux
        every { mockTransformFlux.subscribeOn(any()) } returns mockTransformFlux
        every { mockOptionalImageFlux.filter(any()) } returns mockOptionalImageFlux

        // SUT
        spyCrawler.mTransforms = mockTransforms
        spyCrawler.transformImageAsync(mockImage)

        verify { mockTransformFlux.subscribeOn(Schedulers.parallel()) }
        verify { Flux.fromIterable(spyCrawler.mTransforms) }
        verify { mockTransformFlux.filter(any()) }
        verify { mockTransformFlux.map<Optional<Image>>(any()) }
        verify { mockOptionalImageFlux.map<Image>(any()) }
        verify { mockOptionalImageFlux.filter(any()) }
    }

    @Test
    fun `transformImageAsync uses the expected Project Reactor inner chained method calls`() {
        mockkStatic(Optional::class)
        val mockTransform = mockk<Transform>()
        listOf(mockTransform, mockTransform, mockTransform).apply {
            injectInto(spyCrawler)
        }
        val total = spyCrawler.mTransforms.count()
        val expected = total - 2

        val mockOptional = mockk<Optional<Image>>()
        every { mockOptional.get() } returns mockImage
        every { Optional.ofNullable<Image>(any()) } returns mockOptional
        every { spyCrawler.createNewCacheItem(any(), any()) } returns true
        every { mockOptional.isPresent } returnsMany listOf(false, true, false)
        every { spyCrawler.applyTransform(any(), any()) } returnsMany listOf(null, mockImage, null)

        // SUT
        assertEquals(expected, spyCrawler.transformImageAsync(mockImage).toStream().count().toInt())

        verify(exactly = expected) { mockOptional.get() }
        verify(exactly = total) { spyCrawler.createNewCacheItem(any(), any()) }
        verify(exactly = total) { mockOptional.isPresent }
        verify(exactly = total) { spyCrawler.applyTransform(any(), any()) }
        verify(exactly = expected) { Optional.ofNullable<Image>(mockImage) }
    }
}
