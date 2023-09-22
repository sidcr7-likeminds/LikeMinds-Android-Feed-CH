package com.likeminds.feedsx.post.create.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.databinding.LmFeedFragmentSelectAuthorBinding
import com.likeminds.feedsx.post.create.view.adapter.SelectAuthorAdapter
import com.likeminds.feedsx.post.create.view.adapter.SelectAuthorAdapterListener
import com.likeminds.feedsx.post.edit.viewmodel.HelperViewModel
import com.likeminds.feedsx.posttypes.model.SDKClientInfoViewData
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.search.util.CustomSearchBar
import com.likeminds.feedsx.utils.EndlessRecyclerScrollListener
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import javax.inject.Inject

class LMFeedSelectAuthorFragment :
    BaseFragment<LmFeedFragmentSelectAuthorBinding, Nothing>(),
    SelectAuthorAdapterListener {

    @Inject
    lateinit var helperViewModel: HelperViewModel

    private lateinit var mAdapter: SelectAuthorAdapter
    private lateinit var scrollListener: EndlessRecyclerScrollListener

    private var searchKeyword: String? = null

    companion object {
        const val ARG_SELECT_AUTHOR_RESULT = "select_author_result"
    }

    override fun getViewModelClass(): Class<Nothing>? {
        return null
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().createPostComponent()?.inject(this)
    }

    override fun getViewBinding(): LmFeedFragmentSelectAuthorBinding {
        return LmFeedFragmentSelectAuthorBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        getInitialData()
        initRecyclerView()
        initializeSearchView()
        initClickListeners()
    }

    override fun observeData() {
        super.observeData()

        helperViewModel.taggingData.observe(viewLifecycleOwner) {
            val userTagViewData = it?.second
            val userViewData = userTagViewData?.map { user ->
                UserViewData.Builder()
                    .name(user.name)
                    .imageUrl(user.imageUrl)
                    .id(user.id)
                    .isGuest(user.isGuest)
                    .userUniqueId(user.userUniqueId)
                    .sdkClientInfoViewData(SDKClientInfoViewData.Builder().uuid(user.uuid).build())
                    .build()
            }
            mAdapter.addAll(userViewData)
        }
    }

    private fun getInitialData() {
        helperViewModel.getMembersForTagging(
            1,
            ""
        )
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        mAdapter = SelectAuthorAdapter(this)
        binding.rvUsers.apply {
            layoutManager = linearLayoutManager
            adapter = mAdapter
            show()

            //create scroll listener
            scrollListener = object : EndlessRecyclerScrollListener(linearLayoutManager) {
                override fun onLoadMore(currentPage: Int) {
                    if (currentPage > 0) {
                        helperViewModel.getMembersForTagging(
                            currentPage,
                            searchKeyword ?: ""
                        )
                    }
                }
            }
        }
    }

    private fun initializeSearchView() {
        binding.searchBar.apply {
            this.initialize(lifecycleScope)

            setSearchViewListener(object :
                CustomSearchBar.SearchViewListener {
                override fun onSearchViewClosed() {
                    hide()
                    clearParticipants()
                }

                override fun crossClicked() {
                    clearParticipants()
                }

                override fun keywordEntered(keyword: String) {
                    clearParticipants(keyword)
                }
            })
            observeSearchView(true)
        }
    }

    // initializes click listeners
    private fun initClickListeners() {
        binding.apply {
            ivSearch.setOnClickListener {
                searchBar.visibility = View.VISIBLE
                searchBar.post {
                    searchBar.openSearch()
                }
            }

            ivBack.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun clearParticipants(keyword: String? = null) {
        scrollListener.resetData()
        mAdapter.clearAndNotify()
        searchKeyword = keyword
        helperViewModel.getMembersForTagging(
            1,
            searchKeyword ?: ""
        )
    }

    override fun onUserSelected(userViewData: UserViewData?) {
        val intent = Intent().apply {
            putExtras(Bundle().apply {
                putParcelable(
                    ARG_SELECT_AUTHOR_RESULT, userViewData
                )
            })
        }
        requireActivity().setResult(Activity.RESULT_OK, intent)
        requireActivity().finish()
    }
}