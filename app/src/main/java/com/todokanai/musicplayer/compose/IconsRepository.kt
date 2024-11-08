package com.todokanai.musicplayer.compose

import com.todokanai.musicplayer.R
import java.io.File

class IconsRepository() {

    private val thumbnailFolder = R.drawable.ic_baseline_folder_24
    private val thumbnailPdf = R.drawable.ic_pdf
    private val thumbnailDefaultFile =R.drawable.ic_baseline_insert_drive_file_24
    fun thumbnail(file: File): Int {
        return if (file.isDirectory) {
            thumbnailFolder
        } else {
            when (file.extension) {
                "pdf" -> { thumbnailPdf }
                else -> { thumbnailDefaultFile }
            }
        }
    }

    val delete = R.drawable.baseline_delete_24
    val prev = R.drawable.baseline_skip_previous_24
    val next = R.drawable.baseline_skip_next_24
    val play = R.drawable.baseline_play_arrow_24
    val pause = R.drawable.baseline_pause_24
    val shuffle = R.drawable.baseline_shuffle_24
    val nonShuffle = R.drawable.baseline_arrow_right_alt_24
    val repeat = R.drawable.baseline_repeat_one_24
    val repeatAll = R.drawable.baseline_repeat_24

    fun pausePlay(isPlaying:Boolean) =
        if(isPlaying){
            pause
        } else{
            play
        }

    fun shuffledImage(isShuffled:Boolean) =
        if(isShuffled){
            shuffle
        } else{
            nonShuffle
        }

    fun loopingImage(isLooping:Boolean) =
        if(isLooping){
            repeat
        } else {
            repeatAll
        }
}