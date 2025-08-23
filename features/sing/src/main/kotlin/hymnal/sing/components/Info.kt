package hymnal.sing.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import hymnal.libraries.navigation.key.HymnSharedTransitionKey
import hymnal.ui.theme.HymnalTheme
import kotlinx.collections.immutable.persistentListOf

internal fun LazyListScope.hymnInfo(
    hymn: HymnContent
) {
    item(key = hymn.index) {
        HymnInfo(hymn, Modifier.animateItem())
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun HymnInfo(
    hymn: HymnContent,
    modifier: Modifier = Modifier,
) {
    SharedElementTransitionScope {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = "${hymn.number}",
                modifier = Modifier.sharedBounds(
                    sharedContentState =
                        rememberSharedContentState(
                            HymnSharedTransitionKey(
                                id = hymn.index,
                                type = HymnSharedTransitionKey.ElementType.Number,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                ),
                style = MaterialTheme.typography.headlineMediumEmphasized,
                textAlign = TextAlign.Center
            )
            Text(
                text = hymn.title,
                modifier = Modifier.sharedBounds(
                    sharedContentState =
                        rememberSharedContentState(
                            HymnSharedTransitionKey(
                                id = hymn.index,
                                type = HymnSharedTransitionKey.ElementType.Title,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                ),
                style = MaterialTheme.typography.headlineMediumEmphasized,
                textAlign = TextAlign.Center
            )

             hymn.author?.let {
                Text(
                    text = "by $it",
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    textAlign = TextAlign.Center
                )
            }

            hymn.majorKey?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMediumEmphasized,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            HymnInfo(
                hymn = HymnContent(
                    index = "108",
                number = 108,
                title = "Amazing Grace",
                majorKey = "C Major",
                    lyrics = persistentListOf(),
                ),
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}