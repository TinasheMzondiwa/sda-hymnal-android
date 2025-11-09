// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.widget.data

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.storage.db.dao.SabbathTimesDao
import hymnal.storage.db.entity.SabbathTimesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import hymnal.libraries.l10n.R as L10nR

interface WidgetRepository {
    fun state(): Flow<WidgetState>
}

@ContributesBinding(AppScope::class)
@Inject
class WidgetRepositoryImpl(
    private val appContext: Context,
    private val sabbathTimesDao: SabbathTimesDao,
    private val dispatcherProvider: DispatcherProvider,
) : WidgetRepository {

    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    override fun state(): Flow<WidgetState> {
        return sabbathTimesDao.getFlow()
            .map { entity ->
                entity?.toWidgetState() ?: WidgetState.Error
            }
            .flowOn(dispatcherProvider.io)
    }

    private fun SabbathTimesEntity.toWidgetState(): WidgetState {
        val start = friday.asDateTime()
        val end = saturday.asDateTime()
        val dateNow = ZonedDateTime.now()

        if (start.isBefore(dateNow) && end.isBefore(dateNow)) {
            return WidgetState.Error
        }

        val isSabbath = isWithinSabbath(
            now = dateNow,
            start = start,
            end = end,
        )
        val (label, date) = if (isSabbath) {
            appContext.getString(L10nR.string.sabbath_end) to end
        } else {
            appContext.getString(L10nR.string.sabbath_start) to start
        }

        val dayLabel = if (!isSabbath && date.dayOfWeek != dateNow.dayOfWeek) {
            date.format(dateFormatter)
        } else if (isSabbath) {
            appContext.getString(L10nR.string.sabbath_notification_title)
        } else {
            null
        }

        val sabbathInfo = WidgetSabbathInfo(
            location = location,
            label = if (isSabbath) dayLabel.orEmpty() else label,
            dayLabel = if (isSabbath) label else dayLabel,
            time = date
                .toLocalDateTime()
                .format(timeFormatter)
                .uppercase(),
        )

        return WidgetState.Data(sabbathInfo = sabbathInfo)
    }

    private fun isWithinSabbath(
        now: ZonedDateTime,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Boolean = now.isAfter(start) && now.isBefore(end) // [start, end)

}

internal fun String.asDateTime(): ZonedDateTime =
    ZonedDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)