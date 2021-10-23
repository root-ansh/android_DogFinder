package work.curioustools.dogfinder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Rule

open class BaseTest {
    @get:Rule
    val executorRule = InstantTaskExecutorRule()


    @Before
    open fun beforeEachTest() {
        MockKAnnotations.init(this)
    }

}