package com.example.test

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskItemRepository(
    private val taskItemDao: TaskItemDao,
    private val encryptionKey: String // Ключ шифрования, передается при создании репозитория
) {

    // Получение всех элементов с расшифровкой данных
    val allTaskItems: Flow<List<TaskItem>> = taskItemDao.allTaskItems().map { items ->
        items.map { decryptTaskItem(it) }
    }

    @WorkerThread
    suspend fun insertTaskItem(taskItem: TaskItem) {
        // Шифруем данные перед записью
        taskItemDao.insertTaskItem(encryptTaskItem(taskItem))
    }

    @WorkerThread
    suspend fun updateTaskItem(taskItem: TaskItem) {
        // Шифруем данные перед обновлением
        taskItemDao.updateTaskItem(encryptTaskItem(taskItem))
    }

    @WorkerThread
    suspend fun deleteTaskItem(taskItem: TaskItem) {
        // Удаляем как есть
        taskItemDao.deleteTaskItem(taskItem)
    }

    @WorkerThread
    suspend fun clearTaskItem() {
        // Очищаем таблицу
        taskItemDao.clearTable()
    }

    // Шифрование TaskItem
    private fun encryptTaskItem(taskItem: TaskItem): TaskItem {
        return TaskItem(
            name = taskItem.name, // Не обязательно шифровать, если это публичные данные
            login = beltBlockEncrypt(stringToHex( taskItem.login), stringToHex( encryptionKey)),
            password = beltBlockEncrypt(stringToHex(taskItem.password), stringToHex(encryptionKey)),
            id = taskItem.id
        )
    }

    // Расшифровка TaskItem
    private fun decryptTaskItem(taskItem: TaskItem): TaskItem {
        return TaskItem(
            name = taskItem.name,
            login = hexToString( beltBlockDecrypt((taskItem.login), stringToHex(encryptionKey))),
            password = hexToString(beltBlockDecrypt((taskItem.password), stringToHex(encryptionKey))),
            id = taskItem.id
        )
    }
}
