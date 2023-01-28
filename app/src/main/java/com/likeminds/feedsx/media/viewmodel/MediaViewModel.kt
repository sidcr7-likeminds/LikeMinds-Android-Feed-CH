package com.likeminds.feedsx.media.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.feedsx.media.MediaRepository
import com.likeminds.feedsx.media.model.*
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.utils.coroutine.launchDefault
import com.likeminds.feedsx.utils.file.FileUtil
import com.likeminds.feedsx.utils.filterThenMap
import com.likeminds.feedsx.utils.model.BaseViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val localFolders by lazy { MutableLiveData<List<MediaFolderViewData>>() }

    private val localDocumentFiles by lazy { MutableLiveData<List<BaseViewType>>() }
    private val documentMediaList by lazy { ArrayList<MediaViewData>() }
    private val documentPreviewLiveData by lazy { MutableLiveData<List<SingleUriData>>() }
    val updatedUriDataList by lazy { MutableLiveData<List<SingleUriData>>() }

    private val getMediaBrowserViewData by lazy { MediaBrowserViewData.Builder().build() }

    fun getDocumentPreview(): LiveData<List<SingleUriData>> {
        return documentPreviewLiveData
    }

    /**
     * Fetches document preview asynchronously
     */
    fun fetchDocumentPreview(
        context: Context, uris: List<SingleUriData>,
    ) = viewModelScope.launchDefault {
        val updatedUris = uris.filter { singleUriData ->
            singleUriData.thumbnailUri == null
        }.mapNotNull { singleUriData ->
            val uri = MediaUtils.getDocumentPreview(context, singleUriData.uri)
            if (uri != null) {
                singleUriData.toBuilder().thumbnailUri(uri).build()
            } else {
                null
            }
        }
        documentPreviewLiveData.postValue(updatedUris)
    }

    fun fetchExternallySharedUriData(context: Context, uris: List<Uri>) =
        viewModelScope.launchDefault {
            val dataList = uris.mapNotNull { uri ->
                val singleUriData = mediaRepository.getExternallySharedUriDetail(context, uri)
                    ?: return@mapNotNull null
                return@mapNotNull when (singleUriData.fileType) {
                    IMAGE -> {
                        val newUri =
                            FileUtil.getSharedImageUri(context, uri) ?: return@mapNotNull null
                        singleUriData.toBuilder().uri(newUri).build()
                    }
                    VIDEO -> {
                        val newUri =
                            FileUtil.getSharedVideoUri(context, uri) ?: return@mapNotNull null
                        val thumbnailUri = FileUtil.getVideoThumbnailUri(context, uri)
                        singleUriData.toBuilder()
                            .uri(newUri)
                            .thumbnailUri(thumbnailUri)
                            .build()
                    }
                    PDF -> {
                        val newUri =
                            FileUtil.getSharedPdfUri(context, uri) ?: return@mapNotNull null
                        val thumbnailUri = MediaUtils.getDocumentPreview(context, uri)
                        singleUriData.toBuilder()
                            .uri(newUri)
                            .thumbnailUri(thumbnailUri)
                            .build()
                    }
                    else -> null
                }
            }
            updatedUriDataList.postValue(dataList)
        }

    fun fetchAllFolders(
        context: Context,
        mediaTypes: List<String>,
    ): LiveData<List<MediaFolderViewData>> {
        mediaRepository.getLocalFolders(context, mediaTypes, localFolders::postValue)
        return localFolders
    }

    fun fetchAllDocuments(context: Context): LiveData<List<BaseViewType>> {
        mediaRepository.getLocalDocumentFiles(context) { medias ->
            // Update documents list to be used for various purpose like sorting
            documentMediaList.clear()
            documentMediaList.addAll(medias)

            sortDocumentsByName()
        }
        return localDocumentFiles
    }

    fun fetchUriDetail(context: Context, uri: Uri, callback: (media: MediaViewData?) -> Unit) {
        mediaRepository.getLocalUriDetail(context, uri, callback)
    }

    fun fetchUriDetails(
        context: Context,
        uris: List<Uri>,
        callback: (media: List<MediaViewData>) -> Unit,
    ) {
        mediaRepository.getLocalUrisDetails(context, uris, callback)
    }

    fun sortDocumentsByName() {
        documentMediaList.sortBy { it.mediaName }
        postDocumentListForView(documentMediaList)
    }

    fun sortDocumentsByDate() {
        documentMediaList.sortByDescending { it.date }
        postDocumentListForView(documentMediaList)
    }

    fun filterDocumentsByKeyword(keyword: String) {
        val keywordList = keyword.split(" ")
        val updatedList = documentMediaList.filterThenMap({ media ->
            val matchedKeywords = keywordList.filter {
                media.mediaName?.contains(it) == true
            }
            Pair(matchedKeywords.isNotEmpty(), matchedKeywords)
        }, {
            it.first.toBuilder().filteredKeywords(it.second).build()
        })

        postDocumentListForView(updatedList)
    }

    fun clearDocumentFilter() {
        postDocumentListForView(documentMediaList)
    }

    private fun postDocumentListForView(updatedList: List<MediaViewData>) {
        val mediaList = ArrayList<BaseViewType>()
        mediaList.add(getMediaBrowserViewData)
        mediaList.addAll(updatedList)
        localDocumentFiles.postValue(mediaList)
    }
}