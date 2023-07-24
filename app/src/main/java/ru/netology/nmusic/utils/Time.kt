package ru.netology.nmusic.utils

fun time(millis: Int): String {
    val min = millis / 60000 % 60
    val sec = millis / 1000 % 60
    return String.format("%02d:%02d", min, sec)
}