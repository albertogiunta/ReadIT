package com.jaus.albertogiunta.readit.viewPresenter.linksList

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.utils.*
import kotlinx.android.synthetic.main.item_link_1.view.*
import kotlinx.android.synthetic.main.section_empty_bottom_spacing.view.*
import org.jetbrains.anko.textColor

class LinksListAdapter(private val items: List<Link>,
                       private val onClickListener: (View, Int, Int) -> Unit,
                       private val onLongClickListener: (View, Int, Int) -> Unit) : RecyclerView.Adapter<LinksListAdapter.LinkViewHolder>() {

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) = holder.bind(items[position], position == items.size - 1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val holder = LinkViewHolder(parent.inflate(R.layout.item_link_1))
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
                clEditButtonsLayout.gone()
                if (!itemLink.seen) {
                    clCard.background = context.getDrawable(R.drawable.shape_border_cardview_unseen)
                    tvTitle.textColor = ContextCompat.getColor(context, R.color.cardTxtPrimary)
                } else {
                    clCard.background = context.getDrawable(R.drawable.shape_border_cardview_seen)
                    tvTitle.textColor = ContextCompat.getColor(context, R.color.cardTxtPrimarySeen)
                }
                if (isLast) {
                    blankBottomSpace.visible()
                    blankBottomSpace.setOnClickListener {  }
                } else blankBottomSpace.gone()
            }
        }
    }
}

enum class CardLayout(val id: Int, val layout: Int, val action: Int) {

    CARD1(1, R.layout.item_link_1, 0),
    CARD2(2, R.layout.item_link_2, 0)

}