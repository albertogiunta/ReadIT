package com.jaus.albertogiunta.readit.viewPresenter.linksHome

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.utils.*
import kotlinx.android.synthetic.main.item_link_1.view.*
import kotlinx.android.synthetic.main.section_blank_item_bottom_space.view.*
import kotlinx.android.synthetic.main.section_link_options.view.*

class LinkAdapter(private val items: List<Link>,
                  private val onClickListener: (View, Int, Int) -> Unit,
                  private val onLongClickListener: (View, Int, Int) -> Unit) : RecyclerView.Adapter<LinkAdapter.LinkViewHolder>() {

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) = holder.bind(items[position], position == items.size - 1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val holder = LinkViewHolder(parent.inflate(R.layout.item_link_2))
        holder.onClick(onClickListener)
        holder.onLongClick(onLongClickListener)
        return holder
    }

    override fun getItemCount(): Int = items.size

    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(itemLink: Link, isLast: Boolean) {
            with(itemView) {
                tvTitle.text = itemLink.title
                tvUrl.text = itemLink.url
                tvTimeLeft.text = itemLink.timestamp.getRemainingTime().toLiteralString(false)
                ivFavicon.loadFavicon(itemLink.faviconURL())
                clCard.background = if (itemLink.seen) context.getDrawable(R.drawable.shape_border_cardview_seen) else context.getDrawable(R.drawable.shape_border_cardview_unseen)
                clEditButtons.gone()
                if (isLast) blankBottomSpace.visible() else blankBottomSpace.gone()
            }
        }
    }
}