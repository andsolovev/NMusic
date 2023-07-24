package ru.netology.nmusic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmusic.api.MusicApi
import ru.netology.nmusic.api.MusicCallback
import ru.netology.nmusic.dto.Album

class TrackViewModel : ViewModel() {

    val _data = MutableLiveData<Album>()
    val data: LiveData<Album>
        get() = _data

    val nowPlaying = MutableLiveData<Int>()

    init {
        getAlbum()
        nowPlaying.value = 1
    }

    private fun getAlbum() {
        MusicApi.musicApiService.getAlbum(object : MusicCallback<Album> {
            override fun onSuccess(data: Album) {
                _data.postValue(data)
            }

            override fun onError(e: Exception) {
               throw Exception(e)
            }
        })
    }

    fun isPlaying(id: Int) {
        nowPlaying.value = id

        _data.value = data.value?.let { album ->
            album.copy(tracks = album.tracks.map { track ->
                if (id == track.id) {
                    track.copy(isPlaying = !track.isPlaying, isPaused = false, isSelected = true)
                } else {
                    track.copy(isPlaying = false, isPaused = false, isSelected = false)
                }
            })
        }
    }

    fun isPaused(id: Int) {
        _data.value = data.value?.let { album ->
            album.copy(tracks = album.tracks.map { track ->
                if (id == track.id) {
                    track.copy(isPlaying = !track.isPlaying, isPaused = !track.isPaused, isSelected = true)
                } else {
                    track.copy(isPlaying = false, isPaused = false, isSelected = false)
                }
            })
        }
    }

    fun isNotPlaying(): Boolean {
        return data.value?.tracks?.filter { track ->
            track.isPlaying
        }.isNullOrEmpty()
    }
}