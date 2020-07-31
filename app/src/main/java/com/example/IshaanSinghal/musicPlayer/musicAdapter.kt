package com.example.IshaanSinghal.musicPlayer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.IshaanSinghal.musicplayerskeleton.R

class MusicAdapter(private var playlist:MutableList<Music>, private var itemClicked : ItemClicked):RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var artist:TextView
        var song:TextView
        var duration:TextView
        init {
            artist=itemView.findViewById(R.id.songArtist)
            song=itemView.findViewById(R.id.songName)
            duration=itemView.findViewById(R.id.playTime)
            itemView.setOnClickListener(this)
        }

        override fun onClick(parent: View?) {
            itemClicked.itemClicked(adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup,viewInt: Int): ViewHolder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.rec_items,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var music= playlist[position]
        holder.artist.text=music.artistName
        holder.song.text=music.songName
        holder.duration.text=
            timeFormat(music.duartion)
    }
}