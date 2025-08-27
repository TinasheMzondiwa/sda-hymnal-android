package hymnal.sing.components.text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import hymnal.services.prefs.model.ThemeStyle
import hymnal.sing.components.text.TextStyleScreen.Event as UiEvent
import hymnal.sing.components.text.TextStyleScreen.State as UiState

@Inject
class TextStylePresenter : Presenter<UiState> {
    @Composable
    override fun present(): UiState {
        var style by rememberRetained { mutableStateOf(ThemeStyle()) }

        return UiState(style) { event ->
            style = when (event) {
                is UiEvent.OnThemeChange -> {
                    style.copy(theme = event.theme)
                }

                is UiEvent.OnFontChange -> {
                    style.copy(font = event.font)
                }

                is UiEvent.OnTextSizeChange -> {
                    style.copy(textSize = event.textSize)
                }
            }

        }
    }

    @CircuitInject(TextStyleScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(): TextStylePresenter
    }
}