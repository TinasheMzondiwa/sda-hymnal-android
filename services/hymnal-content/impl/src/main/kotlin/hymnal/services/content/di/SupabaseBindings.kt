package hymnal.services.content.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import hymnal.services.hymnalcontent.impl.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

@ContributesTo(AppScope::class)
@BindingContainer
object SupabaseBindings {

    @Provides
    @SingleIn(AppScope::class)
    fun provideSupabaseStorage(): Storage {
        val client = createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY,
        ) {
            install(Storage)
        }
        return client.storage
    }
}