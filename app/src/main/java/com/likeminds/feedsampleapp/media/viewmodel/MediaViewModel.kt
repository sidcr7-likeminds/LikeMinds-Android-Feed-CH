package com.likeminds.feedsampleapp.media.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.likeminds.feedsampleapp.media.MediaRepository
import com.likeminds.feedsampleapp.media.model.MediaBrowserViewData
import com.likeminds.feedsampleapp.media.model.MediaFolderViewData
import com.likeminds.feedsampleapp.media.model.MediaHeaderViewData
import com.likeminds.feedsampleapp.media.model.MediaViewData
import com.likeminds.feedsampleapp.utils.ValueUtils.filterThenMap
import com.likeminds.feedsampleapp.utils.model.BaseViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val localFolders by lazy { MutableLiveData<List<MediaFolderViewData>>() }
    private val bucketMedias by lazy { MutableLiveData<List<BaseViewType>>() }

    private val localDocumentFiles by lazy { MutableLiveData<List<BaseViewType>>() }
    private val documentMediaList by lazy { ArrayList<MediaViewData>() }

    private val getMediaBrowserViewData by lazy { MediaBrowserViewData.Builder().build() }

    fun fetchAllFolders(
        context: Context,
        mediaTypes: List<String>,
    ): LiveData<List<MediaFolderViewData>> {
        mediaRepository.getLocalFolders(context, mediaTypes, localFolders::postValue)
        return localFolders
    }

    fun fetchMediaInBucket(
        context: Context,
        bucketId: String,
        mediaTypes: MutableList<String>,
    ): LiveData<List<BaseViewType>> {
        mediaRepository.getMediaInBucket(context, bucketId, mediaTypes) { medias ->
            val mediaList = ArrayList<BaseViewType>()
            var headerName = ""
            medias.forEach { media ->
                if (media.dateTimeStampHeader != headerName) {
                    mediaList.add(getMediaHeader(media.dateTimeStampHeader))
                    headerName = media.dateTimeStampHeader
                }
                mediaList.add(media)
            }
            bucketMedias.postValue(mediaList)
        }
        return bucketMedias
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

    private fun getMediaHeader(title: String): MediaHeaderViewData {
        return MediaHeaderViewData.Builder().title(title).build()
    }

    private fun postDocumentListForView(updatedList: List<MediaViewData>) {
        val mediaList = ArrayList<BaseViewType>()
        mediaList.add(getMediaBrowserViewData)
        mediaList.addAll(updatedList)
        localDocumentFiles.postValue(mediaList)
    }
}