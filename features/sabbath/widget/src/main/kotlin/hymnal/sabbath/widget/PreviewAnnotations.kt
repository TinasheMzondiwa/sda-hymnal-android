/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hymnal.sabbath.widget

import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview

/**
 * Previews for 2x2 sized widgets in handheld and tablets.
 *
 * https://developer.android.com/design/ui/mobile/guides/widgets/sizing
 */
@OptIn(ExperimentalGlancePreviewApi::class)
// Reference devices
@Preview(widthDp = 172, heightDp = 234) // pixel 6a - smaller phone (2x2 in 4x5 grid - portrait)
@Preview(widthDp = 172, heightDp = 224) // pixel 7 pro - larger phone (2x2 in 4x5 grid - portrait)
@Preview(widthDp = 304, heightDp = 229) // Pixel tablet (2x2 in 6x5 grid - landscape)
// Min / max sizes
@Preview(widthDp = 109, heightDp = 115) // min handheld
@Preview(widthDp = 306, heightDp = 276) // max handheld
@Preview(widthDp = 180, heightDp = 184) // min tablet
@Preview(widthDp = 304, heightDp = 304) // max tablet
annotation class SmallWidgetPreview