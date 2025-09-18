
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
    UNKNOWN,
}

@OptIn(InternalSerializationApi::class)
@Serializable
internal data class RemoteHymn(
    val index: String,
    val number: Int,
    val title: String,
    val majorKey: String?,
    val author: String?,
    val lyrics: List<LyricSection>
)

@OptIn(InternalSerializationApi::class)
@Serializable
internal data class LyricSection(
    val type: LyricType,
    val index: Int,
    val lines: List<String>
)