// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.widget.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.services.sabbath.api.SabbathInfo
import hymnal.storage.db.dao.SabbathTimesDao
import hymnal.storage.db.entity.SabbathTimesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

interface WidgetRepository {
    fun state(): Flow<WidgetState>
}

@ContributesBinding(AppScope::class)
@Inject
class WidgetRepositoryImpl(
    private val sabbathTimesDao: SabbathTimesDao,
    private val dispatcherProvider: DispatcherProvider,
) : WidgetRepository {

    override fun state(): Flow<WidgetState> {
        return sabbathTimesDao.getFlow()
            .map { entity ->
                if (entity != null) {
                    WidgetState.Data(sabbathInfo = entity.toInfo())
                } else {
                    WidgetState.Error
                }
            }
            .flowOn(dispatcherProvider.io)
    }

    private fun SabbathTimesEntity.toInfo(): SabbathInfo {
        val start = friday.asDateTime()
        val end = saturday.asDateTime()

        return SabbathInfo(
            location = location,
            isSabbath = isWithinSabbath(
                now = ZonedDateTime.now(),
                start = start,
                end = end
            ),
            sabbathStart = start,
            sabbathEnd = end,
        )
    }

    private fun isWithinSabbath(
        now: ZonedDateTime,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Boolean = now.isAfter(start) && now.isBefore(end) // [start, end)

}

internal fun String.asDateTime(): ZonedDateTime =
    ZonedDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)