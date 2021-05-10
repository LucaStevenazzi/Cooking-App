package com.example.cooking_app.data

class Repository constructor(
    private val ricettaDao: RicettaDao
) {

    fun readData(): List<RicettaTab> {
        return ricettaDao.readData()
    }

    suspend fun insertData(ricettaTab: RicettaTab) {
        ricettaDao.insertData(ricettaTab)
    }

    fun searchDatabase(searchQuery: String): List<RicettaTab> {
        return ricettaDao.searchDatabase(searchQuery)
    }
}