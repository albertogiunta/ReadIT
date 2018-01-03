package com.jaus.albertogiunta.readit.viewPresenter.linksHome

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.notifications.NotificationBuilder
import com.jaus.albertogiunta.readit.utils.consumeEditButton
import com.jaus.albertogiunta.readit.utils.getURLFromClipboard
import com.jaus.albertogiunta.readit.utils.toggleVisibility
import com.jaus.albertogiunta.readit.viewPresenter.base.BaseActivity
import kotlinx.android.synthetic.main.activity_links.*
import kotlinx.android.synthetic.main.dialog_manual_input.view.*
import kotlinx.android.synthetic.main.section_ad.*
import kotlinx.android.synthetic.main.section_link_options.view.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.share


class LinksActivity : BaseActivity<LinksContract.View, LinkPresenterImpl>(), LinksContract.View {

    override var presenter: LinkPresenterImpl = LinkPresenterImpl()
    private lateinit var urlFetchingWaitDialog: AlertDialog
    private lateinit var itemOnClick: (View, Int, Int) -> Unit
    private lateinit var itemOnLongClick: (View, Int, Int) -> Unit
    private val notificationManager = NotificationBuilder.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)

        // UI initialization
        itemOnClick = { _, position, _ -> presenter.onLinkBrowsingRequest(position) }
        itemOnLongClick = { view, position, _ ->
            run {
                view.clEditButtons.toggleVisibility()
                with(view.clEditButtons) {
                    ibShare.setOnClickListener { consumeEditButton { presenter.onLinkSharingRequest(position) } }
                    ibEdit.setOnClickListener { consumeEditButton { presenter.onLinkUpdateRequest(position) } }
                    ibCopy.setOnClickListener { consumeEditButton { presenter.onLinkCopyRequest(position) } }
                    ibRemove.setOnClickListener { consumeEditButton { presenter.onLinkRemovalRequest(position) } }
                }
            }
        }

        adView.loadAd(AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build())

        fabAdd.setOnClickListener { displayNewLinkDialog() }

        // LIST initialization
        rvLinks.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rvLinks.adapter = LinkAdapter(presenter.linkList, itemOnClick, itemOnLongClick)
        updateLinkListUI()

        // DIALOG initialization
        urlFetchingWaitDialog = indeterminateProgressDialog(message = "Imma fetch all the info for ya", title = "Noice, you got a new link!")
        urlFetchingWaitDialog.cancel()
    }

    override fun onResume() {
        super.onResume()
        presenter.onActivityResumed()
    }

    //////////////////// LOADING
    override fun startLoadingState() = urlFetchingWaitDialog.show()

    override fun stopLoadingState() = urlFetchingWaitDialog.cancel()

    //////////////////// EMPTY ACTIVITY
    override fun showContent(showContent: Boolean) {
        rvLinks.toggleVisibility(showContent)
        adLayout.toggleVisibility(showContent)
        emptyLayout.toggleVisibility(!showContent)
    }

    //////////////////// LINK INTERACTION
    override fun updateLinkListUI() {
        runOnUiThread {
            // show content if list is not empty, show empty state activity otherwise
            showContent(presenter.shouldShowLinkList())
            if (presenter.shouldShowLinkList()) rvLinks.adapter.notifyDataSetChanged()
            notificationManager.sendBundledNotification()
        }
    }

    override fun completelyRedrawList() {
        runOnUiThread {
            rvLinks.adapter = null
            rvLinks.layoutManager = null
            rvLinks.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
            rvLinks.adapter = LinkAdapter(presenter.linkList, itemOnClick, itemOnLongClick)
            updateLinkListUI()
        }
    }

    override fun updateNotification() {
        notificationManager.sendBundledNotification()
    }

    override fun displayUpdateDialog(link: Link) {
        displayInputDialog(false, link.url)
    }

    override fun displayNewLinkDialog() {
        displayInputDialog(true)
    }

    override fun launchBrowser(link: Link) {
        try {
            browse(link.url)
        } catch (e: IllegalArgumentException) {
            showError("This link seems to be broken :O")
        }
    }

    override fun launchShare(link: Link) {
        share(link.url)
    }

    @SuppressLint("InflateParams")
    private fun displayInputDialog(isNew: Boolean, url: String = Link.EMPTY_LINK) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_manual_input, null)
        val builder = AlertDialog.Builder(getContext())

        if (url != Link.EMPTY_LINK) dialogView.etUrl.setText(url, TextView.BufferType.EDITABLE)

        val positiveButtonText = if (isNew) "Add" else "Update"
        val titleText = if (isNew) "Oh hey you! Got a new link for me?" else "Links like to change"

        val dialog = builder.setTitle(titleText)
                .setView(dialogView)
                .setPositiveButton(positiveButtonText, { _, _ ->
                    presenter.onLinkAdditionRequest(isNew, dialogView.etUrl.text.toString())
                })
                .setNeutralButton("Paste from clipboard", { _, _ ->
                    run {
                        val urlFromClipboard: String? = getURLFromClipboard()
                        urlFromClipboard?.let { presenter.onLinkAdditionRequest(isNew, urlFromClipboard) } ?: this.showError("No Link found in clipboard")
                    }
                })
                .setNegativeButton("Cancel", { _, _ -> })
                .create()

        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        with(intent) {
            if (Intent.ACTION_SEND == action && type != null && "text/plain" == type) {
                getStringExtra(Intent.EXTRA_TEXT)?.let {
                    if (it != Link.EMPTY_LINK) presenter.onLinkAdditionRequest(true, it)
                }
            }
        }
    }
}
