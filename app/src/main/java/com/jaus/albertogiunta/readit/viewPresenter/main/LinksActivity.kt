package com.jaus.albertogiunta.readit.viewPresenter.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.EditText
import android.widget.LinearLayout
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.utils.SystemUtils
import com.jaus.albertogiunta.readit.viewPresenter.base.BaseActivity
import kotlinx.android.synthetic.main.activity_links.*
import org.jetbrains.anko.*


class LinksActivity : BaseActivity<LinksContract.View, LinkPresenterImpl>(), LinksContract.View {

    override var presenter: LinkPresenterImpl = LinkPresenterImpl()
    private lateinit var urlFetchingWaitDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)

        urlFetchingWaitDialog = indeterminateProgressDialog(message = "Imma fetch all the info for ya", title = "Noice, you got a new link!")
        urlFetchingWaitDialog.cancel()

        // UI initialization
        rvLinks.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rvLinks.adapter = LinkAdapter(presenter.linkList)

        btnAddLinkManually.setOnClickListener { getLinkURLWithManualInput() }
        btnAddLinkFromClipboard.setOnClickListener { getLinkURLFromClipboard() }

        getLinkURLFromIntent()
    }

    //////////////////// LOADING
    override fun startLoadingState() = urlFetchingWaitDialog.show()

    override fun stopLoadingState() = urlFetchingWaitDialog.cancel()

    //////////////////// LINK INTERACTION
    override fun updateLinkListUI() {
        runOnUiThread { rvLinks.adapter.notifyDataSetChanged() }
    }

    private fun addLink(url: String) {
        presenter.onLinkAdditionRequest(url)
    }

    private fun getLinkURLWithManualInput() {
        alert {
            customView {
                var et: EditText? = null
                verticalLayout {
                    et = editText {
                        hint = "Write your link HERE"
                    }
                }
                positiveButton("Add") {
                    addLink(et?.text.toString())
                }
                negativeButton("Cancel") {}
            }
        }.show()
    }

    private fun getLinkURLFromClipboard() {
        addLink(SystemUtils.getURLFromClipboard(getContext()))
    }

    private fun getLinkURLFromIntent() {
        with(intent) {
            if (Intent.ACTION_SEND == action && type != null && "text/plain" == type) {
                getStringExtra(Intent.EXTRA_TEXT)?.let { addLink(it) }
            }
        }
    }
}
