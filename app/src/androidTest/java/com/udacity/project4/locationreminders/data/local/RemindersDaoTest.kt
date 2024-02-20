package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun getSingleReminder() = runBlockingTest {
        val item = ReminderDTO("Shopping", "Lets get groceries", "Walmart", 1.234, 5.678)

        database.reminderDao().saveReminder(item)

        val getitem = database.reminderDao().getReminderById(item.id)

        assertThat<ReminderDTO>(getitem as ReminderDTO, notNullValue())
        assertThat(getitem.id, `is`(item.id))
        assertThat(getitem.title, `is`(item.title))
        assertThat(getitem.description, `is`(item.description))
        assertThat(getitem.latitude, `is`(item.latitude))
        assertThat(getitem.longitude, `is`(item.longitude))
    }

    @Test
    fun getMultipleReminders() = runBlockingTest {
        val item = ReminderDTO("Shopping", "Lets get groceries", "Walmart", 1.234, 5.678)
        val item2 = ReminderDTO("Gym", "Lets train", "Gold's", 2.454, 3.656)
        val item3 = ReminderDTO("School", "Start learning", "High School", 4.576, 5.475)

        database.reminderDao().saveReminder(item)
        database.reminderDao().saveReminder(item2)
        database.reminderDao().saveReminder(item3)

        val list = database.reminderDao().getReminders()

        assertThat(list, `is`(notNullValue()))
        assertThat(list.size, `is`(3))
    }

    @Test
    fun saveAndDeleteReminders() = runBlockingTest {
        val item = ReminderDTO("Shopping", "Lets get groceries", "Walmart", 1.234, 5.678)
        val item2 = ReminderDTO("Gym", "Lets train", "Gold's", 2.454, 3.656)
        val item3 = ReminderDTO("School", "Start learning", "High School", 4.576, 5.475)

        database.reminderDao().saveReminder(item)
        database.reminderDao().saveReminder(item2)
        database.reminderDao().saveReminder(item3)

        database.reminderDao().deleteAllReminders()

        val list = database.reminderDao().getReminders()

        assertThat(list, `is`(emptyList()))
    }
}