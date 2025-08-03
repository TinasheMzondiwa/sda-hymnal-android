
package hymnal.services.content.impl.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
internal enum class LyricType {
    @SerialName("verse")
    VERSE,
    @SerialName("refrain")
    REFRAIN,
    UNKNOWN // A sensible default or handle unknown types as errors
}

@OptIn(InternalSerializationApi::class)
@Serializable
internal data class RemoteHymn(
    val index: String,
    val number: Int,
    val title: String,
    val lyrics: List<LyricSection>
)

@OptIn(InternalSerializationApi::class)
@Serializable
internal data class LyricSection(
    val type: LyricType,
    val index: Int,
    val lines: List<String>
)