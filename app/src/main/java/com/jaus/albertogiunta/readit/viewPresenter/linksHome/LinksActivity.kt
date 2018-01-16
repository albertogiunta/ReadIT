package com.jaus.albertogiunta.readit.viewPresenter.linksHome

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.db.Prefs
import com.jaus.albertogiunta.readit.db.Settings
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.notifications.NotificationBuilder
import com.jaus.albertogiunta.readit.utils.*
import com.jaus.albertogiunta.readit.viewPresenter.base.BaseActivity
import com.jaus.albertogiunta.readit.viewPresenter.intro.IntroActivity
import kotlinx.android.synthetic.main.activity_links.*
import kotlinx.android.synthetic.main.dialog_about.view.*
import kotlinx.android.synthetic.main.dialog_manual_input.view.*
import kotlinx.android.synthetic.main.section_ad.*
import kotlinx.android.synthetic.main.section_link_options.view.*
import org.jetbrains.anko.*


class LinksActivity : BaseActivity<LinksContract.View, LinkPresenterImpl>(), LinksContract.View {

    override var presenter: LinkPresenterImpl = LinkPresenterImpl()
    private lateinit var urlFetchingWaitDialog: AlertDialog
    private lateinit var itemOnClick: (View, Int, Int) -> Unit
    private lateinit var itemOnLongClick: (View, Int, Int) -> Unit
    private val notificationManager = NotificationBuilder.instance
    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)
        setSupportActionBar(toolbar)

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

        with(adView) {
            loadAd(AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build())
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    this@LinksActivity.pbAdLoading.gone()
                    this@LinksActivity.tvAdLoading.gone()
                }
            }
        }

        fabAdd.setOnClickListener { displayNewLinkDialog() }

        // LIST initialization
        with(rvLinks) {
            layoutManager = LinearLayoutManager(this@LinksActivity, LinearLayout.VERTICAL, false)
            adapter = LinkAdapter(presenter.linkList, itemOnClick, itemOnLongClick)
        }

        // DIALOG initialization
        urlFetchingWaitDialog = indeterminateProgressDialog(message = "Imma fetch all the info for ya", title = "Noice, you got a new link!")
        with(urlFetchingWaitDialog) {
            urlFetchingWaitDialog.cancel()
        }

        onNewIntent(intent)

        doAsync {
            if (!Prefs.tutorialSeen) startActivity(intentFor<IntroActivity>())
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onActivityResumed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        this.menu = menu
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.toggleSeen(Settings.showSeen)
        menu.togglePreferredCardRadioButton()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return with(item) {
            when (itemId) {
                R.id.action_toggle_seen -> consumeOptionButton { presenter.onSeenToggleRequest() }
                R.id.action_toggle_card_1 -> consumeOptionButton { presenter.onCardToggleRequest(CardLayout.CARD1) }
                R.id.action_toggle_card_2 -> consumeOptionButton { presenter.onCardToggleRequest(CardLayout.CARD2) }
                R.id.action_refer -> consumeOptionButton { share("Try ReadIT for Android, and never forget to read a link again: https://play.google.com/store/apps/details?id=$packageName") }
                R.id.action_review -> consumeOptionButton { openPlayStore() }
                R.id.action_about -> consumeOptionButton { displayAboutDialog() }
                else -> super.onOptionsItemSelected(this)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        with(intent) {
            if (Intent.ACTION_SEND == action && type != null && "text/plain" == type) {
                getStringExtra(Intent.EXTRA_TEXT)?.let {
                    if (it != Link.EMPTY_LINK) presenter.onLinkAdditionRequest(true, it)
                    getIntent().removeExtra(Intent.EXTRA_TEXT)
                }
            }
        }
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
        }
        updateNotification()
    }

    override fun completelyRedrawList() {
        runOnUiThread {
            with(rvLinks) {
                adapter = null
                layoutManager = null
                layoutManager = LinearLayoutManager(this@LinksActivity, LinearLayout.VERTICAL, false)
                adapter = LinkAdapter(presenter.linkList, itemOnClick, itemOnLongClick)
            }
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

    override fun toggleSeenLinks(displaySeenLink: Boolean) {
        menu.toggleSeen(displaySeenLink)
        completelyRedrawList()
    }

    override fun toggleCardLayoutMenuItems() {
        menu.toggleCardsRadioButtons()
        completelyRedrawList()
    }

    @SuppressLint("InflateParams")
    private fun displayInputDialog(isNew: Boolean, url: String = Link.EMPTY_LINK) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_manual_input, null)
        val builder = AlertDialog.Builder(getContext(), R.style.MyCustomDialogTheme)

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

    @SuppressLint("InflateParams")
    private fun displayAboutDialog() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_about, null)
        val builder = AlertDialog.Builder(getContext(), R.style.MyCustomDialogTheme)
        dialogView.tvAbout3.movementMethod = LinkMovementMethod.getInstance()

        val dialog = builder
                .setTitle(R.string.title_about)
                .setView(dialogView)
                .setPositiveButton("GOTCHA", { _, _ -> })
                .create()
        dialog.show()

    }
}
