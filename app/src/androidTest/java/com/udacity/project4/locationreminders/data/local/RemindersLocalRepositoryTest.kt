package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun getSingleReminder() = runBlocking {
        val item = ReminderDTO("Shopping", "Lets get groceries", "Walmart", 1.234, 5.678)

        repository.saveReminder(item)

        val result = repository.getReminder(item.id)

        assertThat(result is Result.Success, `is`(true))
        val resSuccess = result as Result.Success

        assertThat(resSuccess.data.title, `is`(item.title))
        assertThat(resSuccess.data.description, `is`(item.description))
        assertThat(resSuccess.data.latitude, `is`(item.latitude))
        assertThat(resSuccess.data.longitude, `is`(item.longitude))
    }

    @Test
    fun getMultipleReminders() = runBlocking {
        val item = ReminderDTO("Shopping", "Lets get groceries", "Walmart", 1.234, 5.678)
        val item2 = ReminderDTO("Gym", "Lets train", "Gold's", 2.454, 3.656)
        val item3 = ReminderDTO("School", "Start learning", "High School", 4.576, 5.475)

        repository.saveReminder(item)
        repository.saveReminder(item2)
        repository.saveReminder(item3)

        val list = repository.getReminders()
        list as Result.Success

        assertThat(list.data, `is`(CoreMatchers.notNullValue()))
        assertThat(list.data.size, `is`(3))
    }

    @Test
    fun saveAndDeleteReminders() = runBlocking {
        val item = ReminderDTO("Shopping", "Lets get groceries", "Walmart", 1.234, 5.678)
        val item2 = ReminderDTO("Gym", "Lets train", "Gold's", 2.454, 3.656)
        val item3 = ReminderDTO("School", "Start learning", "High School", 4.576, 5.475)

        repository.saveReminder(item)
        repository.saveReminder(item2)
        repository.saveReminder(item3)

        repository.deleteAllReminders()

        val list = repository.getReminders()
        list as Result.Success

        assertThat(list.data, `is`(emptyList()))
    }

}