package com.likeminds.feedsample.utils.membertagging.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.feedsample.databinding.ItemMemberBinding
import com.likeminds.feedsample.utils.membertagging.model.UserTagViewData

internal class MemberAdapter(
    private val darkMode: Boolean,
    private val memberAdapterClickListener: MemberAdapterClickListener
) : RecyclerView.Adapter<MemberViewHolder>() {

    private val members = ArrayList<UserTagViewData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding, darkMode, memberAdapterClickListener)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount() = members.size

    /**
     * Updates the member list in the recyclerview adapter
     */
    @JvmSynthetic
    internal fun setMembers(users: List<UserTagViewData>) {
        this.members.clear()
        this.members.addAll(users)
        notifyDataSetChanged()
    }

    @JvmSynthetic
    internal fun allMembers(users: List<UserTagViewData>) {
        this.members.addAll(users)
        notifyDataSetChanged()
    }

    @JvmSynthetic
    internal fun clear() {
        this.members.clear()
        notifyDataSetChanged()
    }
}