package com.jaus.albertogiunta.readit.viewPresenter.main

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.utils.inflate
import kotlinx.android.synthetic.main.item_link.view.*

class LinkAdapter(val items: List<Link>): RecyclerView.Adapter<LinkAdapter.LinkViewHolder>() {

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) = holder.bind(items[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder = LinkViewHolder(parent.inflate(R.layout.item_link))

    override fun getItemCount(): Int = items.size

    class LinkViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(itemLink: Link) {
            with(itemView) {
                tvTitle.text = itemLink.title
                tvUrl.text = itemLink.url
            }
        }

    }
}