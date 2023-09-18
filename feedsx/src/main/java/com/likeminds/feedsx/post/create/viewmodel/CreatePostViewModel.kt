package com.likeminds.feedsx.post.create.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.likeminds.feedsx.LMFeedAnalytics
import com.likeminds.feedsx.media.MediaRepository
import com.likeminds.feedsx.media.model.*
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.post.PostWithAttachmentsRepository
import com.likeminds.feedsx.post.create.util.PostAttachmentUploadWorker
import com.likeminds.feedsx.posttypes.model.LinkOGTagsViewData
import com.likeminds.feedsx.utils.LMFeedUserPreferences
import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.ViewDataConverter.convertAttachment
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.file.FileUtil
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingDecoder
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.post.model.AddPostRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class CreatePostViewModel @Inject constructor(
    private val userPreferences: LMFeedUserPreferences,
    private val postWithAttachmentsRepository: PostWithAttachmentsRepository,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    private val _postAdded = MutableLiveData<Boolean>()
    val postAdded: LiveData<Boolean> = _postAdded

    private var temporaryPostId: Long? = null

    sealed class ErrorMessageEvent {
        data class AddPost(val errorMessage: String?) : ErrorMessageEvent()
    }

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    fun fetchUriDetails(
        context: Context,
        uris: List<Uri>,
        callback: (media: List<MediaViewData>) -> Unit,
    ) {
        mediaRepository.getLocalUrisDetails(context, uris, callback)
    }

    // calls AddPost API and posts the response in LiveData
    fun addPost(
        context: Context,
        postTitle: String,
        postTextContent: String?,
        fileUris: List<SingleUriData>? = null,
        ogTags: LinkOGTagsViewData? = null
    ) {
        viewModelScope.launchIO {
            var updatedText = postTextContent?.trim()
            if (updatedText.isNullOrEmpty()) {
                updatedText = null
            }
            if (!fileUris.isNullOrEmpty()) {
                // if the post has upload-able attachments
                temporaryPostId = System.currentTimeMillis()
                val postId = temporaryPostId ?: 0
                val updatedFileUris = includeAttachmentMetaData(context, fileUris)
                val uploadData = startMediaUploadWorker(
                    context,
                    postId,
                    updatedFileUris.size
                )

                // adds post data in local db
                storePost(
                    uploadData,
                    postTitle,
                    updatedText,
                    updatedFileUris
                )
            } else {
                // if the post does not have any upload-able attachments
                val requestBuilder = AddPostRequest.Builder()
                    .heading(postTitle)
                    .text(updatedText)
                if (ogTags != null) {
                    // if the post has ogTags
                    requestBuilder.attachments(ViewDataConverter.convertAttachments(ogTags))
                }
                val request = requestBuilder.build()
                val response = lmFeedClient.addPost(request)
                if (response.success) {
                    // sends post creation completed event
                    sendPostCreationCompletedEvent(
                        updatedText,
                        ogTags
                    )
                    _postAdded.postValue(true)
                } else {
                    errorEventChannel.send(ErrorMessageEvent.AddPost(response.errorMessage))
                }
            }
        }
    }

    //add post:{} into local db
    private fun storePost(
        uploadData: Pair<WorkContinuation, String>,
        heading: String,
        text: String?,
        fileUris: List<SingleUriData>? = null
    ) {
        viewModelScope.launchIO {
            val uuid = uploadData.second
            if (fileUris.isNullOrEmpty()) {
                return@launchIO
            }
            val temporaryPostId = temporaryPostId ?: -1
            val thumbnailUri = fileUris.first().thumbnailUri
            val postEntity = ViewDataConverter.convertPost(
                temporaryPostId,
                uuid,
                thumbnailUri.toString(),
                text,
                heading
            )
            val attachments = fileUris.map {
                convertAttachment(
                    temporaryPostId,
                    it
                )
            }
            // add it to local db
            postWithAttachmentsRepository.insertPostWithAttachments(postEntity, attachments)
            _postAdded.postValue(false)
            uploadData.first.enqueue()
        }
    }

    /**
     * Includes attachment's meta data such as dimensions, thumbnails, etc
     * @param context
     * @param files List<SingleUriData>?
     */
    private fun includeAttachmentMetaData(
        context: Context,
        files: List<SingleUriData>,
    ): List<SingleUriData> {
        return files.map {
            // generates localFilePath from the ContentUri provided by client
            val localFilePath =
                FileUtil.getRealPath(context, it.uri)

            // generates awsFolderPath to upload the file
            val awsFolderPath = FileUtil.generateAWSFolderPathFromFileName(
                it.mediaName,
                userPreferences.getUUID()
            )
            val builder = it.toBuilder().localFilePath(localFilePath)
                .awsFolderPath(awsFolderPath)
            when (it.fileType) {
                IMAGE -> {
                    val dimensions = FileUtil.getImageDimensions(context, it.uri)
                    builder.width(dimensions.first)
                        .thumbnailUri(it.uri)
                        .height(dimensions.second)
                        .build()
                }

                VIDEO -> {
                    val thumbnailUri = FileUtil.getVideoThumbnailUri(context, it.uri)
                    if (thumbnailUri != null) {
                        val thumbnailAwsFolderPath = FileUtil.generateAWSFolderPathFromFileName(
                            "THUMB_${thumbnailUri.path}",
                            userPreferences.getUUID()
                        )
                        val thumbnailLocalFilePath = FileUtil.getRealPath(context, thumbnailUri)
                        builder.thumbnailUri(thumbnailUri)
                            .thumbnailLocalFilePath(thumbnailLocalFilePath)
                            .thumbnailAwsFolderPath(thumbnailAwsFolderPath)
                            .build()
                    } else {
                        builder.build()
                    }
                }

                else -> {
                    val thumbnailUri = MediaUtils.getDocumentPreview(context, it.uri)
                    if (thumbnailUri != null) {
                        val thumbnailAwsFolderPath = FileUtil.generateAWSFolderPathFromFileName(
                            "THUMB_${thumbnailUri.path}",
                            userPreferences.getUUID()
                        )
                        val thumbnailLocalFilePath = FileUtil.getRealPath(context, thumbnailUri)
                        val format = FileUtil.getFileExtensionFromFileName(it.mediaName)
                        builder
                            .thumbnailUri(thumbnailUri)
                            .thumbnailLocalFilePath(thumbnailLocalFilePath)
                            .thumbnailAwsFolderPath(thumbnailAwsFolderPath)
                            .format(format)
                            .build()
                    } else {
                        builder.build()
                    }
                }
            }
        }
    }

    // creates PostAttachmentUploadWorker to start media upload
    @SuppressLint("EnqueueWork")
    private fun startMediaUploadWorker(
        context: Context,
        postId: Long,
        filesCount: Int
    ): Pair<WorkContinuation, String> {
        val oneTimeWorkRequest = PostAttachmentUploadWorker.getInstance(postId, filesCount)
        val workContinuation = WorkManager.getInstance(context).beginWith(oneTimeWorkRequest)
        return Pair(workContinuation, oneTimeWorkRequest.id.toString())
    }

    /**
     * Triggers event when the user clicks on add attachment
     * @param type - type of attachment
     */
    fun sendClickedOnAttachmentEvent(type: String) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.CLICKED_ON_ATTACHMENT,
            mapOf(
                "type" to type
            )
        )
    }

    fun sendMediaAttachedEvent(data: ArrayList<SingleUriData>) {
        // counts number of images in attachments
        val imageCount = data.count {
            it.fileType == IMAGE
        }
        // counts number of videos in attachments
        val videoCount = data.count {
            it.fileType == VIDEO
        }
        // counts number of documents in attachments
        val docsCount = data.count {
            it.fileType == PDF
        }

        // sends image attached event if imageCount > 0
        if (imageCount > 0) {
            sendImageAttachedEvent(imageCount)
        }
        // sends image attached event if videoCount > 0
        if (videoCount > 0) {
            sendVideoAttachedEvent(videoCount)
        }
        // sends image attached event if docsCount > 0
        if (docsCount > 0) {
            sendDocumentAttachedEvent(docsCount)
        }
    }

    /**
     * Triggers when the user attaches image
     * @param imageCount - number of attached images
     **/
    private fun sendImageAttachedEvent(imageCount: Int) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.IMAGE_ATTACHED_TO_POST,
            mapOf(
                "image_count" to imageCount.toString()
            )
        )
    }

    /**
     * Triggers when the user attaches video
     * @param videoCount - number of attached videos
     **/
    private fun sendVideoAttachedEvent(videoCount: Int) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.VIDEO_ATTACHED_TO_POST,
            mapOf(
                "video_count" to videoCount.toString()
            )
        )
    }

    /**
     * Triggers when the user attaches document
     * @param documentCount - number of attached documents
     **/
    private fun sendDocumentAttachedEvent(documentCount: Int) {
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.DOCUMENT_ATTACHED_TO_POST,
            mapOf(
                "document_count" to documentCount.toString()
            )
        )
    }

    /**
     * Triggers when the user opens post is created successfully
     **/
    private fun sendPostCreationCompletedEvent(
        postText: String?,
        ogTags: LinkOGTagsViewData?
    ) {
        val map = hashMapOf<String, String>()
        val taggedUsers = MemberTaggingDecoder.decodeAndReturnAllTaggedMembers(postText)
        if (taggedUsers.isNotEmpty()) {
            map["user_tagged"] = "yes"
            map["tagged_users_count"] = taggedUsers.size.toString()
            val taggedUserIds =
                taggedUsers.joinToString {
                    it.first
                }
            map["tagged_users_uuid"] = taggedUserIds
        } else {
            map["user_tagged"] = "no"
        }
        if (ogTags != null) {
            map["link_attached"] = "yes"
            map["link"] = ogTags.url ?: ""
        } else {
            map["link_attached"] = "no"
        }
        map["image_attached"] = "no"
        map["video_attached"] = "no"
        map["document_attached"] = "no"
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.POST_CREATION_COMPLETED,
            map
        )
    }
}