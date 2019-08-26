package com.example.background.workers

import android.content.Context
import android.provider.MediaStore
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import timber.log.Timber
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ajay Deepak on 26-08-2019, 16:24
 */
class SaveImageWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    val TAG by lazy { SaveImageWorker::class.java.simpleName }
    val Title = "Blurred Image"
    val dateFormatter = SimpleDateFormat(
            "yyyy.MM.dd 'at' HH:mm:ss z",
            Locale.getDefault())

    override fun doWork(): Result {

        makeStatusNotification("Saving image to file", applicationContext)
        sleep()

        val resolver = applicationContext.contentResolver

        return try {
            val resourUri = inputData.getString(KEY_IMAGE_URI)

            // inserting image into permanent location
            val imageUrl = MediaStore.Images.Media.insertImage(resolver, resourUri, Title, dateFormatter.format(Date()))
            if (!imageUrl.isNullOrEmpty()) {
                val output = workDataOf(KEY_IMAGE_URI to imageUrl)
                Result.success(output)

            } else {

                Timber.e("Writing to MediaStore failed")
                Result.failure()
            }

        } catch (exception: Exception) {
            Result.failure()
        }
    }
}