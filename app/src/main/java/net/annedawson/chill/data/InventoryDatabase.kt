package net.annedawson.chill.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Item::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class) // Apply the type converter for the Date class
abstract class InventoryDatabase : RoomDatabase() {
    // [ ] is the array of tables.
    // Item::class references the Item class.
    // The class you define is abstract because
    // Room creates the implementation for you.
    abstract fun itemDao(): ItemDao  // Room creates the implementation for you
    // as long as you return a DAO interface (which ItemDao is).


    // a companion object is defined inside a class
    // members act like static members which can be accessed by
    // classname.membername
    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null

        // The value of a volatile variable is never cached,
        // and all reads and writes are to and from the main memory.
        // These features help ensure the value of Instance
        // is always up to date and is the same for all execution threads.
        // It means that changes made by one thread to Instance
        // are immediately visible to all other threads.

        fun getDatabase(context: Context): InventoryDatabase {
            //The Elvis operator ?: is used for null safety.
            // It returns the left-hand operand if it's not null,
            // and the right-hand operand otherwise, therefore
            // if the Instance is not null, Instance is returned,
            // otherwise create a new database instance.

            // Note 'this' refers to the current class instance.
            // which is the companion object itself
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                    .fallbackToDestructiveMigration() // This will delete the database on upgrade
                    // i.e. when the database version number above changes
                    .build()
                    .also { Instance = it }
            }
            // 'it' is the recently created database instance.
            // Instance is an instance of the InventoryDatabase class.
            // if Instance is not null, return it.
            // otherwise, create a new database instance, synchronized
            // so that only one thread is allowed in the block

            // Use Room's Room.databaseBuilder to
            // create your (item_database) database
            // only if it doesn't exist.
            // Otherwise, return the existing database.

            // Wrapping the code to get the database inside
            // a synchronized block means that
            // only one thread of execution at a time
            // can enter this block of code,
            // which makes sure the database
            // only gets initialized once
            // and there is only one instance of the database.
            // This pattern is the singleton pattern
        }
    }
}