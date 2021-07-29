package assignmentTests

import common.InstrumentedTests
import edu.vanderbilt.crawler.ui.screens.settings.Settings
import edu.vanderbilt.imagecrawler.crawlers.CrawlerType
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(InstrumentedTests::class)
class ParallelStreams1Tests {
    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            with (Settings) {
                reset()
                crawlStrategy = CrawlerType.PARALLEL_STREAMS1
            }
        }
    }
}
