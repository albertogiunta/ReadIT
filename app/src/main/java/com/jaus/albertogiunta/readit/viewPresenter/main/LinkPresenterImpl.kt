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
        NetworkingFactory
                .createService(LinkService::class.java)
                .contactWebsite(url)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view?.startLoadingState() }
                .doAfterTerminate { view?.stopLoadingState() }
                .subscribe({ result ->
                    run {
                        val htmlPage = result.toJsoupDocument()
                        Link(title = htmlPage.title(), url = url).addTo(dao, linkList)

                        // FAVICON
//                        var faviconUri = htmlPage.head().select("link[href~=.*\\.ico]").first().attr("href").replace(Regex("^/+"), "")
//                        if (!faviconUri.contains("www")) faviconUri = "$url$faviconUri"
//                        println("uriiiii   $faviconUri")

                        /**
                         * strategies:
                         * 1) baseurl/favicon.ico
                         * 2) if failed search with regex for .ico file uri, remove leading // (but note that it doesn't always work
                         * 3) display placeholder instead of favicon
                         */


                        view?.updateLinkListUI("")

                    }
                }, { error -> println(error) }
                )
    }

    override fun onLinkOpeningRequest() {
        NotImplementedError()
    }
}