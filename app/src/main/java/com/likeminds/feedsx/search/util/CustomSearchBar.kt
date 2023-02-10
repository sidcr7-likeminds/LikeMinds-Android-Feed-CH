package com.likeminds.feedsx.search.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.Configuration
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.constraintlayout.widget.ConstraintLayout
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.databinding.LayoutSearchBarBinding
import com.likeminds.feedsx.utils.AnimationUtils.circleHideView
import com.likeminds.feedsx.utils.AnimationUtils.circleRevealView
import com.likeminds.feedsx.utils.ViewUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class CustomSearchBar @JvmOverloads constructor(
    mContext: Context,
    attributeSet: AttributeSet? = null
) : ConstraintLayout(mContext, attributeSet) {

    private val subject = PublishSubject.create<String>()

    private val isHardKeyboardAvailable: Boolean
        get() = this.resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS

    /**
     * The previous query text.
     */
    private var mOldQuery: CharSequence = ""

    /**
     * The current query text.
     */
    private var mCurrentQuery = ""

    /**
     * Listener for when the search view opens and closes.
     */
    private var mSearchViewListener: SearchViewListener? = null

    /**
     * Determines if the search view is opened or closed.
     * @return True if the search view is open, false if it is closed.
     */
    /**
     * Whether or not the search view is open right now.
     */
    var isOpen = false
        private set

    private val compositeDisposable = CompositeDisposable()

    private val binding = LayoutSearchBarBinding.inflate(LayoutInflater.from(context), this, true)

    private fun displayClearButton(display: Boolean) {
        binding.ivClose.visibility = if (display) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "CustomSearchBar"
    }

    init {
        binding.ivClose.setOnClickListener {
            binding.etSearch.setText("")
            mSearchViewListener?.crossClicked()
        }
        binding.ivBack.setOnClickListener { closeSearch() }
        initSearchView()
    }

    private fun initSearchView() {
        binding.etSearch.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSubmitQuery()
                return@OnEditorActionListener true
            }
            false
        })
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isOpen) {
                    onTextChanged(s)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.etSearch.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            // If we gain focus, show keyboard and show suggestions.
            if (hasFocus) {
                showKeyboard(view)
            }
        }
    }

    /**
     * Closes the search view if necessary.
     */
    fun closeSearch() {
        if (!isOpen) {
            return
        }
        isOpen = false
        // Clear text, values, and focus.
        binding.etSearch.setText("")
        clearFocusInHere()
        val listenerAdapter: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                // After the animation is done. Hide the root view.
                binding.searchToolbar.visibility = View.GONE
            }
        }
        circleHideView(binding.searchToolbar, listenerAdapter)
        mSearchViewListener?.onSearchViewClosed()
    }

    /**
     * Filters and updates the buttons when text is changed.
     * @param newText The new text.
     */
    private fun onTextChanged(newText: CharSequence) {
        // Get current query
        mCurrentQuery = binding.etSearch.text.toString()

        // If the text is not empty, show the empty button and hide the voice button
        if (!TextUtils.isEmpty(mCurrentQuery)) {
            displayClearButton(true)
        } else {
            displayClearButton(false)
        }

        // If we have a query listener and the text has changed, call it.
        if (subject.hasObservers() &&
            (!TextUtils.isEmpty(mCurrentQuery) || !TextUtils.isEmpty(mOldQuery))
        ) {
            subject.onNext(newText.toString())
        }

        mOldQuery = mCurrentQuery
    }

    /**
     * Called when a query is submitted. This will close the search view.
     */
    private fun onSubmitQuery() {
        // Get the query.
        val query: CharSequence? = binding.etSearch.text
        // If the query is not null and it has some text, submit it.
        if (query != null && TextUtils.getTrimmedLength(query) > 0 && subject.hasObservers()) {
            clearFocusInHere()
        }
    }

    fun openSearch() {
        // If search is already open, just return.
        if (isOpen) {
            return
        }
        // Get focus
        binding.etSearch.setText("")
        binding.etSearch.requestFocus()
        circleRevealView(binding.searchToolbar)
        if (BrandingData.currentPrimary != null) {
            setBackgroundColor(BrandingData.currentPrimary!!)
        } else if (BrandingData.currentAdvanced != null) {
            setBackgroundColor(BrandingData.currentAdvanced!!.first)
        }
        elevation = 20F
        isOpen = true
        mSearchViewListener?.onSearchViewOpened()
    }

    private fun showKeyboard(view: View?) {
        view?.requestFocus()
        if (isHardKeyboardAvailable.not()) {
            val inputMethodManager =
                view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(view, 0)
        }
    }

    fun setSearchViewListener(mSearchViewListener: SearchViewListener?) {
        this.mSearchViewListener = mSearchViewListener
    }

    private fun clearFocusInHere() {
        ViewUtils.hideKeyboard(this)
        binding.etSearch.clearFocus()
    }

    fun hasKeyword(): Boolean {
        return mCurrentQuery.isNotEmpty()
    }

    fun observeSearchView(debounce: Boolean = true) {
        val observable = if (debounce) {
            subject.debounce(500, TimeUnit.MILLISECONDS)
                .map { text -> text.trim() }
        } else {
            subject.map { text -> text.trim() }
        }
        val disposable = observable.distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ keyword ->
                if (keyword.isNotEmpty()) {
                    mSearchViewListener?.keywordEntered(keyword)
                } else {
                    mSearchViewListener?.emptyKeywordEntered()
                }
            }, {
                Log.e(TAG, "${it.message}")
            })
        compositeDisposable.add(disposable)
    }

    fun dispose() {
        compositeDisposable.dispose()
    }

    /**
     * Interface that handles the opening and closing of the SearchView.
     */
    interface SearchViewListener {
        fun onSearchViewOpened() {}

        fun onSearchViewClosed()

        fun crossClicked()

        fun keywordEntered(keyword: String) {}

        fun emptyKeywordEntered() {}
    }

}