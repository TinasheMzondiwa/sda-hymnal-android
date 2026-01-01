package hymnal.services.content.impl.fakes

import androidx.annotation.VisibleForTesting
import hymnal.storage.db.dao.BaseDao

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
abstract class FakeBaseDao<T> : BaseDao<T> {
    val items = mutableSetOf<T>()

    override suspend fun insert(item: T) {
        items.add(item)
    }

    override suspend fun insertAll(items: List<T>) {
        this.items.addAll(items)
    }

    override suspend fun update(item: T) {
        items.add(item)
    }

    override suspend fun delete(item: T) {
        items.remove(item)
    }
}
