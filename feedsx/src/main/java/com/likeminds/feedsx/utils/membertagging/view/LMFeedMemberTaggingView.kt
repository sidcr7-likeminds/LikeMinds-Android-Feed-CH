package com.likeminds.feedsx.utils.membertagging.view

import android.content.Context
import android.text.Editable
import android.text.SpannableString
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.LmFeedLayoutMemberTaggingBinding
import com.likeminds.feedsx.utils.EndlessRecyclerScrollListener
import com.likeminds.feedsx.utils.membertagging.model.MemberTaggingExtras
import com.likeminds.feedsx.utils.membertagging.model.UserTagViewData
import com.likeminds.feedsx.utils.membertagging.util.*
import com.likeminds.feedsx.utils.membertagging.view.adapter.MemberAdapter
import com.likeminds.feedsx.utils.membertagging.view.adapter.MemberAdapterClickListener

/**
 * This is a constraint layout, so it cannot be considered as a popup window or a dialog, which will
 * always have the highest z index and will show on top of any other views.
 * Always put this view just inside the top most parent to avoid rearranging and pushing of views
 */
class LMFeedMemberTaggingView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), TextWatcherListener, MemberAdapterClickListener {

    private var binding = LmFeedLayoutMemberTaggingBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    var taggingEnabled = true
        set(value) {
            field = value
            enableTagging(value)
        }

    private lateinit var mAdapter: MemberAdapter
    private lateinit var memberTaggingTextWatcher: MemberTaggingTextWatcher
    private var memberTaggingViewListener: MemberTaggingViewListener? = null
    private lateinit var scrollListener: EndlessRecyclerScrollListener

    //Community Members and groups to search
    private val communityMembersAndGroups = mutableListOf<UserTagViewData>()

    //contains searched text
    private var searchText: String = ""

    //Contains selected members
    private val selectedMembers by lazy { mutableListOf<UserTagViewData>() }

    private lateinit var extras: MemberTaggingExtras

    val isShowing: Boolean
        get() {
            return mAdapter.itemCount > 0
        }

    /**
     * Initialises member tagging view with the required attribute and start listening for member tagging
     * @param extras [MemberTaggingExtras] Contains configurable fields
     */
    fun initialize(extras: MemberTaggingExtras) {
        this.extras = extras
        initializeRecyclerView()
        initializeTextWatcher()
        configureView()
    }

    private fun configureView() {
        //Set max height
        val heightInPx = MemberTaggingUtil.getMaxHeight(context, extras.maxHeightInPercentage)
        maxHeight = heightInPx
        val lp = binding.recyclerView.layoutParams as LayoutParams
        lp.matchConstraintMaxHeight = heightInPx
        binding.recyclerView.layoutParams = lp

        //Set the theme
        if (extras.darkMode) {
            binding.constraintLayout.setBackgroundResource(R.color.black_80)
        } else {
            binding.constraintLayout.setBackgroundResource(R.drawable.background_container)
        }
    }

    fun reSetMaxHeight(px: Int) {
        if (px >= maxHeight || px <= 0) {
            return
        }
        maxHeight = px
        val lp = binding.recyclerView.layoutParams as LayoutParams
        lp.matchConstraintMaxHeight = px
        binding.recyclerView.layoutParams = lp
    }

    fun addListener(memberTaggingViewListener: MemberTaggingViewListener) {
        this.memberTaggingViewListener = memberTaggingViewListener
    }

    private fun enableTagging(value: Boolean) {
        if (value) {
            memberTaggingTextWatcher.startObserving()
        } else {
            memberTaggingTextWatcher.stopObserving()
        }
    }

    private fun initializeTextWatcher() {
        memberTaggingTextWatcher = MemberTaggingTextWatcher(taggingEnabled, extras.editText)
        memberTaggingTextWatcher.addTextWatcherListener(this)
        memberTaggingTextWatcher.startObserving()
    }

    private fun initializeRecyclerView() {
        //create adapter
        mAdapter = MemberAdapter(extras.darkMode, this)

        //create layout manager
        val linearLayoutManager = LinearLayoutManager(this.context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        scrollListener = object : EndlessRecyclerScrollListener(linearLayoutManager) {
            override fun onLoadMore(currentPage: Int) {
                if (currentPage > 0) {
                    memberTaggingViewListener?.callApi(currentPage, searchText)
                }
            }
        }
        scrollListener.resetData()

        binding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = linearLayoutManager
            addOnScrollListener(scrollListener)
        }
    }

