// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.components.info

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Composable
fun ResourceText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    linkColor: Color = MaterialTheme.colorScheme.primary,
    verseColor: Color = MaterialTheme.colorScheme.onSurface,
    fontSize: TextUnit = style.fontSize,
    fontFamily: FontFamily? = style.fontFamily,
    maxLines: Int = Int.MAX_VALUE,
    onCitationClick: (String) -> Unit
) {
    val combinedRegex = remember { Regex("(\\{([^}]+)\\})|(\\[(\\d+)\\])") }

    val annotatedString = remember(text) {
        buildAnnotatedString {
            var lastIndex = 0

            combinedRegex.findAll(text).forEach { matchResult ->
                // Append text BEFORE the match
                append(text.substring(lastIndex, matchResult.range.first))

                // Check which pattern matched
                val citationMatch = matchResult.groups[1] // The full {DA...}
                val verseMatch = matchResult.groups[3]    // The full [1]

                if (citationMatch != null) {
                    // --- Handle Citation ---
                    val fullMatch = citationMatch.value
                    // Group 2 is the inner text
                    val citationCode = matchResult.groups[2]?.value ?: ""

                    val link = LinkAnnotation.Clickable(
                        tag = citationCode,
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline
                            )
                        ),
                        linkInteractionListener = {
                            onCitationClick(citationCode)
                        }
                    )
                    pushLink(link)
                    append(fullMatch)
                    pop()

                } else if (verseMatch != null) {
                    // --- Handle Verse Number ---
                    // Group 4 is the number inside brackets
                    val verseNumber = matchResult.groups[4]?.value ?: ""

                    withStyle(
                        style = SpanStyle(
                            baselineShift = BaselineShift(0.3f),
                            fontSize = 0.75.em,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = verseColor
                        )
                    ) {
                        append(verseNumber) // Append ONLY the number, removing brackets
                    }
                }

                lastIndex = matchResult.range.last + 1
            }

            // Append any remaining text
            if (lastIndex < text.length) {
                append(text.substring(lastIndex))
            }
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        style = style,
        color = color,
        fontSize = fontSize,
        fontFamily = fontFamily,
        maxLines = maxLines,
        softWrap = true,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 30.sp
    )
}