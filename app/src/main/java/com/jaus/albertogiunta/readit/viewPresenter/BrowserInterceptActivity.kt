package com.jaus.albertogiunta.readit.viewPresenter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jaus.albertogiunta.readit.viewPresenter.links.LinksActivity

class BrowserInterceptActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val action = intent.action

        if (Intent.ACTION_VIEW == action) {
            val dataString = intent.dataString

            if (dataString.isBlank()) {
                return
            } else {
                saveToReadIT(dataString)
            }
        }

        finishAffinity()
    }

    private fun saveToReadIT(dataString: String) {
        val intent = Intent(this@BrowserInterceptActivity, LinksActivity::class.java)
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, dataString)
        startActivity(intent)
        finish()
    }

//    private fun showError() {
//        Toast.makeText(this@BrowserInterceptActivity, "Error opening in Pocket", Toast.LENGTH_LONG).show()
//    }

}