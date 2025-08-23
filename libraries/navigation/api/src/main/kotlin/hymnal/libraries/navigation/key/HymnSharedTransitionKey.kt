package hymnal.libraries.navigation.key

import com.slack.circuit.sharedelements.SharedTransitionKey

data class HymnSharedTransitionKey(val id: String, val type: ElementType) : SharedTransitionKey {
    enum class ElementType {
        Card,
        Number,
        Title,
        Lyrics,
    }
}