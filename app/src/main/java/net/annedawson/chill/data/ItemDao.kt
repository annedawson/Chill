package net.annedawson.chill.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item) // Suspend function to be called from a coroutine
                                   // in its own separate thread
    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * from items WHERE id = :id") //
    fun getItem(id: Int): Flow<Item>

    @Query("SELECT * from items ORDER BY LOWER(name) ASC")
    fun getAllItems(): Flow<List<Item>>
}

// The database operations can take a long time to execute,
// so they need to run on a separate thread.
// Room doesn't allow database access on the main thread.

