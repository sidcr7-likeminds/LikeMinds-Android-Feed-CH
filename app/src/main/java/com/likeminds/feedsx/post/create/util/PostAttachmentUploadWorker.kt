package com.likeminds.feedsx.post.create.util

import android.content.Context
import android.util.Log
import androidx.work.*
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.utils.mediauploader.MediaUploadWorker
import com.likeminds.feedsx.utils.mediauploader.model.AWSFileResponse
import com.likeminds.feedsx.utils.mediauploader.model.GenericFileRequest
import com.likeminds.feedsx.utils.mediauploader.model.IMAGE
import com.likeminds.feedsx.utils.mediauploader.utils.FileHelper
import com.likeminds.feedsx.utils.mediauploader.utils.UploadHelper
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation

class PostAttachmentUploadWorker(
    context: Context,
    workerParams: WorkerParameters
) : MediaUploadWorker(context, workerParams) {

    private val attachments by lazy { getStringParam(ARG_ATTACHMENTS) }
    private lateinit var attachmentsToUpload: List<SingleUriData>

    companion object {
        const val ARG_ATTACHMENTS = "ARG_ATTACHMENTS"

        fun getInstance(attachments: String): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<PostAttachmentUploadWorker>()
                .setInputData(
                    workDataOf(
                        ARG_ATTACHMENTS to attachments
                    )
                )
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
        }
    }

    override fun checkArgs() {
        require(ARG_ATTACHMENTS)
    }

    override fun init() {
        convertJsonStringToAttachments(attachments)
    }

    // creates list of SingleUriData from attachments JSON
    private fun convertJsonStringToAttachments(attachments: String) {
        val sType = object : TypeToken<List<SingleUriData>>() {}.type
        attachmentsToUpload = Gson().fromJson(attachments, sType)
    }

    // creates/resumes AWS uploads for each attachment
    override fun uploadFiles(continuation: Continuation<Int>) {
        val totalFilesToUpload = attachmentsToUpload.size

        uploadList = createAWSRequestList(attachmentsToUpload)
        uploadList.forEach { request ->
            val resumeAWSFileResponse =
                UploadHelper.getInstance().getAWSFileResponse(request.awsFolderPath)
            if (resumeAWSFileResponse != null) {
                resumeAWSUpload(resumeAWSFileResponse, totalFilesToUpload, continuation, request)
            } else {
                createAWSUpload(request, totalFilesToUpload, continuation)
            }
        }
    }

    // resumes AWS file upload
    private fun resumeAWSUpload(
        resumeAWSFileResponse: AWSFileResponse,
        totalFilesToUpload: Int,
        continuation: Continuation<Int>,
        request: GenericFileRequest
    ) {
        val resume = transferUtility.resume(resumeAWSFileResponse.transferObserver!!.id)
        if (resume == null) {
            createAWSUpload(request, totalFilesToUpload, continuation)
        } else {
            setTransferObserver(resumeAWSFileResponse, totalFilesToUpload, continuation)
        }
    }

    // creates and starts AWS upload
    private fun createAWSUpload(
        request: GenericFileRequest,
        totalFilesToUpload: Int,
        continuation: Continuation<Int>
    ) {
        val awsFileResponse =
            //TODO: uuid
            uploadFile(request, null)
        if (awsFileResponse != null) {
            UploadHelper.getInstance().addAWSFileResponse(awsFileResponse)
            setTransferObserver(awsFileResponse, totalFilesToUpload, continuation)
        }
    }

    /**
     * Starts Uploading file on AWS.
     * @param request A [GenericFileRequest] object
     * @return [AWSFileResponse] containing aws transfer utility objects and keys
     */
    private fun uploadFile(request: GenericFileRequest, uuid: String? = null): AWSFileResponse? {
        val filePath = request.localFilePath ?: return null
        val file = if (request.fileType == IMAGE) {
            FileHelper.compressFile(applicationContext, filePath)
        } else {
            File(filePath)
        }
        val observer = transferUtility.upload(
            request.awsFolderPath,
            file,
            CannedAccessControlList.PublicRead
        )
        return AWSFileResponse.Builder()
            .transferObserver(observer)
            .name(request.name ?: "")
            .awsFolderPath(request.awsFolderPath)
            .index(request.index)
            .fileType(request.fileType)
            .width(request.width)
            .height(request.height)
            .pageCount(request.pageCount)
            .size(request.size)
            .duration(request.duration)
            .uuid(uuid)
            .build()
    }

    // sets a transfer listener to uploading
    private fun setTransferObserver(
        awsFileResponse: AWSFileResponse,
        totalFilesToUpload: Int,
        continuation: Continuation<Int>
    ) {
        val observer = awsFileResponse.transferObserver!!
        setProgress(observer.id, observer.bytesTransferred, observer.bytesTotal)
        observer.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                onStateChanged(awsFileResponse, state, totalFilesToUpload, continuation)
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                Log.d("PUI", "onStateChanged: bytes $bytesCurrent")
                setProgress(id, bytesCurrent, bytesTotal)
            }

            override fun onError(id: Int, ex: Exception?) {
                ex?.printStackTrace()
                failedIndex.add(awsFileResponse.index)
                checkWorkerComplete(totalFilesToUpload, continuation)
            }
        })
    }

    // onStateChanged listener for AWS file upload
    private fun onStateChanged(
        response: AWSFileResponse,
        state: TransferState?,
        totalFilesToUpload: Int,
        continuation: Continuation<Int>
    ) {
        if (isStopped) {
            return
        }
        when (state) {
            TransferState.COMPLETED -> {
                UploadHelper.getInstance().removeAWSFileResponse(response)
                val downloadUri = response.downloadUrl
                //TODO : Uploading completed.
                Log.d(
                    "PUI", """
                    onStateChanged: uploaded $response
                    url: $downloadUri
                """.trimIndent()
                )
            }
            TransferState.FAILED -> {
                failedIndex.add(response.index)
                checkWorkerComplete(totalFilesToUpload, continuation)
            }
            else -> {

            }
        }
    }
}