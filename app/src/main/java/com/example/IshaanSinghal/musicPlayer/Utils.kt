package com.example.IshaanSinghal.musicPlayer

import java.util.concurrent.TimeUnit

fun timeFormat(millis :Long):String{
    return String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(millis),
        TimeUnit.MILLISECONDS.toSeconds(millis)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
    )
}