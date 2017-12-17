package com.jaus.albertogiunta.readit.viewPresenter.linksHome

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.utils.SystemUtils
import com.jaus.albertogiunta.readit.viewPresenter.base.BaseActivity
import kotlinx.android.synthetic.main.activity_links.*
import kotlinx.android.synthetic.main.dialog_manual_input.view.*
import kotlinx.android.synthetic.main.item_link.view.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.indeterminateProgressDialog


class LinksActivity : BaseActivity<LinksContract.View, LinkPresenterImpl>(), LinksContract.View {

    override var presenter: LinkPresenterImpl = LinkPresenterImpl()
    private lateinit var urlFetchingWaitDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)

        // UI initialization
        val itemOnClick: (View, Int, Int) -> Unit = { view, _, _ -> browse(view.tvUrl.text.toString()) }
        val itemOnLongClick: (View, Int, Int) -> Unit = { view, _, _ -> getLinkURLWithManualInput(view.tvUrl.text.toString()) }

        fabAdd.setOnClickListener { getLinkURLWithManualInput(Link.EMPTY_LINK) }

//        btnAddLinkManually.setOnClickListener { getLinkURLWithManualInput(Link.EMPTY_LINK) }
//        btnAddLinkFromClipboard.setOnClickListener { getLinkURLFromClipboard() }

        // LIST initialization
        rvLinks.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rvLinks.adapter = LinkAdapter(presenter.linkList, itemOnClick, itemOnLongClick)
        updateLinkListUI()

        // DIALOG initialization
        urlFetchingWaitDialog = indeterminateProgressDialog(message = "Imma fetch all the info for ya", title = "Noice, you got a new link!")
        urlFetchingWaitDialog.cancel()

        // INTENT initialization
        getLinkURLFromIntent()
    }

    //////////////////// LOADING
    override fun startLoadingState() = urlFetchingWaitDialog.show()

    override fun stopLoadingState() = urlFetchingWaitDialog.cancel()

    //////////////////// LINK INTERACTION
    override fun updateLinkListUI() {
        runOnUiThread {
            rvLinks.adapter.notifyDataSetChanged()
        }
    }

    private fun addLink(url: String) {
        presenter.onLinkAdditionRequest(url)
    }

    private fun getLinkURLWithManualInput(url: String = Link.EMPTY_LINK) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_manual_input, null)
        val builder = AlertDialog.Builder(getContext())

        if (url != Link.EMPTY_LINK) dialogView.etUrl.setText(url, TextView.BufferType.EDITABLE)

        val dialog = builder.setTitle("Put a new link inside of me")
                .setView(dialogView)
                .setPositiveButton("Add", { _, _ -> addLink(dialogView.etUrl.text.toString()) })
                .setNeutralButton("Copy from clipboard", { _, _ -> getLinkURLFromClipboard() })
                .setNegativeButton("Cancel", { _, _ -> })
                .create()

        dialog.show()
    }

    private fun getLinkURLFromClipboard() {
        val url: String? = SystemUtils.getURLFromClipboard(getContext())
        // i.e. url is null right after a reboot
        if (url != null) addLink(url) else this.showError("No Link found in clipboard")
    }

    private fun getLinkURLFromIntent() {
        with(intent) {
            if (Intent.ACTION_SEND == action && type != null && "text/plain" == type) {
                getStringExtra(Intent.EXTRA_TEXT)?.let { addLink(it) }
            }
        }
    }
}
