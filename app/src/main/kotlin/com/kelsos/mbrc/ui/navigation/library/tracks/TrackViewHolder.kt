package com.kelsos.mbrc.ui.navigation.library.tracks


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.Group
import androidx.core.view.isVisible
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.extensions.string
import com.kelsos.mbrc.ui.BindableViewHolder
import kotterknife.bindView

class TrackViewHolder(
  itemView: View,
  indicatorPressed: (view: View, position: Int) -> Unit,
  pressed: (view: View, position: Int) -> Unit
) : BindableViewHolder<TrackEntity>(itemView) {
  private val artist: TextView by bindView(R.id.line_two)
  private val title: TextView by bindView(R.id.line_one)
  private val empty: Group by bindView(R.id.listitem_loading)
  private val indicator: ImageView by bindView(R.id.overflow_menu)
  private val unknownArtist: String by lazy { string(R.string.unknown_artist) }

  init {
    indicator.setOnClickListener { indicatorPressed(it, adapterPosition) }
    itemView.setOnClickListener { pressed(it, adapterPosition) }
  }

  override fun clear() {
    empty.isVisible = true
    artist.text = ""
    title.text = ""
  }

  override fun bindTo(item: TrackEntity) {
    empty.isVisible = false
    title.text = item.title
    artist.text = if (item.artist.isBlank()) unknownArtist else item.artist
  }

  companion object {
    fun create(
      parent: ViewGroup,
      indicatorPressed: (view: View, position: Int) -> Unit,
      pressed: (view: View, position: Int) -> Unit
    ): TrackViewHolder {
      val inflater: LayoutInflater = LayoutInflater.from(parent.context)
      val view = inflater.inflate(R.layout.ui_list_dual, parent, false)
      return TrackViewHolder(view, indicatorPressed, pressed)
    }
  }
}