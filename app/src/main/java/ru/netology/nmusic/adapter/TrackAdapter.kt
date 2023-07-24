package ru.netology.nmusic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmusic.R
import ru.netology.nmusic.databinding.ItemMusicBinding
import ru.netology.nmusic.dto.Track

interface OnInteractionListener {
    fun onPlay(track: Track)
}

class MusicAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Track, TrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track)
    }
}


class TrackViewHolder(
    private val binding: ItemMusicBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(track: Track) {
        with(binding.listTitle) {
            text = String.format("Track ${track.file}")
            setTextColor(
                if (track.isPlaying || track.isSelected) {
                    ContextCompat.getColor(this.context, R.color.orange_text)
                } else {
                    ContextCompat.getColor(this.context, R.color.semi_white)
                }
            )
        }

        with(binding.playButton) {
            setOnClickListener { onInteractionListener.onPlay(track) }
            setImageResource(
                if (!track.isPlaying) {
                    R.drawable.baseline_play_arrow_48
                } else {
                    R.drawable.baseline_pause_24
                }
            )
        }
    }
}

class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean =
        oldItem == newItem
}
