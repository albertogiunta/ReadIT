package com.jaus.albertogiunta.readit.viewPresenter.settings

import com.jaus.albertogiunta.readit.viewPresenter.base.BasePresenter
import com.jaus.albertogiunta.readit.viewPresenter.base.BaseView

interface SettingsContract {

    interface View : BaseView

    interface Presenter : BasePresenter<View>

}