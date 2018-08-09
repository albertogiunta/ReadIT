package com.jaus.albertogiunta.readit.viewPresenter.linksList

import com.jaus.albertogiunta.readit.MyApplication
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.db.Prefs
import com.jaus.albertogiunta.readit.db.Settings
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.model.WebsiteInfo
import com.jaus.albertogiunta.readit.networking.LinkService
import com.jaus.albertogiunta.readit.networking.NetworkingFactory
import com.jaus.albertogiunta.readit.utils.*
import com.jaus.albertogiunta.readit.utils.FirebaseAction.*
import com.jaus.albertogiunta.readit.utils.FirebaseContentType.LINK_INTERACTION
import com.jaus.albertogiunta.readit.viewPresenter.base.BasePresenterImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime

class LinksListPresenter : BasePresenterImpl<LinksListContract.View>(), LinksListContract.Presenter {

    private var linkList = mutableListOf<Link>()
    override var linkListForView = mutableListOf<Link>() // treated as FIFO queue (newer links go to the bottom)
    private val dao: LinkDao = MyApplication.database.linkDao()
    private var editingIndex: Int = 0

    init {
        refillLinksList()
    }

    private fun refillLinksList() =
        linkList.clear().also { doAsync { linkList.addAll(dao.getAllLinksFromMostRecent()) }.get() }

    override fun onActivityResumed() = refreshListToUpdateView()

    override fun onLinkAdditionRequest(isNew: Boolean, url: String) {
        if (url == Link.EMPTY_LINK) {
            view?.run { this.showErrorSnackbar(this.getContext().getString(R.string.toast_onfailure_link_addition)) }
            return
        }

        val polishedURL: String = url.polished()

        NetworkingFactory
            .createService(LinkService::class.java)
            .contactWebsite(polishedURL)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view?.startLoadingState() }
            .doAfterTerminate { view?.stopLoadingState() }
            .map { result: ResponseBody ->
                with(result.toJsoupDocument()) {
                    result.close()
                    WebsiteInfo(polishedURL, title())
                }
            }
            .subscribe({ siteInfo: WebsiteInfo ->
                if (isNew) Link(title = siteInfo.title, url = siteInfo.url).addTo(dao, linkListForView)
                else linkListForView[editingIndex].apply {
                    this.title = siteInfo.title
                    this.url = siteInfo.url
                }.update(dao, linkListForView, editingIndex)
                refreshListToUpdateView()
                view?.run { this.showErrorSnackbar(this.getContext().getString(R.string.toast_onsuccess_link_addition)) }
            }, { error ->
                println(error)
                view?.run { this.showErrorSnackbar(this.getContext().getString(R.string.toast_onfailure_link_addition)) }
            })
    }

    override fun onLinkBrowsingRequest(position: Int) {
        sendFirebaseEvent(LINK_INTERACTION, LINK_BROWSE)
        doAsync {
            val link = dao.getLinkById(linkListForView[position].id)
            link.apply { seen = true }.update(dao, linkListForView, position)
            view?.launchBrowser(link)
        }
    }

    override fun onLinkSharingRequest(position: Int) {
        sendFirebaseEvent(LINK_INTERACTION, LINK_SHARE)
        doAsync {
            view?.launchShare(dao.getLinkById(linkListForView[position].id))
        }
    }

    override fun onLinkCopyRequest(position: Int) {
        sendFirebaseEvent(LINK_INTERACTION, LINK_COPY)
        view?.run {
            this.getContext().saveURLToClipboard(linkListForView[position].url)
            this.showMessageSnackbar(this.getContext().getString(R.string.toast_link_copied_to_clipboard))
        }
    }

    override fun onLinkRemovalRequest(position: Int) {
        sendFirebaseEvent(LINK_INTERACTION, LINK_REMOVE)
        doAsync {
            linkListForView[position].remove(dao, linkListForView, position)
            uiThread {
                refreshListToUpdateView()
                view?.run {
                    this.showMessageSnackbar(this.getContext().getString(R.string.toast_link_removed))
                }
            }
        }
    }

    override fun onLinkUpdateRequest(position: Int) {
        sendFirebaseEvent(LINK_INTERACTION, LINK_UPDATE)
        editingIndex = position
        doAsync {
            val link = dao.getLinkById(linkListForView[position].id)
            uiThread {
                view?.displayUpdateDialog(link)
            }
        }
    }

    override fun shouldShowLinkList(): Boolean = linkListForView.isNotEmpty()

    override fun shouldShowShowSeenMenuButton(): Boolean = linkList.any { it.seen }

    override fun shouldShowUnlockButton(): Boolean {
        return !isRewardActive() && linkList.getExpiredCount() > 0
    }

    override fun onSeenToggleRequest() {
        doAsync { Settings.showSeen = !Settings.showSeen }.get()
        refreshListToUpdateView()
        view?.toggleSeenLinks(Settings.showSeen)
    }

    override fun rewardUser() {
        Prefs.expiredLinksLastActivationTimestamp = DateTime.now().toString(Utils.dateTimeFormatISO8601)
        Settings.showSeen = true
        refreshListToUpdateView()
    }

    private fun refreshListToUpdateView() {
        fetchLinksForActivity()

        view?.updateNotification()
        toggleScreenContent()
        toggleUnlockButton()
//        toggleVisibilityShowSeenMenuButton()

    }

    private fun toggleUnlockButton() {
        if (shouldShowUnlockButton()) {
            with(linkList.getUnreadExpiredCount()) {
                val buttonText = when {
                    this > 0 -> "You've $this unread & expired link${if (this > 1) "s" else ""}!\nLet's catch up?"
                    else -> "Unlock your expired links"
                }
            view?.showUnlockButton(buttonText)
            }
        } else {
            view?.hideUnlockButton()
        }
    }

    private fun toggleScreenContent() {
        if (shouldShowLinkList()) view?.showContent() else view?.showLandingScreen()
    }

    private fun toggleVisibilityShowSeenMenuButton() {
        if (shouldShowShowSeenMenuButton()) view?.showShowSeenMenuButton() else view?.hideShowSeenMenuButton()
    }

    private fun fetchLinksForActivity() {
        refillLinksList()
            .also {
                linkListForView.clear()
                linkListForView.addAll(linkList.filterAndSortForLinksActivity())
            }
    }

    private fun sendFirebaseEvent(contentType: FirebaseContentType, action: FirebaseAction) {
        view?.getContext()?.sendFirebaseEvent(contentType, action)
    }
}