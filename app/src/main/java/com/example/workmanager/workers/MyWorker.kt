package com.example.workmanager.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.workmanager.util.KEY_IMAGE_URI
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MyWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
): Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val appContext = applicationContext

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        // Show the notification on top of app
        createForegroundInfo("Image downloading", appContext)

        // Sleep the app so that the tasks emulate a long time process
        sleep()

        return try {
            if (TextUtils.isEmpty(resourceUri)) {
                throw IllegalArgumentException("Invalid input uri")
            }
            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)))

            val outputUri = writeBitmapToFile(picture, appContext)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
            Result.success(outputData)

        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            Result.failure()
        }
    }
}