package com.jaus.albertogiunta.readit.viewPresenter.main

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_link.view.*
import org.joda.time.DateTime
import org.joda.time.Period

class LinkAdapter(private val items: List<Link>,
                  private val onClickListener: (View, Int, Int) -> Unit,
                  private val onLongClickListener: (View, Int, Int) -> Unit) : RecyclerView.Adapter<LinkAdapter.LinkViewHolder>() {

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) = holder.bind(items[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val holder = LinkViewHolder(parent.inflate(R.layout.item_link))
        holder.onClick(onClickListener)
        holder.onLongClick(onLongClickListener)
        return holder
    }

    override fun getItemCount(): Int = items.size

    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(itemLink: Link) {
            with(itemView) {
                tvTitle.text = itemLink.title
                tvUrl.text = itemLink.url
                val timeString = Period(itemLink.timestamp.plusDays(1), DateTime.now()).toHHmm()
                tvTimeLeft.text = "$timeString left"

                try {
                    Picasso.with(context)
                            .load("https://${SystemUtils.getHost(itemLink.url)}/favicon.ico")
                            .placeholder(R.drawable.ic_placeholder)
                            .into(ivFav)
                } catch (e: Exception) {
                    println("ERROR in PICASSO!!! $e")
                }
            }
        }
    }
}