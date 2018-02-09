package com.jaus.albertogiunta.readit.viewPresenter.links

import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.viewPresenter.base.BasePresenter
import com.jaus.albertogiunta.readit.viewPresenter.base.BaseView

object LinksContract {

    interface View : BaseView {

        fun startLoadingState()

        fun stopLoadingState()

        fun showContent(showContent: Boolean)

        fun completelyRedrawList()

        fun updateLinkListUI()

        fun updateNotification()

        fun launchBrowser(link: Link)

        fun launchShare(link: Link)

        fun displayUpdateDialog(link: Link)

        fun displayNewLinkDialog()

        fun toggleSeenLinks(displaySeenLink: Boolean)

        fun toggleCardLayoutMenuItems()
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

        fun onSeenToggleRequest()

//        fun onCheckRewardExpirationRequest()

        fun rewardUser()

        fun onCardToggleRequest(cardLayout: CardLayout)

        fun shouldShowLinkList(): Boolean
    }

}