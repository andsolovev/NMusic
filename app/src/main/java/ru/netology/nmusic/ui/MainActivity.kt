package ru.netology.nmusic.ui

import android.media.AudioAttributes
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmusic.databinding.ActivityMainBinding
import ru.netology.nmusic.dto.Track
import ru.netology.nmusic.viewmodel.TrackViewModel
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import ru.netology.nmusic.BuildConfig
import ru.netology.nmusic.utils.MediaLifecycleObserver
import ru.netology.nmusic.R
import ru.netology.nmusic.adapter.MusicAdapter
import ru.netology.nmusic.adapter.OnInteractionListener
import ru.netology.nmusic.utils.time

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaObserver: MediaLifecycleObserver
    private lateinit var track: Track
    private val viewModel: TrackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mediaObserver = MediaLifecycleObserver()

        val adapter = MusicAdapter(object : OnInteractionListener {
            override fun onPlay(track: Track) {
                playOrPause(track)
            }
        })

        binding.trackPanel.adapter = adapter

        with(binding) {
            buttonPlay.setOnClickListener {
                playOrPause(viewModel.data.value?.tracks!![viewModel.nowPlaying.value!! - 1])
            }
            buttonNext.setOnClickListener { playNext() }
            buttonPrev.setOnClickListener { playPrev() }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaObserver.player?.seekTo(progress)
                binding.timeStart.text = time(mediaObserver.player?.currentPosition!!)
                binding.timeEnd.text = time(mediaObserver.player?.duration!!)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        viewModel.data.observe(this) {
            adapter.submitList(it.tracks)
            with(binding) {
                artistTitle.text = it.artist
                albumTitle.text = it.title
                year.text = it.published
                genre.text = it.genre
            }
            binding.buttonPlay.setImageResource(
                if (viewModel.isNotPlaying()) {
                    R.drawable.baseline_play_arrow_48
                } else {
                    R.drawable.baseline_pause_24
                }
            )
        }

        lifecycle.addObserver(mediaObserver)
    }

    fun playOrPause(track: Track) {
        if (track.isPlaying) {
            mediaObserver.onStateChanged(this@MainActivity, Lifecycle.Event.ON_PAUSE)
            viewModel.isPaused(track.id)
        } else if (track.isPaused) {
            mediaObserver.player?.start()
            viewModel.isPlaying(track.id)
        } else {
            mediaObserver.onStateChanged(this@MainActivity, Lifecycle.Event.ON_STOP)
            mediaObserver.apply {
                player?.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                player?.setDataSource("${BuildConfig.BASE_URL}${track.file}")
            }.play()

            mediaObserver.player?.setOnCompletionListener { playNext() }
            viewModel.isPlaying(track.id)
            initialiseSeekBar()
        }
    }

    private fun playNext() {
        viewModel.data.value?.let {
            track = if (viewModel.nowPlaying.value == it.tracks.size) {
                it.tracks[0]
            } else {
                it.tracks[viewModel.nowPlaying.value!!]
            }
        }
        playOrPause(track)
    }

    private fun playPrev() {
        viewModel.data.value?.let {
            track = if (viewModel.nowPlaying.value == 1) {
                it.tracks[it.tracks.size - 1]
            } else {
                it.tracks[viewModel.nowPlaying.value!! - 2]
            }
        }
        playOrPause(track)
    }

    private fun initialiseSeekBar() {
        val seekBar = binding.seekBar
        seekBar.max = mediaObserver.player!!.duration
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    seekBar.progress = mediaObserver.player?.currentPosition!!
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    seekBar.progress = 0
                }
            }
        }, 0)
    }
}

