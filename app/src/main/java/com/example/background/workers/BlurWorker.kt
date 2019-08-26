package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import timber.log.Timber
import java.lang.Exception

/**
 * Created by Ajay Deepak on 24-08-2019, 21:04
 */

class BlurWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    //val appContext = applicationContext
    override fun doWork(): Result {

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Bluring image", applicationContext)

        return try {

            if(TextUtils.isEmpty(resourceUri)){
                Timber.e("Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = applicationContext.contentResolver

            val image = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)))

            val blurImage = blurBitmap(image, applicationContext)
            val outputFile = writeBitmapToFile(applicationContext, blurImage)

            val outputData = workDataOf(KEY_IMAGE_URI to outputFile.toString())

            makeStatusNotification("Output is $outputFile", applicationContext)
            Result.success(outputData)

        } catch (throwable: Throwable) {
            Timber.e(throwable)
            Result.failure()
        }
    }
}