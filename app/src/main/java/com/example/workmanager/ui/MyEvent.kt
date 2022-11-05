package com.example.workmanager.ui

sealed class MyEvent {
    object DownloadImage: MyEvent()
    object CancelWork: MyEvent()
}