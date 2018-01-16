package com.jaus.albertogiunta.readit.viewPresenter.settings

import android.os.Bundle
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.viewPresenter.base.BaseActivity

class SettingsActivity : BaseActivity<SettingsContract.View, SettingsPresenterImpl>(), SettingsContract.View {

    override var presenter: SettingsPresenterImpl = SettingsPresenterImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
}