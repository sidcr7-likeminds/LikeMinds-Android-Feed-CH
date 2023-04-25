package com.likeminds.feedsample.utils.mediauploader.utils

import com.likeminds.feedsample.utils.mediauploader.model.AWSFileResponse


class UploadHelper {

    companion object {
        private var uploadHelperInstance: UploadHelper? = null

        @JvmStatic
        fun getInstance(): UploadHelper {
            if (uploadHelperInstance == null)
                uploadHelperInstance = UploadHelper()
            return uploadHelperInstance!!
        }
    }

    private val awsFileResponses: ArrayList<AWSFileResponse> = arrayListOf()

    fun addAWSFileResponse(awsFileResponse: AWSFileResponse) {
        awsFileResponses.add(awsFileResponse)
    }

    fun removeAWSFileResponse(awsFileResponse: AWSFileResponse) {
        awsFileResponses.remove(awsFileResponse)
    }

    fun getAWSFileResponse(awsFolderPath: String?): AWSFileResponse? {
        if (awsFolderPath == null)
            return null
        for (awsFileResponse in awsFileResponses) {
            if (awsFileResponse.awsFolderPath == awsFolderPath)
                return awsFileResponse
        }
        return null
    }
}