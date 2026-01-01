// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.fakes.stubs

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.BucketListFilter
import io.github.jan.supabase.storage.DownloadOptionBuilder
import io.github.jan.supabase.storage.FileObject
import io.github.jan.supabase.storage.FileObjectV2
import io.github.jan.supabase.storage.FileUploadResponse
import io.github.jan.supabase.storage.ImageTransformation
import io.github.jan.supabase.storage.SignedUrl
import io.github.jan.supabase.storage.UploadData
import io.github.jan.supabase.storage.UploadOptionBuilder
import io.github.jan.supabase.storage.UploadSignedUrl
import io.github.jan.supabase.storage.resumable.ResumableClient
import io.ktor.utils.io.ByteWriteChannel
import kotlin.time.Duration

open class StubBucketApi : BucketApi {
    override val bucketId: String get() = TODO()
    override val supabaseClient: SupabaseClient get() = TODO()
    override val resumable: ResumableClient get() = TODO()

    override suspend fun upload(
        path: String,
        data: UploadData,
        options: UploadOptionBuilder.() -> Unit
    ): FileUploadResponse = TODO()

    override suspend fun uploadToSignedUrl(
        path: String,
        token: String,
        data: UploadData,
        options: UploadOptionBuilder.() -> Unit
    ): FileUploadResponse = TODO()

    override suspend fun update(
        path: String,
        data: UploadData,
        options: UploadOptionBuilder.() -> Unit
    ): FileUploadResponse = TODO()

    override suspend fun delete(paths: Collection<String>): Unit = TODO()
    override suspend fun move(from: String, to: String, destinationBucket: String?): Unit = TODO()
    override suspend fun copy(from: String, to: String, destinationBucket: String?): Unit = TODO()
    override suspend fun createSignedUploadUrl(path: String, upsert: Boolean): UploadSignedUrl =
        TODO()

    override suspend fun createSignedUrl(
        path: String,
        expiresIn: Duration,
        transform: ImageTransformation.() -> Unit
    ): String = TODO()

    override suspend fun createSignedUrls(
        expiresIn: Duration,
        paths: Collection<String>
    ): List<SignedUrl> = TODO()

    override suspend fun downloadAuthenticated(
        path: String,
        options: DownloadOptionBuilder.() -> Unit
    ): ByteArray = TODO()

    override suspend fun downloadAuthenticated(
        path: String,
        channel: ByteWriteChannel,
        options: DownloadOptionBuilder.() -> Unit
    ): Unit = TODO()

    override suspend fun downloadPublic(
        path: String,
        options: DownloadOptionBuilder.() -> Unit
    ): ByteArray = TODO()

    override suspend fun downloadPublic(
        path: String,
        channel: ByteWriteChannel,
        options: DownloadOptionBuilder.() -> Unit
    ): Unit = TODO()

    override suspend fun list(
        prefix: String,
        filter: BucketListFilter.() -> Unit
    ): List<FileObject> = TODO()

    override suspend fun info(path: String): FileObjectV2 = TODO()
    override suspend fun exists(path: String): Boolean = TODO()
    override suspend fun changePublicStatusTo(public: Boolean): Unit = TODO()
    override fun publicUrl(path: String): String = TODO()
    override fun authenticatedUrl(path: String): String = TODO()
    override fun authenticatedRenderUrl(
        path: String,
        transform: ImageTransformation.() -> Unit
    ): String = TODO()

    override fun publicRenderUrl(path: String, transform: ImageTransformation.() -> Unit): String =
        TODO()
}