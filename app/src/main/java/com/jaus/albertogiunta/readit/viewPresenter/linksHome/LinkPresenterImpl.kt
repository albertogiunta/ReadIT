package com.jaus.albertogiunta.readit.viewPresenter.linksHome

import com.jaus.albertogiunta.readit.MyApplication
import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.networking.LinkService
import com.jaus.albertogiunta.readit.networking.NetworkingFactory
import com.jaus.albertogiunta.readit.utils.*
import com.jaus.albertogiunta.readit.viewPresenter.base.BasePresenterImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime

class LinkPresenterImpl : BasePresenterImpl<LinksContract.View>(), LinksContract.Presenter {

    override var linkList = mutableListOf<Link>()
    private val dao: LinkDao = MyApplication.database.linkDao()
    private var editingIndex: Int = 0

    init {
        doAsync {
            if (!Link.IS_ALL_LINKS_DEBUG_ACTIVE) {
                linkList.addAll(dao.getAllLinksFromMostRecent().filter { it.timestamp.isAfter(DateTime.now().minusHours(24)) })
            } else {
                linkList.addAll(dao.getAllLinksFromMostRecent())
            }
        }
    }

    override fun onLinkAdditionRequest(isNew: Boolean, url: String) {
        if (url == Link.EMPTY_LINK) {
            view?.showError("Your link seems to be empty or not a valid link :/"); return
        }

        val polishedURL: String
        val indexOfProtocol = url.indexOf("http")

        if (indexOfProtocol == -1) {
            TODO("polishedURL = url has no protocol")
        } else {
            polishedURL = url.substring(indexOfProtocol).replace("\\s+", "")
        }

        NetworkingFactory
                .createService(LinkService::class.java)
                .contactWebsite(polishedURL)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.startLoadingState() }
                .doAfterTerminate { view?.stopLoadingState() }
                .subscribe({ result ->
                    val htmlPage = result.toJsoupDocument()
                    if (isNew) Link(title = htmlPage.title(), url = polishedURL).addTo(dao, linkList)
                    else linkList[editingIndex].apply {
                        this.title = htmlPage.title()
                        this.url = polishedURL
                    }.update(dao, linkList, editingIndex)

                    view?.updateLinkListUI()
                    result.close()
                }, { error ->
                    println(error)
                    view?.showError("Your link seems to be not a valid link :/")
                })
    }

    override fun onLinkBrowsingRequest(position: Int) {
        doAsync {
            val link = dao.getLinkById(linkList[position].id)
            link.apply { seen = true }.update(dao, linkList, position)
            view?.launchBrowser(link)
            uiThread {
                view?.updateLinkListUI()
            }
        }
    }

    override fun onLinkSharingRequest(position: Int) {

    }

    override fun onLinkCopyRequest(position: Int) {
        view?.run {
            this.getContext().saveURLToClipboard(linkList[position].url)
            this.showMessage("Link copied to your Clipboard!")
        }
    }

    override fun onLinkRemovalRequest(position: Int) {
        doAsync {
            linkList[position].remove(dao, linkList, position)
            uiThread {
                view?.updateLinkListUI()
                view?.showMessage("Link removed successfully")
            }
        }
    }

    override fun onLinkUpdateRequest(position: Int) {
        editingIndex = position
        doAsync {
            val link = dao.getLinkById(linkList[position].id)
            uiThread {
                view?.displayUpdateDialog(link)
            }
        }
    }
}