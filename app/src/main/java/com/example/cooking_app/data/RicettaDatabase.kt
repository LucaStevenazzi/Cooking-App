package com.example.cooking_app.data

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(
    entities = [RicettaTab::class],
    version = 1,
    exportSchema = false
)
abstract class RicettaDatabase: RoomDatabase() {

    abstract fun RicettaDao(): RicettaDao

}