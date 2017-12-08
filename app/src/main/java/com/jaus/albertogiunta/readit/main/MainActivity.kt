package com.jaus.albertogiunta.readit.main

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.jaus.albertogiunta.readit.MyApplication
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.model.Link
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.joda.time.DateTime

class MainActivity : Activity() {

    private lateinit var dao: LinkDao
    private val linkList = mutableListOf<Link>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dao = MyApplication.database.linkDao()
        linkList.addAll(dao.getAllLinks())

        rvLinks.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rvLinks.adapter = LinkAdapter(linkList)

        btnAddLink.setOnClickListener {
            doAsync {
                val newLink = Link(title = "sample title", url = "${DateTime.now()}")
                dao.insert(newLink)
                linkList.add(newLink)
                runOnUiThread { rvLinks.adapter.notifyDataSetChanged() }
            }
        }

    }
}
