package com.example.cooking_app.data

import androidx.constraintlayout.helper.widget.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RicettaDao {

    @Query("SELECT * FROM `tabella ricette` ORDER BY id ASC")
    fun readData(): List<RicettaTab>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(ricetta: RicettaTab)

    @Query("SELECT * FROM `tabella ricette` WHERE nome LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): List<RicettaTab>

}