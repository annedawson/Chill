package net.annedawson.chill

// To organise the imports, in Android Studio, use the menu Code -> Optimise imports
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.annedawson.chill.data.InventoryDatabase
import net.annedawson.chill.data.Item
import net.annedawson.chill.data.ItemDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Assert.assertTrue


@RunWith(AndroidJUnit4::class)
class ItemDaoTest {
    private lateinit var itemDao: ItemDao
    private lateinit var inventoryDatabase: InventoryDatabase
    private val item1 = Item(1, "Apples", "location 1", 20, 0L)
    private val item2 = Item(2, "Bananas", "location 1", 97,0L)

    private suspend fun addOneItemToDb() {
        itemDao.insert(item1)
    }

    private suspend fun addTwoItemsToDb() {
        itemDao.insert(item1)
        itemDao.insert(item2)
    }

    // @Before: This annotation indicates that
    // the createDb() function should be executed
    // before each test case.
    // It sets up the in-memory database
    // and the itemDao for testing.

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        inventoryDatabase = Room.inMemoryDatabaseBuilder(context, InventoryDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        itemDao = inventoryDatabase.itemDao()
    }

    // @After: This annotation indicates that
    // the closeDb() function should be executed
    // after each test case.
    // It closes the database to release resources.

    @After
    @Throws(IOException::class)
    fun closeDb() {
        inventoryDatabase.close()
    }

    // @Test: This annotation marks the
    // daoInsert_insertsItemIntoDB() function
    // as a test case. It verifies that
    // inserting an item using the DAO
    // correctly adds it to the database.

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        addOneItemToDb()
        val allItems = itemDao.getAllItems().first()
        assertEquals(allItems[0], item1)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        val allItems = itemDao.getAllItems().first()
        assertEquals(allItems[0], item1)
        assertEquals(allItems[1], item2)
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdateItems_updatesItemsInDB() = runBlocking {
        addTwoItemsToDb()
        itemDao.update(Item(1, "Apples", 15.0, 25))
        itemDao.update(Item(2, "Bananas", 5.0, 50))

        val allItems = itemDao.getAllItems().first()
        assertEquals(allItems[0], Item(1, "Apples", 15.0, 25))
        assertEquals(allItems[1], Item(2, "Bananas", 5.0, 50))
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteItems_deletesAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        itemDao.delete(item1)
        itemDao.delete(item2)
        val allItems = itemDao.getAllItems().first()
        assertTrue(allItems.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun daoGetItem_returnsItemFromDB() = runBlocking {
        addOneItemToDb()
        val item = itemDao.getItem(1)
        assertEquals(item.first(), item1)
    }

}