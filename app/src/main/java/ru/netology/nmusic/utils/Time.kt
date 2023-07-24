package ru.netology.nmusic.utils

fun time(milis: Int): String {
    val min = milis / 60000 % 60
    val sec = milis / 1000 % 60
    return String.format("%02d:%02d", min, sec)
}