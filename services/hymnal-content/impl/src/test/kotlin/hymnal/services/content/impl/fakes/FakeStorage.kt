// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.fakes

import hymnal.services.content.impl.fakes.stubs.StubStorage
import io.github.jan.supabase.storage.BucketApi

class FakeStorage : StubStorage() {
    override fun from(bucketId: String): BucketApi {
        return FakeBucketApi()
    }
}