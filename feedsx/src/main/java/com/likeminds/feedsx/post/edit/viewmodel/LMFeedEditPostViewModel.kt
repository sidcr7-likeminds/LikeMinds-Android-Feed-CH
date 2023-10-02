package com.likeminds.feedsx.post.edit.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.likeminds.feedsx.LMFeedAnalytics
import com.likeminds.feedsx.media.MediaRepository
import com.likeminds.feedsx.media.model.MediaViewData
import com.likeminds.feedsx.media.model.SingleUriData
import com.likeminds.feedsx.post.PostWithAttachmentsRepository
import com.likeminds.feedsx.post.create.util.PostAttachmentUploadWorker
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.topic.model.LMFeedTopicViewData
import com.likeminds.feedsx.utils.*
import com.likeminds.feedsx.utils.coroutine.launchIO
import com.likeminds.feedsx.utils.file.FileUtil
import com.likeminds.feedsx.widgets.model.WidgetViewData
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.post.model.EditPostRequest
import com.likeminds.likemindsfeed.post.model.GetPostRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class LMFeedEditPostViewModel @Inject constructor(
    private val userPreferences: LMFeedUserPreferences,
    private val postWithAttachmentsRepository: PostWithAttachmentsRepository,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val lmFeedClient = LMFeedClient.getInstance()

    sealed class PostDataEvent {
        data class GetPost(val post: PostViewData) : PostDataEvent()

        data class EditPost(val post: PostViewData) : PostDataEvent()
    }

    private val postDataEventChannel = Channel<PostDataEvent>(Channel.BUFFERED)
    val postDataEventFlow = postDataEventChannel.receiveAsFlow()

    // Pair -> [uploadWorkerUUID, attachment]
    private val _uploadingData =
        MutableLiveData<Triple<String, AttachmentViewData, List<LMFeedTopicViewData>>>()
    val uploadingData: LiveData<Triple<String, AttachmentViewData, List<LMFeedTopicViewData>>> =
        _uploadingData

    sealed class ErrorMessageEvent {
        data class GetPost(val errorMessage: String?) : ErrorMessageEvent()
        data class EditPost(val errorMessage: String?) : ErrorMessageEvent()
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

    // to get the Post to be edited
    fun getPost(postId: String) {
        viewModelScope.launchIO {
            // builds api request
            val request = GetPostRequest.Builder()
                .postId(postId)
                .page(1)
                .pageSize(5)
                .build()

            // calls api
            val response = lmFeedClient.getPost(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val post = data.post
                val users = data.users
                val widgets = data.widgets
                val topics = data.topics
                postDataEventChannel.send(
                    PostDataEvent.GetPost(
                        ViewDataConverter.convertPost(
                            post,
                            users,
                            widgets,
                            topics
                        )
                    )
                )
            } else {
                errorEventChannel.send(ErrorMessageEvent.GetPost(response.errorMessage))
            }
        }
    }

    // starts worker to upload article image
    fun uploadArticleImage(
        context: Context,
        postTitle: String,
        updatedText: String,
        fileUri: SingleUriData?,
        topics: List<LMFeedTopicViewData>
    ) {
        if (fileUri == null) {
            return
        }
        val updatedFileUri = includeAttachmentMetaData(
            context,
            fileUri
        )

        val temporaryId = System.currentTimeMillis()

        val uploadData = startMediaUploadWorker(
            context,
            temporaryId
        )

        // adds post data in local db
        storePost(
            uploadData,
            temporaryId,
            postTitle,
            updatedText,
            updatedFileUri,
            topics
        )
    }

    /**
     * Includes attachment's meta data such as dimensions, etc
     * @param context
     * @param file SingleUriData?
     */
    private fun includeAttachmentMetaData(
        context: Context,
        fileUri: SingleUriData
    ): SingleUriData {
        // generates localFilePath from the ContentUri provided by client
        val localFilePath =
            FileUtil.getRealPath(context, fileUri.uri)

        // generates awsFolderPath to upload the file
        val awsFolderPath = FileUtil.generateAWSFolderPathFromFileName(
            fileUri.mediaName,
            userPreferences.getUUID()
        )
        val builder = fileUri.toBuilder().localFilePath(localFilePath)
            .awsFolderPath(awsFolderPath)

        val dimensions = FileUtil.getImageDimensions(context, fileUri.uri)
        return builder.width(dimensions.first)
            .thumbnailUri(fileUri.uri)
            .height(dimensions.second)
            .build()
    }

    //add post:{} into local db
    private fun storePost(
        uploadData: Pair<WorkContinuation, String>,
        temporaryId: Long,
        heading: String,
        text: String?,
        fileUri: SingleUriData,
        topics: List<LMFeedTopicViewData>
    ) {
        viewModelScope.launchIO {
            val uuid = uploadData.second
            // means that it is article post
            val postEntity = ViewDataConverter.convertPost(
                temporaryId,
                uuid,
                "",
                null,
                null,
                null
            )
            val attachments = listOf(
                ViewDataConverter.convertAttachmentForResource(
                    temporaryId,
                    fileUri,
                    text ?: "",
                    heading
                )
            )

            val topicEntities = topics.map {
                ViewDataConverter.convertTopic(temporaryId, it)
            }

            val attachmentsViewData = ViewDataConverter.convertAttachmentsEntity(attachments)

            // add it to local db
            postWithAttachmentsRepository.insertPostWithAttachments(
                postEntity,
                attachments,
                topicEntities
            )
            _uploadingData.postValue(Triple(uuid, attachmentsViewData.first(), topics))
            uploadData.first.enqueue()
        }
    }

    // creates PostAttachmentUploadWorker to start media upload
    @SuppressLint("EnqueueWork")
    private fun startMediaUploadWorker(
        context: Context,
        postId: Long
    ): Pair<WorkContinuation, String> {
        val oneTimeWorkRequest = PostAttachmentUploadWorker.getInstance(postId, 1)
        val workContinuation = WorkManager.getInstance(context).beginWith(oneTimeWorkRequest)
        return Pair(workContinuation, oneTimeWorkRequest.id.toString())
    }

    // calls EditPost API and posts the response in LiveData
    fun editPost(
        postId: String,
        postTitle: String?,
        postTextContent: String?,
        attachments: List<AttachmentViewData>? = null,
        ogTags: LinkOGTagsViewData? = null,
        widget: WidgetViewData? = null,
        selectedTopics: List<LMFeedTopicViewData>? = null
    ) {
        viewModelScope.launchIO {
            var updatedText = postTextContent?.trim()
            if (updatedText.isNullOrEmpty()) {
                updatedText = null
            }

            val topicIds = selectedTopics?.map {
                it.id
            }

            val request = when {
                widget != null -> {
                    // if the post has any file attachments
                    EditPostRequest.Builder()
                        .postId(postId)
                        .entityId(widget.id)
                        .topicIds(topicIds)
                        .attachments(
                            ViewDataConverter.createAttachmentsForWidget(
                                postTitle ?: "",
                                updatedText ?: "",
                                widget
                            )
                        )
                        .build()
                }

                attachments != null -> {
                    // if the post has any file attachments
                    EditPostRequest.Builder()
                        .postId(postId)
                        .text(updatedText)
                        .heading(postTitle)
                        .attachments(ViewDataConverter.createAttachments(attachments))
                        .topicIds(topicIds)
                        .build()
                }

                else -> {
                    // if the post does not have any file attachments
                    val requestBuilder = EditPostRequest.Builder()
                        .postId(postId)
                        .heading(postTitle)
                        .text(updatedText)
                        .topicIds(topicIds)
                    if (ogTags != null) {
                        // if the post has ogTags
                        requestBuilder.attachments(ViewDataConverter.convertAttachments(ogTags))
                    }
                    requestBuilder.build()
                }
            }

            // calls api
            val response = lmFeedClient.editPost(request)
            if (response.success) {
                val data = response.data ?: return@launchIO
                val post = data.post
                val users = data.users
                val widgets = data.widgets
                val topics = data.topics
                val postViewData = ViewDataConverter.convertPost(post, users, widgets, topics)
                postDataEventChannel.send(PostDataEvent.EditPost(postViewData))

                // sends post edited event
                sendPostEditedEvent(postViewData)
            } else {
                errorEventChannel.send(ErrorMessageEvent.EditPost(response.errorMessage))
            }
        }
    }

    /**
     * Triggers when the user edits a post
     **/
    private fun sendPostEditedEvent(
        post: PostViewData
    ) {
        val postType = ViewUtils.getPostTypeFromViewType(post.viewType)
        val postCreatorUUID = post.user.sdkClientInfoViewData.uuid
        LMFeedAnalytics.track(
            LMFeedAnalytics.Events.POST_EDITED,
            mapOf(
                "created_by_uuid" to postCreatorUUID,
                LMFeedAnalytics.Keys.POST_ID to post.id,
                "post_type" to postType,
            )
        )
    }
}