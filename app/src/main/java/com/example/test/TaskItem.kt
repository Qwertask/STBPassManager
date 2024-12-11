package com.example.test

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_item_table")
class TaskItem(
    @ColumnInfo(name = "name")var name: String,
    @ColumnInfo(name= "login")var login: String,
    @ColumnInfo(name = "password")var password: String,
    @PrimaryKey(autoGenerate = true)var id: Int = 0
) {

    override fun toString(): String {
        return "DATA(id=$id, name='$name', login='$login', password='$password')"
    }
}