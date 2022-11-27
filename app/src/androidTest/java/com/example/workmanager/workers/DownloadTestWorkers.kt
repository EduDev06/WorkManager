package com.example.workmanager.workers

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.workmanager.util.KEY_IMAGE_URI
import com.example.workmanager.util.TAG_OUTPUT
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DownloadTestWorkers  {
    private lateinit var targetContext: Context
    private lateinit var testContext: Context

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        testContext = InstrumentationRegistry.getInstrumentation().context
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(targetContext, config)
    }

    @Test
    fun test() {
        val inputDataUri = copyFileFromTestToTargetCtx(
            testContext,
            targetContext,
            "test_image.jpg"
        )

        val inputData = workDataOf(KEY_IMAGE_URI to inputDataUri.toString())

        // Create request
        val request = OneTimeWorkRequestBuilder<MyWorker>()
            .setInputData(inputData)
            .addTag(TAG_OUTPUT)
            .build()

        val workManager = WorkManager.getInstance(ApplicationProvider.getApplicationContext())

        workManager.enqueue(request).result.get()

        val workInfo = workManager.getWorkInfoById(request.id).get()
        val outputUri = workInfo?.outputData?.getString(KEY_IMAGE_URI)

        assertThat(workInfo.state).isEqualTo(WorkInfo.State.SUCCEEDED)
        assertThat(uriFileExists(targetContext, outputUri)).isTrue()
    }
}