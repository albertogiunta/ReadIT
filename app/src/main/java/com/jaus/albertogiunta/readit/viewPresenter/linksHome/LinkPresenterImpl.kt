package com.jaus.albertogiunta.readit.viewPresenter.linksHome

import com.jaus.albertogiunta.readit.MyApplication
import com.jaus.albertogiunta.readit.db.LinkDao
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

class LinkPresenterImpl : BasePresenterImpl<LinksContract.View>(), LinksContract.Presenter {

    override var linkList = mutableListOf<Link>() // treated as FIFO queue (newer links go to the bottom)
    private val dao: LinkDao = MyApplication.database.linkDao()
    private var editingIndex: Int = 0

    init {
        doAsync {
            linkList.addAll(dao.getAllLinksFromMostRecent().filterAndSortForLinksActivity())
        }
    }

    override fun onActivityResumed() {
        updateListInView(true)
    }

    override fun onLinkAdditionRequest(isNew: Boolean, url: String) {
        if (url == Link.EMPTY_LINK) {
            view?.showError("Your link seems to be empty or not a valid link :/"); return
        }

        var polishedURL: String = url.replace("\\s+", "")
        try {
            if (url.indexOf("http") == -1) polishedURL = "http://$polishedURL"
        } catch (e: Exception) {
            view?.showError("Your link seems to be empty or not a valid link :/"); return
        }

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
                    if (isNew) Link(title = siteInfo.title, url = siteInfo.url).addTo(dao, linkList)
                    else linkList[editingIndex].apply {
                        this.title = siteInfo.title
                        this.url = siteInfo.url
                    }.update(dao, linkList, editingIndex)
                    updateListInView()
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
            val link = dao.getLinkById(linkList[position].id)
            link.apply { seen = true }.update(dao, linkList, position)
            view?.launchBrowser(link)
            updateListInView()
        }
    }

    override fun onLinkSharingRequest(position: Int) {
        sendFirebaseEvent(LINK_INTERACTION, LINK_SHARE)
        doAsync {
            view?.launchShare(dao.getLinkById(linkList[position].id))
        }
    }

    override fun onLinkCopyRequest(position: Int) {
        sendFirebaseEvent(LINK_INTERACTION, LINK_COPY)
        view?.run {
            this.getContext().saveURLToClipboard(linkList[position].url)
            this.showMessage("Link copied to your Clipboard!")
        }
    }

    override fun onLinkRemovalRequest(position: Int) {
        sendFirebaseEvent(LINK_INTERACTION, LINK_REMOVE)
        doAsync {
            linkList[position].remove(dao, linkList, position)
            updateListInView()
            uiThread {
                view?.showMessage("Link removed successfully")
            }
        }
    }

    override fun onLinkUpdateRequest(position: Int) {
        sendFirebaseEvent(LINK_INTERACTION, LINK_UPDATE)
        editingIndex = position
        doAsync {
            val link = dao.getLinkById(linkList[position].id)
            uiThread {
                view?.displayUpdateDialog(link)
            }
        }
    }

    override fun shouldShowLinkList(): Boolean = linkList.isNotEmpty()

    override fun onSeenToggleRequest() {
        doAsync {
            // toggle shared preferences
            Settings.showSeen = !Settings.showSeen

            // re-init linkList because otherwise un-toggling doesn't work (the whole fresh list is needed)
            linkList.clear()
            linkList.addAll(dao.getAllLinksFromMostRecent())
        }.get()

        updateListInView(true)
        view?.toggleSeenLinks(Settings.showSeen)
    }

    override fun onCardToggleRequest(cardLayout: CardLayout) {
        if (Settings.cardLayout.id != cardLayout.id) Settings.cardLayout = cardLayout
        view?.toggleCardLayoutMenuItems()
    }

    private fun updateListInView(forceRefresh: Boolean = false) {
        val list = linkList.filterAndSortForLinksActivity() // sort & filter
        linkList.clear()
        linkList.addAll(list)
        doAsync {
            uiThread {
                if (!forceRefresh) view?.updateLinkListUI() else view?.completelyRedrawList() // update UI
            }
        }
    }

    private fun sendFirebaseEvent(contentType: FirebaseContentType, action: FirebaseAction) {
        view?.getContext()?.sendFirebaseEvent(contentType, action)
    }
}