    fun hide() {
        if (isShowing) {
            mAdapter.clear()
            memberTaggingViewListener?.onHide()
        }
    }

    private fun getMemberFromSelectedList(userUniqueId: String): UserTagViewData? {
        return selectedMembers.firstOrNull { member ->
            member.userUniqueId == userUniqueId
        }
    }

    private fun getMember(userUniqueId: String): UserTagViewData? {
        return communityMembersAndGroups.firstOrNull { member ->
            member.userUniqueId == userUniqueId
        }
    }

    private fun showMemberTaggingList() {
        if (communityMembersAndGroups.isNotEmpty()) {
            memberTaggingViewListener?.onShow()
            val lastItem = communityMembersAndGroups.lastOrNull()
            mAdapter.setMembers(communityMembersAndGroups.map {
                if (it.userUniqueId == lastItem?.userUniqueId) {
                    //if last item hide bottom line in item view
                    it.toBuilder().isLastItem(true).build()
                } else {
                    it
                }
            })
        } else {
            hide()
        }
    }

    override fun onHitTaggingApi(text: String) {
        searchText = text.substring(1) //omit '@'
        scrollListener.resetData()
        memberTaggingViewListener?.callApi(1, searchText)
    }

    override fun onMemberRemoved(regex: String) {
        val memberRoute = MemberTaggingDecoder.getRouteFromRegex(regex) ?: return
        val userUniqueId = memberRoute.lastPathSegment ?: return
        val member = getMemberFromSelectedList(userUniqueId)
        if (member != null) {
            selectedMembers.remove(member)
            memberTaggingViewListener?.onMemberRemoved(member)
        }
    }

    override fun dismissMemberTagging() {
        hide()
    }

    override fun onMemberTagged(user: UserTagViewData) {
        val tagSpannableString = "@${user.name}"
        val memberName = SpannableString(tagSpannableString)

        //if id == 0, then it is a group tag
        val regex = if (user.id == 0) {
            //get regex from object
            user.tag
        } else {
            //create regex from name and id
            "<<${user.name}|route://user_profile/${user.uuid}>>"
        }

        //set span
        memberName.setSpan(
            MemberTaggingClickableSpan(
                extras.color,
                regex
            ), 0, memberName.length, 0
        )
        val selectedMember = getMemberFromSelectedList(user.userUniqueId)
        if (selectedMember == null) {
            selectedMembers.add(user)
        }
        memberTaggingTextWatcher.replaceEditText(memberName)
        memberTaggingTextWatcher.resetGlobalPosition()
        memberTaggingViewListener?.onMemberTagged(user)
        hide()
    }

    fun decodeFirstMemberAndAddToSelectedList(text: String?): String? {
        MemberTaggingDecoder.decode(
            extras.editText,
            text,
            extras.color
        )
        val firstMember = MemberTaggingDecoder.decodeAndReturnAllTaggedMembers(text).firstOrNull()
            ?: return null
        val member = getMember(firstMember.first) ?: return null
        if (getMemberFromSelectedList(member.userUniqueId) == null) {
            selectedMembers.add(member)
        }
        return member.name
    }

    fun replaceSelectedMembers(editable: Editable?): String {
        if (editable == null) {
            return ""
        }
        val spans = MemberTaggingUtil.getSortedSpan(editable)
        val stringBuilder = StringBuilder()
        var lastIndex = 0
        spans.forEach { span ->
            val start = editable.getSpanStart(span)
            val end = editable.getSpanEnd(span)
            if (start > lastIndex) {
                stringBuilder.append(editable.substring(lastIndex, start))
            }
            stringBuilder.append(span.regex)
            lastIndex = end
        }
        if (editable.length >= lastIndex) {
            stringBuilder.append(editable.substring(lastIndex))
        }
        return stringBuilder.toString()
    }

    fun setMembersAndGroup(usersAndGroups: ArrayList<UserTagViewData>) {
        communityMembersAndGroups.clear()
        communityMembersAndGroups.addAll(usersAndGroups)
        showMemberTaggingList()
    }

    fun addMembers(usersAndGroups: ArrayList<UserTagViewData>) {
        communityMembersAndGroups.addAll(usersAndGroups)
        if (isShowing) {
            mAdapter.allMembers(usersAndGroups)
        }
    }

    fun getTaggedMemberCount() = selectedMembers.size

    fun getTaggedMembers() = selectedMembers.toList()

    fun isMembersListEmpty() = communityMembersAndGroups.isEmpty()

}