package com.jaus.albertogiunta.readit.viewPresenter.backgroundAddition

import android.content.Intent
import android.os.Bundle
import com.jaus.albertogiunta.readit.MyApplication
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.model.WebsiteInfo
import com.jaus.albertogiunta.readit.networking.LinkService
import com.jaus.albertogiunta.readit.networking.NetworkingFactory
import com.jaus.albertogiunta.readit.notifications.NotificationBuilder
import com.jaus.albertogiunta.readit.utils.*
import com.jaus.albertogiunta.readit.viewPresenter.base.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.ResponseBody

class BackgroundAdditionActivity : BaseActivity<BackgroundAdditionContract.View, BackgroundAdditionContract.Presenter>(), BackgroundAdditionContract.View {

    override var presenter: BackgroundAdditionContract.Presenter = BackgroundAdditionPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
    }

    override fun onResume() {
        super.onResume()
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
            showErrorToast(getString(R.string.toast_onfailure_link_addition))
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
                showMessageToast(getString(R.string.toast_onsuccess_link_addition))
                NotificationBuilder.instance.sendBundledNotification()
                sendFirebaseEvent(FirebaseContentType.LINK_INTERACTION, FirebaseAction.LINK_ADD_BG)
            }, { error ->
                println(error)
                showErrorToast(getString(R.string.toast_onfailure_link_addition))
            })
    }

    override fun closeActivity() {
        finishAffinity()
    }
}