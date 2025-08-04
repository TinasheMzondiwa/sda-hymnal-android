package hymnal.hymns

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.services.model.Hymn
import hymnal.services.model.HymnCategory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Stable
interface HymnsStateProducer {
    @Composable
    operator fun invoke(hymns: ImmutableList<Hymn>, category: HymnCategory?): ImmutableList<Hymn>
}

@ContributesBinding(AppScope::class)
@Inject
class HymnsStateProducerImpl : HymnsStateProducer {
    @Composable
    override fun invoke(hymns: ImmutableList<Hymn>, category: HymnCategory?): ImmutableList<Hymn> {
        return if (category == null) {
            hymns
        } else {
            hymns.filter { it.isInCategory(category) }.toImmutableList()
        }
    }

    private fun Hymn.isInCategory(category: HymnCategory): Boolean {
        return number in category.start..category.end
    }
}
