package ru.netology.nmusic.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.nmusic.BuildConfig
import ru.netology.nmusic.dto.Album
import java.io.IOException
import java.util.concurrent.TimeUnit

interface MusicCallback<T> {
    fun onSuccess(data: T)
    fun onError(e: java.lang.Exception)
}

class MusicApiService {
    private val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<Album>() {}

    companion object {
        private const val BASE_URL = BuildConfig.BASE_URL
    }

    fun getAlbum(musicCallback: MusicCallback<Album>) {
        val request: Request = Request.Builder().url("${BASE_URL}/album.json").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                musicCallback.onError(e)
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                if (!response.isSuccessful) musicCallback.onError(Exception(response.message))
                val body = requireNotNull(response.body.string()) { "body is null" }
                val album: Album = gson.fromJson(body, typeToken.type)
                musicCallback.onSuccess(album)
            }
        })
    }
}

object MusicApi {
    val musicApiService: MusicApiService by lazy {
        MusicApiService()
    }
}

