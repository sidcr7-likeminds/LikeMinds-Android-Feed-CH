package com.likeminds.feedsx.utils.mediauploader

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkerParameters
import com.likeminds.feedsx.FeedSXApplication
import com.likeminds.feedsx.db.models.AttachmentEntity
import com.likeminds.feedsx.utils.getIntOrNull
import com.likeminds.feedsx.utils.getLongOrNull
import com.likeminds.feedsx.utils.mediauploader.model.GenericFileRequest
import com.likeminds.feedsx.utils.mediauploader.model.WORKER_FAILURE
import com.likeminds.feedsx.utils.mediauploader.model.WORKER_RETRY
import com.likeminds.feedsx.utils.mediauploader.model.WORKER_SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class MediaUploadWorker(
    appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    protected val transferUtility by lazy { (appContext.applicationContext as FeedSXApplication).transferUtility }
    protected val postRepository by lazy { (appContext.applicationContext as FeedSXApplication).postRepository }

    private val progressMap by lazy { HashMap<Int, Pair<Long, Long>>() }
    protected var uploadedCount = 0
    protected val failedIndex by lazy { ArrayList<Int>() }
    protected lateinit var uploadList: ArrayList<GenericFileRequest>

    abstract fun checkArgs()
    abstract fun init()
    abstract fun uploadFiles(continuation: Continuation<Int>)

    companion object {
        const val ARG_MEDIA_INDEX_LIST = "ARG_MEDIA_INDEX_LIST"
        const val ARG_PROGRESS = "ARG_PROGRESS"

        fun getProgress(workInfo: WorkInfo): Pair<Long, Long>? {
            val progress = workInfo.progress.getLongArray(ARG_PROGRESS)
            if (progress == null || progress.size != 2) {
                return null
            }
            return Pair(progress[0], progress[1])
        }
    }

    override suspend fun doWork(): Result {
        try {
            checkArgs()
            init()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
        return withContext(Dispatchers.IO) {
            val result = suspendCoroutine<Int> { continuation ->
                uploadFiles(continuation)
            }
            Log.d("PUI", "doWork: called $result")
            return@withContext when (result) {
                WORKER_SUCCESS -> {
                    Result.success()
                }
                WORKER_RETRY -> {
                    Result.retry()
                }
                WORKER_FAILURE -> {
                    getFailureResult(failedIndex.toIntArray())
                }
                else -> {
                    Log.d("TAG", "doWork: ff")
                    getFailureResult(failedIndex.toIntArray())
                }
            }
        }
    }

    private fun getFailureResult(failedArrayIndex: IntArray = IntArray(0)): Result {
        return Result.failure(
            Data.Builder()
                .putIntArray(ARG_MEDIA_INDEX_LIST, failedArrayIndex)
                .build()
        )
    }

    protected fun setProgress(id: Int, bytesCurrent: Long, bytesTotal: Long) {
        progressMap[id] = Pair(bytesCurrent, bytesTotal)
        var averageBytesCurrent = 0L
        var averageBytesTotal = 0L
        progressMap.values.forEach {
            averageBytesCurrent += it.first
            averageBytesTotal += it.second
        }
        if (averageBytesCurrent > 0L && averageBytesTotal > 0L) {
            setProgressAsync(
                Data.Builder()
                    .putLongArray(ARG_PROGRESS, longArrayOf(averageBytesCurrent, averageBytesTotal))
                    .build()
            )
        }
    }

    fun require(key: String) {
        if (!containsParam(key)) {
            throw Error("$key is required")
        }
    }

    protected fun getLongParam(key: String): Long {
        return params.inputData.getLongOrNull(key)
            ?: throw Error("$key is required")
    }

    protected fun getIntParam(key: String): Int {
        return params.inputData.getIntOrNull(key)
            ?: throw Error("$key is required")
    }

    private fun containsParam(key: String): Boolean {
        return params.inputData.keyValueMap.containsKey(key)
    }

    protected fun createAWSRequestList(
        attachmentsToUpload: List<AttachmentEntity>
    ): ArrayList<GenericFileRequest> {
        val awsFileRequestList = ArrayList<GenericFileRequest>()
        attachmentsToUpload.mapIndexed { index, attachment ->
            val attachmentMeta = attachment.attachmentMeta
            val request = GenericFileRequest.Builder()
                .name(attachmentMeta.name)
                .fileType(attachment.attachmentType)
                .awsFolderPath(attachmentMeta.awsFolderPath!!)
                .localFilePath(attachmentMeta.localFilePath)
                .index(index)
                .width(attachmentMeta.width)
                .height(attachmentMeta.height)
                .pageCount(attachmentMeta.pageCount)
                .duration(attachmentMeta.duration)
                .size(attachmentMeta.size)
                .build()
            awsFileRequestList.add(request)
        }
        return awsFileRequestList
    }

    protected fun checkWorkerComplete(
        totalFilesToUpload: Int,
        continuation: Continuation<Int>
    ) {
        if (totalFilesToUpload == uploadedCount + failedIndex.size) {
            if (totalFilesToUpload == uploadedCount) {
                Log.d("PUI", "success")
                continuation.resume(WORKER_SUCCESS)
            } else {
                Log.d("PUI", "failure")
                continuation.resume(WORKER_FAILURE)
            }
        }
    }
}