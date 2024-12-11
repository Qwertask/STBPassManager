package com.example.test

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "launcher")
data class Launcher(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "password_hash") val passwordHash: String? = null
){
    override fun toString(): String {
        return "DATA(id=$id, hash='$passwordHash')"
    }
}
