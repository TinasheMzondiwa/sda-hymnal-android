// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.navigation.SabbathHymnsScreen
import hymnal.sabbath.Event
import hymnal.sabbath.components.SabbathInfoItem
import hymnal.sabbath.components.info.LocationInfoItem
import hymnal.sabbath.components.info.ReminderInfoItem
import hymnal.sabbath.components.info.ResourceItem
import hymnal.sabbath.components.info.SabbathCollectionItem
import hymnal.sabbath.components.info.SabbathInfoCard
import hymnal.services.prefs.HymnalPrefs
import hymnal.services.sabbath.api.SabbathInfo
import hymnal.services.sabbath.api.SabbathRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Stable
interface SabbathInfoStateProducer {

    @Composable
    operator fun invoke(navigator: Navigator, sabbathInfo: SabbathInfo): ImmutableList<SabbathInfoItem>
}

@ContributesBinding(AppScope::class)
@Inject
class SabbathInfoStateProducerImpl(
    private val countdownStateProducer: CountdownStateProducer,
    private val prefs: HymnalPrefs,
    private val sabbathRepository: SabbathRepository,
) : SabbathInfoStateProducer {

    private val formatter = DateTimeFormatter.ofPattern("EE, h:mm a")

    @Composable
    override fun invoke(navigator: Navigator, sabbathInfo: SabbathInfo): ImmutableList<SabbathInfoItem> {
        val coroutineScope = rememberStableCoroutineScope()
        val countDownTime by rememberCountDownTime(sabbathInfo)
        val sabbathProgress by rememberSabbathProgress(sabbathInfo, countDownTime)
        val sabbathRemindersEnabled by produceRetainedState(false) {
            prefs.sabbathRemindersEnabled().collect { value = it }
        }
        val resources by produceRetainedState(emptyList()) {
            sabbathRepository.getResources()
                .catch { Timber.e(it) }
                .collect { value = it }
        }

        val eventSink: (Event.SabbathInfo) -> Unit = rememberRetained {
            { event ->
                when (event) {
                    is Event.SabbathInfo.OnReminderToggled -> {
                        coroutineScope.launch {
                            prefs.setSabbathRemindersEnabled(event.enabled)
                        }
                    }

                    is Event.SabbathInfo.OnSabbathHymnsClicked -> navigator.goTo(SabbathHymnsScreen)
                }
            }
        }

        return buildList {
            add(LocationInfoItem(location = sabbathInfo.location))
            add(
                SabbathInfoCard(
                    isSabbath = sabbathInfo.isSabbath,
                    progress = sabbathProgress,
                    countDown = countDownTime,
                    sabbathStart = sabbathInfo.sabbathStart.format(formatter),
                    sabbathEnd = sabbathInfo.sabbathEnd.format(formatter),
                )
            )
            add(
                ReminderInfoItem(
                    enabled = sabbathRemindersEnabled,
                    eventSink = eventSink,
                )
            )

            add(SabbathCollectionItem(eventSink))

            addAll(resources.map { ResourceItem(it) })
        }.toImmutableList()
    }

    @Composable
    private fun rememberCountDownTime(sabbathInfo: SabbathInfo?) =
        produceRetainedState("", sabbathInfo) {
            sabbathInfo?.run {
                val flow = if (isSabbath) {
                    countdownStateProducer(sabbathEnd, true)
                } else {
                    countdownStateProducer(sabbathStart, false)
                }
                flow.catch { Timber.e(it) }
                    .collect { value = it }
            }
        }

    @Composable
    private fun rememberSabbathProgress(sabbathInfo: SabbathInfo?, key: Any) =
        produceRetainedState(0f, sabbathInfo, key2 = key) {
            sabbathInfo?.run {
                val progress = when {
                    isSabbath -> {
                        val now = ZonedDateTime.now()
                        val total =
                            Duration.between(sabbathStart, sabbathEnd).toMillis().coerceAtLeast(1)
                        val part = Duration.between(sabbathStart, now).toMillis().coerceAtLeast(0)
                        (part.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                    }

                    else -> 0f
                }
                value = progress
            }
        }
}