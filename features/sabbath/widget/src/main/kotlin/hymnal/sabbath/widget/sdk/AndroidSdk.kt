// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.widget.sdk

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(parameter = 0)
fun isAtLeastApi(sdk: Int): Boolean = Build.VERSION.SDK_INT >= sdk

