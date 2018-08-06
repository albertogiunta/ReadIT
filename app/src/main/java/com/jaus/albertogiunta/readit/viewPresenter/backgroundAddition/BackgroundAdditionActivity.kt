package com.jaus.albertogiunta.readit.viewPresenter.backgroundAddition

import android.content.Intent
import android.os.Bundle
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
import com.jaus.albertogiunta.readit.viewPresenter.base.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.ResponseBody

class BackgroundAdditionActivity : BaseActivity<BackgroundAdditionContract.View, BackgroundAdditionContract.Presenter>(), BackgroundAdditionContract.View {

    override var presenter: BackgroundAdditionContract.Presenter = BackgroundAdditionPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
//        closeActivity()
    }

    override fun onResume() {
        super.onResume()
//        presenter.onActivityResumed()
        closeActivity()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        with(intent) {
            if ((Intent.ACTION_SEND == action || Intent.ACTION_VIEW == action) && type != null && "text/plain" == type) {
                getStringExtra(Intent.EXTRA_TEXT)?.let {
                    if (it != Link.EMPTY_LINK) onLinkAdditionRequest(it)
//                    if (it != Link.EMPTY_LINK) presenter.onLinkAdditionRequest(it)
                    getIntent().removeExtra(Intent.EXTRA_TEXT)
                }
            }
        }
    }

    private fun onLinkAdditionRequest(url: String) {
        val dao: LinkDao = MyApplication.database.linkDao()
        if (url == Link.EMPTY_LINK) {
            showErrorToast("Your link doesn't seem to be valid :/")
            return
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
                showMessageToast("Link added to your reading list. Great!")
                NotificationBuilder.instance.sendBundledNotification()
//                sendFirebaseEvent(FirebaseContentType.LINK_INTERACTION, FirebaseAction.LINK_ADD)
            }, { error ->
                println(error)
                showErrorToast("Your link doesn't seem to be valid :/")
            })
    }

    override fun closeActivity() {
        finishAffinity()
    }
}