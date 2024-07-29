package com.todokanai.musicplayer.repository

import com.todokanai.musicplayer.data.room.CurrentMusicDao
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.data.room.MusicDao
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(private val musicDao:MusicDao,private val cMusicDao:CurrentMusicDao){

    val getAll = musicDao.getAll()

    val currentMusic = cMusicDao.getCurrentMusic().map { it?.toMusic() }

    suspend fun getAllNonFlow() = musicDao.getAllNonFlow()

    suspend fun insert(music: Music) = musicDao.insert(music)

    suspend fun deleteAll() = musicDao.deleteAll()

    suspend fun delete(music: Music) = musicDao.delete(music)

    suspend fun currentMusicNonFlow() = cMusicDao.getCurrentNonFlow()?.toMusic()

    suspend fun upsertCurrentMusic(currentMusic: Music){
        cMusicDao.deleteAll()
        cMusicDao.insert(currentMusic.toCurrentMusic())
    }
}