package com.example.cooking_app.MainViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.cooking_app.data.Repository
import com.example.cooking_app.data.RicettaTab


class MainViewModel @ViewModelInject constructor(
        private val repository: Repository
) : ViewModel() {

        //val readData = repository.readData().asLiveData()

        fun insertData(ricettatab: RicettaTab){
             //   viewModelScope.launch(Dispatchers.IO) {
                repository.insertData(ricettatab)
         // }
        }

        fun searchDatabase(searchQuery: String): LiveData<List<RicettaTab>> {
                return repository.searchDatabase(searchQuery).asLiveData()
                }
        }
