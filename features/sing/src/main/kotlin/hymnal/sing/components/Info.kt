package hymnal.sing.components

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
import hymnal.ui.theme.HymnalTheme

internal fun LazyListScope.hymnInfo(
    number: Int,
    title: String,
    majorKey: String?,
    author: String?,
) {
    item(key = number) {
        HymnInfo(number, title, author, majorKey, Modifier.animateItem())
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HymnInfo(
    number: Int,
    title: String,
    author: String?,
    majorKey: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Text(
            text = "$number",
            style = MaterialTheme.typography.headlineMediumEmphasized,
            textAlign = TextAlign.Center
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMediumEmphasized,
            textAlign = TextAlign.Center
        )

        author?.let {
            Text(
                text = "by $it",
                style = MaterialTheme.typography.bodySmallEmphasized,
                textAlign = TextAlign.Center
            )
        }

        majorKey?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMediumEmphasized,
                textAlign = TextAlign.Center
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            HymnInfo(
                number = 108,
                title = "Amazing Grace",
                author = "John Newton",
                majorKey = "C Major",
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}