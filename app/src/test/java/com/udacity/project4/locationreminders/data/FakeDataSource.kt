package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private var data: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {

    private var isErrorCase = false

    fun setErrorCase(value: Boolean) {
        isErrorCase = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (isErrorCase) {
            return Result.Error("Error while getting reminders")
        }
        return Result.Success(data)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        data.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (isErrorCase) {
            return Result.Error("Error while gettign reminders")
        }
        val item = data.find { it.id == id }
        item?.let { return Result.Success(item) }

        return Result.Error("reminder with id $id not found")
    }

    override suspend fun deleteAllReminders() {
        data.clear()
    }


}