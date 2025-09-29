package com.example.gallerio

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallerio.data.ArtworkEntity
import com.example.gallerio.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ArtworkUiState(
    var artworks: List<ArtworkEntity> = emptyList(),
    var searchResults: List<ArtworkEntity> = emptyList(),
    var savedArtworks : List<ArtworkEntity> = emptyList()
)

class ArtworkViewModel(private val repository: ArtworkRepository) :ViewModel() {

    private var currentPage by mutableIntStateOf(1)

    var uiState by mutableStateOf(ArtworkUiState())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isSearchActive by mutableStateOf(false)

    var searchQuery by mutableStateOf("")

    init{
        fetchArtworks()
        loadSavedArtworks()
    }

    private fun loadSavedArtworks() {
        viewModelScope.launch(Dispatchers.IO) {
            val savedArtworks = repository.getSavedArtworks()
            withContext(Dispatchers.Main){
                uiState = uiState.copy(savedArtworks = savedArtworks)
            }
        }
    }

    fun fetchArtworks() {
        if (isSearchActive) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading = true

                val response = RetrofitClient.api.getArtworks(page = currentPage)

                val existingArtworks = repository.getAllArtworks().associateBy { it.id }

                val newArtworks = response.data.map { apiArtwork ->
                    val existing = existingArtworks[apiArtwork.id]
                    apiArtwork.toArtworkEntity().copy(isSaved = existing?.isSaved == true)
                }

                newArtworks.forEach { repository.insertArtwork(it) }

                val updatedArtworks = (uiState.artworks + newArtworks)
                    .distinctBy { it.id }

                withContext(Dispatchers.Main) {
                    uiState = uiState.copy(artworks = updatedArtworks)
                    currentPage++
                }

            } catch (e: Exception) {
                Log.d("Error", "$e occurred. Possibly offline. Loading local DB.")

                // If offline, load from DB
                val localData = repository.getAllArtworks()

                withContext(Dispatchers.Main) {
                    uiState = uiState.copy(artworks = localData)
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    fun updateQuery(newQuery: String) {
        searchQuery = newQuery
    }

    fun searchArtworks(query:String){
        isSearchActive = true
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            val searchArtworks = RetrofitClient.api.searchArtworks(query).data
            val result = searchArtworks.map {
                RetrofitClient.api.getArtworkById(it.id).data
            }

            withContext(Dispatchers.Main){
                uiState = uiState.copy(searchResults = result.map {
                    it.toArtworkEntity()
                })
                isLoading = false
                Log.d("Search","For $query results are $result")
            }
        }
    }


    fun saveArtwork(artwork: ArtworkEntity) {
        viewModelScope.launch {
            val updatedArtwork = artwork.copy(isSaved = true)
            repository.insertArtwork(updatedArtwork)

            uiState = uiState.copy(
                artworks = uiState.artworks.map {
                    if (it.id == updatedArtwork.id) updatedArtwork else it
                },
                savedArtworks = uiState.savedArtworks + updatedArtwork
            )
        }
    }

    fun deleteArtwork(artwork: ArtworkEntity) {
        viewModelScope.launch {
            val updatedArtwork = artwork.copy(isSaved = false)
            repository.deleteArtwork(updatedArtwork)

            uiState = uiState.copy(
                artworks = uiState.artworks.map {
                    if (it.id == updatedArtwork.id) updatedArtwork else it
                },
                savedArtworks = uiState.savedArtworks.filter {
                    it.id != updatedArtwork.id
                }
            )
        }
    }

    fun clearSearch(){
        searchQuery = ""
        isSearchActive = false
        uiState = uiState.copy(searchResults = emptyList())

    }

    fun updateSearchActive(value:Boolean){
        isSearchActive = value
    }

}
