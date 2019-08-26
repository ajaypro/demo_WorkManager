package com.example.background.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import timber.log.Timber
import java.io.File
import java.lang.Exception

/**
 * Created by Ajay Deepak on 26-08-2019, 16:22
 */
class CleanUpWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        makeStatusNotification("Cleanup temp files", applicationContext)
        sleep()

        return try {
            val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
            if (outputDir.exists()) {
                val entries = outputDir.listFiles()
                Timber.i("$entries")

                entries.forEach { entry ->
                    val name = entry.name
                    if (name.isNotEmpty() && name.endsWith(".png")) {
                        val deleted = entry.delete()
                        Timber.i("deleted $name - $deleted")
                    }
                }
            }
            Result.success()
        } catch (exception: Exception) {
            Timber.i(exception)
            Result.failure()
        }
    }
}