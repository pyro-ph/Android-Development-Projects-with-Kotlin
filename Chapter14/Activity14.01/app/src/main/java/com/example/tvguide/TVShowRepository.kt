package com.example.tvguide

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tvguide.api.TelevisionService
import com.example.tvguide.database.TVDao
import com.example.tvguide.database.TVDatabase
import com.example.tvguide.model.TVShow

class TVShowRepository(private val tvService: TelevisionService, private val tvDatabase: TVDatabase) {
    private val apiKey = "your_api_key_here"

    private val tvShowsLiveData: MutableLiveData<List<TVShow>> = MutableLiveData()

    val tvShows: LiveData<List<TVShow>>
        get() = tvShowsLiveData

    suspend fun fetchTVShows() {
        val tvDao: TVDao = tvDatabase.tvDao()
        var shows = tvDao.getTVShows()
        if (shows.isEmpty()) {
            try {
                val tvResponse = tvService.getTVShows(apiKey)
                shows = tvResponse.results
                tvDao.addTVShows(shows)
            } catch (exception: Exception) {
                Log.d("TVShowRepository", "Exception in fetchTVShows: ${exception.message}")
            }
        }

        tvShowsLiveData.postValue(shows)
    }

    suspend fun fetchTVShowsFromNetwork() {
        val tvDao: TVDao = tvDatabase.tvDao()
        var shows = tvDao.getTVShows()
        if (shows.isEmpty()) {
            try {
                val tvResponse = tvService.getTVShows(apiKey)
                shows = tvResponse.results
                tvDao.addTVShows(shows)
            } catch (exception: Exception) {
                Log.d("TVShowRepository", "Exception in fetchTVShowsFromNetwork: ${exception.message}")
            }
        }
    }
}