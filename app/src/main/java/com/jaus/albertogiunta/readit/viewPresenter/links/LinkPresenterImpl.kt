package com.jaus.albertogiunta.readit.viewPresenter.links

import com.jaus.albertogiunta.readit.MyApplication
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

class LinkPresenterImpl : BasePresenterImpl<LinksContract.View>(), LinksContract.Presenter {

    override var linkListForView = mutableListOf<Link>() // treated as FIFO queue (newer links go to the bottom)
    private val dao: LinkDao = MyApplication.database.linkDao()
    private var editingIndex: Int = 0

    init {
        doAsync {
            linkListForView.addAll(dao.getAllLinksFromMostRecent())
        }
    }

    override fun onActivityResumed() {
        refreshListToUpdateView()
    }

    override fun onLinkAdditionRequest(isNew: Boolean, url: String) {
        if (url == Link.EMPTY_LINK) {
            view?.showError("Your link seems to be empty or not a valid link :/")
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
                view?.showMessage("Link queued successfully")
                sendFirebaseEvent(LINK_INTERACTION, LINK_ADD)
            }, { error ->
                println(error)
                view?.showError("Your link seems to be not a valid link :/")
            })
    }

    override fun onLinkBrowsingRequest(position: Int) {
        sendFirebaseEvent(LINK_INTERACTION, LINK_BROWSE)
        doAsync {
            val link = dao.getLinkById(linkListForView[position].id)
            link.apply { seen = true }.update(dao, linkListForView, position)
            view?.launchBrowser(link)
            refreshListToUpdateView()
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
            this.showMessage("Link copied to your Clipboard!")
        }
    }

    override fun onLinkRemovalRequest(position: Int) {
        sendFirebaseEvent(LINK_INTERACTION, LINK_REMOVE)
        doAsync {
            linkListForView[position].remove(dao, linkListForView, position)
            refreshListToUpdateView()
            uiThread {
                view?.showMessage("Link removed successfully")
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

    override fun shouldShowLinkReadToggleButton(): Boolean {
        var unseenCount = 0
        doAsync {
            unseenCount = dao.getAllSeenLinksCount()
        }.get()
        return unseenCount != 0
    }

    override fun onSeenToggleRequest() {
        doAsync { Settings.showSeen = !Settings.showSeen }.get()
        refreshListToUpdateView()
        view?.toggleSeenLinks(Settings.showSeen)
    }

    override fun rewardUser() {
        Prefs.expiredLinksLastActivationTimestamp = DateTime.now().toString(Utils.dateTimeFormatISO8601)
        refreshListToUpdateView()
    }

    private fun fetchLinksForActivity() {
        val list = dao.getAllLinksFromMostRecent().filterAndSortForLinksActivity() // sort & filter
        linkListForView.clear()
        linkListForView.addAll(list)
    }

    private fun refreshListToUpdateView() {
        doAsync { fetchLinksForActivity() }
            .get()
            .also {
                doAsync {
                    val unreadExpiredCount = dao.getAllUnseenExpiredLinks()
                    uiThread {
                        view?.toggleActivityContentVisibilityTo(true)
                        view?.updateLinkListUI()
                        view?.updateUnreadExpiredLinksCount(unreadExpiredCount)
                    }
                }
            }
    }

    private fun sendFirebaseEvent(contentType: FirebaseContentType, action: FirebaseAction) {
        view?.getContext()?.sendFirebaseEvent(contentType, action)
    }
}