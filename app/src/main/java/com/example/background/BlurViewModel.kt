/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.background

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.work.*
import com.example.background.workers.BlurWorker
import com.example.background.workers.CleanUpWorker
import com.example.background.workers.SaveImageWorker


class BlurViewModel(application: Application) : AndroidViewModel(application) {

    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null

    val workManager = WorkManager.getInstance(application)

    // Building Data object to pass to bluractivity, Data object has image uri selected by user.
    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        imageUri?.let {
            builder.putString(KEY_IMAGE_URI, imageUri.toString())
        }
        return builder.build()

    }

    // enqueue work request to workmanager, since its one time request of blurring one image at one click
    internal fun applyBlur(blurLevel: Int) {

        // work request to blur image

        var continuation = workManager
                .beginWith(OneTimeWorkRequest
                        .from(CleanUpWorker::class.java))

        for (blur in 0 until blurLevel) {
            val blurRequest = OneTimeWorkRequestBuilder<BlurWorker>()
            if (blur == 0) {
                blurRequest.setInputData(createInputDataForUri())
            }

            continuation = continuation.then(blurRequest.build())
        }

        val saveImageRequest = OneTimeWorkRequest.Builder(SaveImageWorker::class.java).build()

        // Work continuation defines chain of work requests

        continuation.then(saveImageRequest)
                .enqueue()

    }

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    /**
     * Setters
     */
    internal fun setImageUri(uri: String?) {
        imageUri = uriOrNull(uri)
    }

    internal fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
    }
}
