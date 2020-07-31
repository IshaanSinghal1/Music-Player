package com.example.IshaanSinghal.musicPlayer

import android.content.pm.PackageManager
import android.database.Cursor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.SeekBar
import android.widget.SeekBar.*
import android.widget.Toast
import com.example.IshaanSinghal.musicplayerskeleton.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    ItemClicked {
    companion object {
        private const val REQUEST_CODE_READ_EXTERNAL = 1
    }

    private var mediaPlayer: MediaPlayer? = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build()
        )
    }
    private lateinit var musicList: MutableList<Music>
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: MusicAdapter
    private var currentPosition: Int = 0
    private var initial = false
    override fun itemClicked(position: Int) {
        initial = true
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(this@MainActivity, Uri.parse(musicList[position].songUri))
        mediaPlayer?.prepare()
        play()
        this.currentPosition = position
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        musicList = mutableListOf()
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions()
        }
        play_pause.setOnClickListener {
            if (!initial) {
                initial = true
                mediaPlayer?.setDataSource(
                    this@MainActivity,
                    Uri.parse(musicList[currentPosition].songUri)
                )
                mediaPlayer?.prepare()
            }
            play()
        }
        play_previous.setOnClickListener {
            if(initial){
                mediaPlayer?.reset()
                if(currentPosition>0){
                    --currentPosition
                }else{
                    currentPosition=musicList.size-1
                }
                mediaPlayer?.setDataSource(
                    this@MainActivity,
                    Uri.parse(musicList[currentPosition].songUri)
                )
                mediaPlayer?.prepare()
                play()
            }
        }
        play_next.setOnClickListener {
            nextSong()
        }
        seek_bar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (fromUser){
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        mediaPlayer?.setOnCompletionListener {
            nextSong()
        }
    }

    private fun play() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer?.pause()
            play_pause.setImageDrawable(resources.getDrawable(R.drawable.ic_play_arrow, null))
        } else {
            mediaPlayer?.start()
            play_pause.setImageDrawable(resources.getDrawable(R.drawable.ic_pause, null))
        }
        val handler=Handler()
        this.runOnUiThread(object : Runnable{
            override fun run() {
                    val playpos=mediaPlayer?.currentPosition
                    val totalpos=mediaPlayer?.duration
                    seek_bar.max= totalpos!!
                    seek_bar.progress=playpos!!

                    current_time.text=
                        timeFormat(playpos.toLong())
                    total_time.text=
                        timeFormat((totalpos - playpos).toLong())
                    handler.postDelayed(this,1000)
                }
        })
    }

    private fun getSongs() {
        val selection = MediaStore.Audio.Media.IS_MUSIC
        val project = arrayOf(
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            project,
            selection,
            null,
            null
        )
        while (cursor!!.moveToNext()) {
            musicList.add(
                Music(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3)
                )
            )
        }
        cursor.close()
        layoutManager = LinearLayoutManager(this)
        adapter =
            MusicAdapter(musicList, this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getSongs()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Toast.makeText(
                    this,
                    "Music Player Needs To Access Storage Files",
                    Toast.LENGTH_SHORT
                ).show()
            }
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_EXTERNAL
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_READ_EXTERNAL -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Load Songs
                getSongs()
            } else {
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    fun nextSong(){
        if(initial){
            mediaPlayer?.reset()
            if(currentPosition<musicList.size-1){
                ++currentPosition
            }else{
                currentPosition=0
            }
            mediaPlayer?.setDataSource(
                this@MainActivity,
                Uri.parse(musicList[currentPosition].songUri)
            )
            mediaPlayer?.prepare()
            play()
        }
    }
}



