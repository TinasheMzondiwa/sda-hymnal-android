package hymnal.services.model

import androidx.compose.runtime.Immutable

@Immutable
data class HymnCategory(
    val id: String,
    val name: String,
    val start: Int,
    val end: Int,
)
