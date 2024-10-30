package com.todokanai.musicplayer.components.service

import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.todokanai.musicplayer.components.receiver.MusicReceiver
import com.todokanai.musicplayer.di.MyApplication.Companion.appContext
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.player.CustomPlayer
import com.todokanai.musicplayer.servicemodel.MediaSessionCallback
import com.todokanai.musicplayer.servicemodel.MyAudioFocusChangeListener
import com.todokanai.musicplayer.tools.Notifications
import com.todokanai.musicplayer.variables.Variables
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat(){

    companion object{
        val serviceIntent = Intent(appContext,MusicService::class.java)
    }

    private val receiver by lazy{MusicReceiver()}

    @Inject
    lateinit var audioManager: AudioManager

    @Inject
    lateinit var audioFocusChangeListener: MyAudioFocusChangeListener

    @Inject
    lateinit var mediaSession:MediaSessionCompat

    @Inject
    lateinit var notifications:Notifications

    @Inject
    lateinit var player:CustomPlayer

    override fun onCreate() {
        super.onCreate()
        Variables.isServiceOn = true
        setLateinits()

        registerReceiver(receiver, IntentFilter(Constants.ACTION_REPLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_PREVIOUS), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_PAUSE_PLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_NEXT), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SHUFFLE), RECEIVER_NOT_EXPORTED)

        player.apply{
            initAttributes(
               // initialMusicNew,
                this@MusicService
            )

            /** case 1**/
            beginObserve(mediaSession,{startForegroundService(serviceIntent)})

            /** case 2 **/
           // beginObserve2(mediaSession,{startForegroundService(serviceIntent)})
            // Todo: 어느 쪽이 더 나은 방식인지?
        }       // observe LiveData
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        if (clientPackageName == packageName) {
            return BrowserRoot(Constants.BROWSER_ROOT_ID, null)
        }
        return null
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(mutableListOf())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notifications.updateNotification(
            service = this,
            intent = intent,
            isPlaying = player.isPlayingHolder.value,
            isLooping = player.isLoopingHolder.value,
            isShuffled = player.isShuffledHolder.value,
            currentMusic = player.currentMusicHolder.value
        )
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onDestroy() {
        player.stop()
        audioManager.abandonAudioFocus(audioFocusChangeListener)
        mediaSession.release()
        Variables.isServiceOn = false
        super.onDestroy()
    }

    fun setLateinits(){
        mediaSession.apply {
            setCallback(
                MediaSessionCallback(
                    this@MusicService,
                    this,
                    audioManager,
                    audioFocusChangeListener
                )
            )
            this@MusicService.sessionToken = sessionToken
        }
    }
}