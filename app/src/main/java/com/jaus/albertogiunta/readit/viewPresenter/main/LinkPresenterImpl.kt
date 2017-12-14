package com.jaus.albertogiunta.readit.viewPresenter.main

import com.jaus.albertogiunta.readit.MyApplication
import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.networking.LinkService
import com.jaus.albertogiunta.readit.networking.NetworkingFactory
import com.jaus.albertogiunta.readit.utils.addTo
import com.jaus.albertogiunta.readit.utils.toJsoupDocument
import com.jaus.albertogiunta.readit.viewPresenter.base.BasePresenterImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.doAsync

class LinkPresenterImpl : BasePresenterImpl<LinksContract.View>(), LinksContract.Presenter {

    override var linkList = mutableListOf<Link>()
    private val dao: LinkDao = MyApplication.database.linkDao()

    init {
        doAsync {
            linkList.addAll(dao.getAllLinksFromMostRecent())
        }
    }

    override fun onLinkAdditionRequest(url: String) {
        if (url == Link.EMPTY_LINK) {
            view?.showError("Your link seems to be not valid :/"); return
        }


        NetworkingFactory
                .createService(LinkService::class.java)
                .contactWebsite(url)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.startLoadingState() }
                .doAfterTerminate { view?.stopLoadingState() }
                .subscribe({ result ->
                    val htmlPage = result.toJsoupDocument()
                    Link(title = htmlPage.title(), url = url).addTo(dao, linkList)
                    view?.updateLinkListUI()
                }, { error ->
                    println(error)
                    view?.showError("Your link seems to be invalid :/")
                })
    }

    override fun onLinkOpeningRequest() {
        NotImplementedError()
    }
}