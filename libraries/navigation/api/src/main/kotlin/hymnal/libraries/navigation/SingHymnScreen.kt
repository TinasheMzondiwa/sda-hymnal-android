package hymnal.libraries.navigation

import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class SingHymnScreen(val index: String) : Screen