// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.fakes.stubs

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.SupabaseSerializer
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.storage.Bucket
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.BucketBuilder
import io.github.jan.supabase.storage.Storage
import io.ktor.client.statement.HttpResponse

open class StubStorage : Storage{
    override suspend fun createBucket(
        id: String,
        builder: BucketBuilder.() -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun updateBucket(
        id: String,
        builder: BucketBuilder.() -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun retrieveBuckets(): List<Bucket> {
        TODO("Not yet implemented")
    }

    override suspend fun retrieveBucketById(bucketId: String): Bucket? {
        TODO("Not yet implemented")
    }

    override suspend fun emptyBucket(bucketId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBucket(bucketId: String) {
        TODO("Not yet implemented")
    }

    override fun get(bucketId: String): BucketApi {
        TODO("Not yet implemented")
    }

    override val apiVersion: Int
        get() = TODO("Not yet implemented")
    override val pluginKey: String
        get() = TODO("Not yet implemented")

    override suspend fun parseErrorResponse(response: HttpResponse): RestException {
        TODO("Not yet implemented")
    }

    override val config: Storage.Config
        get() = TODO("Not yet implemented")
    override val supabaseClient: SupabaseClient
        get() = TODO("Not yet implemented")
    override val serializer: SupabaseSerializer
        get() = TODO("Not yet implemented")
}