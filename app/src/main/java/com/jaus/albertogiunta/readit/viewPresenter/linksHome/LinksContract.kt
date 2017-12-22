package com.jaus.albertogiunta.readit.viewPresenter.linksHome

import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.viewPresenter.base.BasePresenter
import com.jaus.albertogiunta.readit.viewPresenter.base.BaseView

object LinksContract {

    interface View : BaseView {

        fun startLoadingState()

        fun stopLoadingState()

        fun completelyRedrawList()

        fun updateLinkListUI()

        fun launchBrowser(link: Link)

        fun launchShare(link: Link)

        fun displayUpdateDialog(link: Link)

        fun displayNewLinkDialog()
    }

    interface Presenter : BasePresenter<View> {

        var linkList: MutableList<Link>

        fun onActivityResumed()

        fun onLinkBrowsingRequest(position: Int)

        fun onLinkAdditionRequest(isNew: Boolean, url: String)

        fun onLinkCopyRequest(position: Int)

        fun onLinkRemovalRequest(position: Int)

        fun onLinkSharingRequest(position: Int)

        fun onLinkUpdateRequest(position: Int)
    }

}