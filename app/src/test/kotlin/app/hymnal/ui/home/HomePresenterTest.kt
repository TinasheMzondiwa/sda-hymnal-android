package app.hymnal.ui.home

import app.hymnal.ui.home.HomeScreen.Event
import com.google.common.truth.Truth.assertThat
import com.slack.circuit.foundation.NavEvent
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.test.runTest
import org.junit.Test

class HomePresenterTest {

    private val screen = HomeScreen()
    private val fakeNavigator = FakeNavigator(screen)
    private val fakePlayServicesChecker = FakePlayServicesChecker(true)

    private val presenter = HomePresenter(
        navigator = fakeNavigator,
        screen = screen,
        playServicesChecker = fakePlayServicesChecker,
    )

    @Test
    fun `present - shows default current route`() = runTest {
        presenter.test {
            val state = awaitItem()

            assertThat(state.currentRoute).isEqualTo(HomeRoute.Hymns)
        }
    }

    @Test
    fun `present - provides all available routes when play-services is available`() = runTest {
        presenter.test {
            val state = awaitItem()

            assertThat(state.routes).isEqualTo(HomeRoute.entries.toImmutableList())
        }
    }

    @Test
    fun `present - removes Sabbath route when play-services is not available`() = runTest {
        val presenter = HomePresenter(
            navigator = fakeNavigator,
            screen = screen,
            playServicesChecker = FakePlayServicesChecker(isAvailable = false),
        )
        presenter.test {
            val state = awaitItem()

            assertThat(state.routes).isNotEmpty()
            assertThat(state.routes).doesNotContain(HomeRoute.Sabbath)
        }
    }

    @Test
    fun `present - OnNav - navigates to route when clicked`() = runTest {
        presenter.test {
            var state = awaitItem()

            state.eventSink(Event.OnNav(HomeRoute.Collections))

            state = awaitItem()
            assertThat(state.currentRoute).isEqualTo(HomeRoute.Collections)
        }
    }

    @Test
    fun `present - OnNavEvent - sends NavEvent to navigator`() = runTest {
        presenter.test {
            val state = awaitItem()

            state.eventSink(Event.OnNavEvent(NavEvent.Pop()))

            fakeNavigator.awaitPop()
        }
    }
}

private class FakePlayServicesChecker(val isAvailable: Boolean) : PlayServicesChecker {
    override fun invoke(): Boolean = isAvailable
}