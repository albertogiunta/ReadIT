package com.jaus.albertogiunta.readit.main

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.jaus.albertogiunta.readit.MyApplication
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.networking.LinkService
import com.jaus.albertogiunta.readit.networking.NetworkingFactory
import com.jaus.albertogiunta.readit.utils.toJsoupDocument
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_links.*
import org.jetbrains.anko.doAsync

class LinksActivity : Activity() {

    private lateinit var dao: LinkDao
    private val linkList = mutableListOf<Link>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)

        dao = MyApplication.database.linkDao()
        linkList.addAll(dao.getAllLinks())

        rvLinks.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rvLinks.adapter = LinkAdapter(linkList)

        btnAddLink.setOnClickListener {
            doAsync {
                val url = "https://www.google.com"

                NetworkingFactory
                        .createService(LinkService::class.java)
                        .contactWebsite(url)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            run {
                                val doc = result.toJsoupDocument()
                                println(doc.title())
                            }
                        }, { error -> println(error) }
                        )

                val newLink = Link(title = "sample title", url = url)
                dao.insert(newLink)
                linkList.add(newLink)
                runOnUiThread { rvLinks.adapter.notifyDataSetChanged() }
            }
        }
    }
}
