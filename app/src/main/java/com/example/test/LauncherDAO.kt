package com.example.test

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LauncherDao {

    @Insert
    suspend fun insert(launcher: Launcher)

    @Query("DELETE FROM launcher")
    suspend fun clearTable()

    @Query("SELECT * FROM launcher LIMIT 1")
    suspend fun getLauncher(): Launcher?
}
