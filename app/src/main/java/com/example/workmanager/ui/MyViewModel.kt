package com.example.workmanager.ui

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.example.workmanager.R
import com.example.workmanager.util.IMAGE_MANIPULATION_WORK_NAME
import com.example.workmanager.util.KEY_IMAGE_URI
import com.example.workmanager.util.TAG_OUTPUT
import com.example.workmanager.workers.MyWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    application: Application,
    private val workManager: WorkManager
): ViewModel() {

    val workInfo = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    private var imageUri: Uri? = null

    init {
        imageUri = getImageUri(application.applicationContext)
    }

    fun onEvent(e: MyEvent) {
        when (e) {
            MyEvent.DownloadImage -> saveImage()
            MyEvent.CancelWork -> cancelWork()
        }
    }

    private fun saveImage() {
        // set Constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresStorageNotLow(true)
            .setRequiresBatteryNotLow(true)
            .build()

        val imageWorker = OneTimeWorkRequestBuilder<MyWorker>()
            .setConstraints(constraints)
            .setInputData(createInputData())
            .addTag(TAG_OUTPUT)
            .build()

        workManager.enqueueUniqueWork(
            IMAGE_MANIPULATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            imageWorker
        )
    }

    private fun cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    private fun getImageUri(context: Context): Uri {
        val resources = context.resources

        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.lake))
            .appendPath(resources.getResourceTypeName(R.drawable.lake))
            .appendPath(resources.getResourceEntryName(R.drawable.lake))
            .build()
    }

    private fun createInputData(): Data {
        val builder = Data.Builder()
        imageUri?.let { builder.putString(KEY_IMAGE_URI, it.toString()) }
        return builder.build()
    }
}