package com.example.musicplayer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.os.Handler
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.graphics.toColorInt
import android.animation.ArgbEvaluator
import android.os.*
import java.io.*
import android.view.GestureDetector
import android.view.MotionEvent
import android.content.Intent
import kotlin.rem
import kotlin.text.get
import android.view.GestureDetector.SimpleOnGestureListener
import android.content.Context
import android.animation.ObjectAnimator
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var songList: ListView
    private lateinit var songTitle: TextView
    private lateinit var songArtist: TextView
    private lateinit var btnPlay: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var btnLyrics: Button
    private lateinit var lyricsContainer: ScrollView
    private lateinit var lyricsText: TextView
    private lateinit var songProgress: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var animatedBackground: View
    private var backgroundAnimator: ValueAnimator? = null
    private lateinit var songCard: View
    private lateinit var gestureDetector: GestureDetector
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

    private var currentColorSet = 0
    private var songs: ArrayList<Song> = ArrayList()
    private var currentPosition = 0
    private var isPlaying = false
    private val handler = Handler()
    private val REQUEST_RECORD_AUDIO = 101
    private val REQUEST_SPEECH_RECOGNITION = 102
    private var isRecordingLyrics = false
    private val colorSets = listOf(
        intArrayOf("#FF6B6B".toColorInt(), "#4ECDC4".toColorInt()),
        intArrayOf("#4776E6".toColorInt(), "#8E54E9".toColorInt()),
        intArrayOf("#F857A6".toColorInt(), "#FF5858".toColorInt()),
        intArrayOf("#43C6AC".toColorInt(), "#191654".toColorInt())
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize animated background
        animatedBackground = findViewById(R.id.animatedBackground)
        startBackgroundAnimation()


        // Initialize views
        initViews()

        gestureDetector = GestureDetector(this, SwipeGestureListener())

        // Set touch listener for the song card
        songCard.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        // Check and request permissions
        checkPermissions()

        // Setup media player
        mediaPlayer = MediaPlayer()

        // Setup buttons
        setupButtons()

        // Update song progress
        updateSongProgress()
    }

    inner class SwipeGestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null || e2 == null) return false

            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            if (abs(diffX) > abs(diffY) &&
                abs(diffX) > SWIPE_THRESHOLD &&
                abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                if (diffX > 0) {
                    // Swipe right - previous song
                    if (currentPosition > 0) {
                        currentPosition--
                        playSong()
                        animateSwipe(false)
                    }
                } else {
                    // Swipe left - next song
                    if (currentPosition < songs.size - 1) {
                        currentPosition++
                        playSong()
                        animateSwipe(true)
                    }
                }
                return true
            }
            return false
        }
    }

    private fun animateSwipe(isLeftSwipe: Boolean) {
        val anim = ObjectAnimator.ofFloat(
            songCard,
            "translationX",
            if (isLeftSwipe) -100f else 100f,
            0f
        ).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
        }
        anim.start()
    }

    private fun startBackgroundAnimation() {
        backgroundAnimator?.cancel()

        val nextColorSet = (currentColorSet + 1) % colorSets.size
        backgroundAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(),
            colorSets[currentColorSet][0],
            colorSets[currentColorSet][1],
            colorSets[nextColorSet][0],
            colorSets[nextColorSet][1]
        ).apply {
            duration = 8000
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE

            addUpdateListener { animator ->
                val color = animator.animatedValue as Int
                val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    intArrayOf(color, colorSets[currentColorSet][1])
                )
                gradientDrawable.cornerRadius = 0f
                animatedBackground.background = gradientDrawable
            }
        }

        backgroundAnimator?.start()
        currentColorSet = nextColorSet
    }


    private fun initViews() {
        songList = findViewById(R.id.songList)
        songTitle = findViewById(R.id.songTitle)
        songArtist = findViewById(R.id.songArtist)
        btnPlay = findViewById(R.id.btnPlay)
        btnNext = findViewById(R.id.btnNext)
        btnPrev = findViewById(R.id.btnPrev)
        btnLyrics = findViewById(R.id.btnLyrics)
        lyricsContainer = findViewById(R.id.lyricsContainer)
        lyricsText = findViewById(R.id.lyricsText)
        songProgress = findViewById(R.id.songProgress)
        currentTime = findViewById(R.id.currentTime)
        totalTime = findViewById(R.id.totalTime)
        songCard = findViewById(R.id.songCard)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                100
            )
        } else {
            loadSongs()
        }
    }

    private fun loadSongs() {
        val musicResolver = contentResolver
        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor = musicResolver.query(
            musicUri,
            null,
            MediaStore.Audio.Media.IS_MUSIC + "!= 0",
            null,
            null
        )

        if (musicCursor != null && musicCursor.moveToFirst()) {
            val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val pathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            do {
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist = musicCursor.getString(artistColumn)
                val thisPath = musicCursor.getString(pathColumn)
                songs.add(Song(thisTitle, thisArtist, thisPath))
            } while (musicCursor.moveToNext())

            musicCursor.close()
        }

        // Set adapter for song list
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            songs.map { it.title })
        songList.adapter = adapter

        songList.setOnItemClickListener { _, _, position, _ ->
            currentPosition = position
            playSong()
        }
    }

    private fun setupButtons() {
        btnPlay.setOnClickListener {
            if (isPlaying) {
                pauseSong()
            } else {
                playSong()
            }
        }

        btnNext.setOnClickListener {
            if (currentPosition < songs.size - 1) {
                currentPosition++
                playSong()
            } else {
                Toast.makeText(this, "No more songs", Toast.LENGTH_SHORT).show()
            }
        }

        btnPrev.setOnClickListener {
            if (currentPosition > 0) {
                currentPosition--
                playSong()
            } else {
                Toast.makeText(this, "This is the first song", Toast.LENGTH_SHORT).show()
            }
        }

        btnLyrics.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO
                )
            } else {
                generateLyrics()
            }
        }
    }

    private fun playSong() {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(songs[currentPosition].path)
            mediaPlayer.prepare()
            mediaPlayer.start()
            isPlaying = true
            btnPlay.setImageResource(R.drawable.ic_pause)

            // Update UI
            songTitle.text = songs[currentPosition].title
            songArtist.text = songs[currentPosition].artist

            // Set total time
            val duration = mediaPlayer.duration
            totalTime.text = millisecondsToTime(duration)

            // Show toast
            Toast.makeText(this, "Playing: ${songs[currentPosition].title}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pauseSong() {
        mediaPlayer.pause()
        isPlaying = false
        btnPlay.setImageResource(R.drawable.ic_play)
    }

    private fun updateSongProgress() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition
                    songProgress.max = mediaPlayer.duration
                    songProgress.progress = currentPosition
                    currentTime.text = millisecondsToTime(currentPosition)
                }
                handler.postDelayed(this, 1000)
            }
        }, 1000)

        songProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun generateLyrics() {
        if (isRecordingLyrics) {
            // Already recording, stop and process
            stopLyricsRecording()
            return
        }

        lyricsText.text = "Analyzing audio for lyrics..."
        lyricsContainer.visibility = View.VISIBLE
        btnLyrics.text = "Stop Analysis"
        isRecordingLyrics = true

        val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Sing or speak the lyrics")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        try {
            startActivityForResult(intent, REQUEST_SPEECH_RECOGNITION)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not available", Toast.LENGTH_SHORT).show()
            resetLyricsUI()
        }
    }

    private fun stopLyricsRecording() {
        // In a real app, you might stop the recognition service here
        resetLyricsUI()
    }

    private fun resetLyricsUI() {
        isRecordingLyrics = false
        btnLyrics.text = "Generate Lyrics"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SPEECH_RECOGNITION) {
            if (resultCode == RESULT_OK && data != null) {
                val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!results.isNullOrEmpty()) {
                    val recognizedText = results[0]
                    formatLyrics(recognizedText)
                }
            } else {
                lyricsText.text = "Could not recognize lyrics. Try again in a quiet environment."
            }
            resetLyricsUI()
        }
    }

    private fun formatLyrics(rawText: String) {
        // Simple formatting - in a real app you might do more sophisticated processing
        val formattedLyrics = buildString {
            append("🎤 Generated Lyrics 🎤\n\n")
            append("Song: ${songs[currentPosition].title}\n")
            append("Artist: ${songs[currentPosition].artist}\n\n")

            // Add some basic formatting
            val lines = rawText.split(".")
            for (line in lines) {
                if (line.trim().isNotEmpty()) {
                    append("🎵 ${line.trim()}\n")
                }
            }
        }

        lyricsText.text = formattedLyrics
    }

    private fun millisecondsToTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadSongs()
                } else {
                    Toast.makeText(this, "Permission needed to read songs", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    generateLyrics()
                } else {
                    Toast.makeText(this, "Permission needed for lyrics", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }

    data class Song(val title: String, val artist: String, val path: String)
}