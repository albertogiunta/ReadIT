package com.jaus.albertogiunta.readit.viewPresenter.backgroundAddition

import com.jaus.albertogiunta.readit.MyApplication
import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.model.WebsiteInfo
import com.jaus.albertogiunta.readit.networking.LinkService
import com.jaus.albertogiunta.readit.networking.NetworkingFactory
import com.jaus.albertogiunta.readit.notifications.NotificationBuilder
import com.jaus.albertogiunta.readit.utils.addTo
import com.jaus.albertogiunta.readit.utils.polished
import com.jaus.albertogiunta.readit.utils.toJsoupDocument
import com.jaus.albertogiunta.readit.viewPresenter.base.BasePresenterImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class BackgroundAdditionPresenter : BasePresenterImpl<BackgroundAdditionContract.View>(), BackgroundAdditionContract.Presenter {

    override var dao: LinkDao = MyApplication.database.linkDao()

    override fun onActivityResumed() {
//        view?.closeActivity()
    }

    override fun onLinkAdditionRequest(url: String) {
        doAsync {
            uiThread {
                if (url == Link.EMPTY_LINK) {
                    view?.showErrorToast("Your link doesn't seem to be valid :/")
//            return
                }

                val polishedURL: String = url.polished()

                NetworkingFactory
                    .createService(LinkService::class.java)
                    .contactWebsite(polishedURL)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { result: ResponseBody ->
                        with(result.toJsoupDocument()) {
                            result.close()
                            WebsiteInfo(polishedURL, title())
                        }
                    }
                    .subscribe({ siteInfo: WebsiteInfo ->
                        Link(title = siteInfo.title, url = siteInfo.url).addTo(dao)
                        view?.showMessageToast("Link added to your reading list. Great!")
                        NotificationBuilder.instance.sendBundledNotification()
                    }, { error ->
                        println(error)
                        view?.showErrorToast("Your link doesn't seem to be valid :/")
                    })
            }
        }
    }
}