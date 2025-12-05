
package hymnal.services.content.impl.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val majorKey: String? = null,
    val lyrics: List<LyricSection>,
    val author: String? = null,
    @SerialName("author_link") val authorLink: String? = null,
)

@OptIn(InternalSerializationApi::class)
@Serializable
internal data class LyricSection(
    val type: LyricType,
    val index: Int,
    val lines: List<String>
)