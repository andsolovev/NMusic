package ru.netology.nmusic.dto

data class Track(
    val id: Int,
    val file: String,
    var isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val isSelected: Boolean = false,
)
