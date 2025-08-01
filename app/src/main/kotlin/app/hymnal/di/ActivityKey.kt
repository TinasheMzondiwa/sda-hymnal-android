package app.hymnal.di

import android.app.Activity
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

/** A [dev.zacsweers.metro.MapKey] annotation for binding Activities in a multibinding map. */
@MapKey
@Target(AnnotationTarget.CLASS)
annotation class ActivityKey(val value: KClass<out Activity>)