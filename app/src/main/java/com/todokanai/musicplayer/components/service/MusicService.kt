package com.todokanai.musicplayer.components.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.asLiveData
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.todokanai.musicplayer.R
import com.todokanai.musicplayer.components.receiver.MusicReceiver
import com.todokanai.musicplayer.components.receiver.NoisyReceiver
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.myobjects.LateInitObjects.receiver
import com.todokanai.musicplayer.player.CustomPlayer
import com.todokanai.musicplayer.player.MyMediaSession
import com.todokanai.musicplayer.repository.MusicRepository
import com.todokanai.musicplayer.servicemodel.MediaSessionCallback
import com.todokanai.musicplayer.servicemodel.MyAudioFocusChangeListener
import com.todokanai.musicplayer.tools.Notifications
import com.todokanai.musicplayer.variables.Variables
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat(){
    companion object{
        lateinit var serviceIntent : Intent
        lateinit var customPlayer: CustomPlayer
        lateinit var mediaSession: MyMediaSession
   //     lateinit var mediaController:MediaControllerCompat
    }
    private lateinit var notifications: Notifications
    private lateinit var notificationManager:NotificationManagerCompat
    private lateinit var audioFocusChangeListener:MyAudioFocusChangeListener
    private val noisyReceiver = NoisyReceiver()
    private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val serviceChannel by lazy {
        NotificationChannel(
            Constants.CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_NONE             //  알림의 중요도
        )
    }

    @Inject
    lateinit var dsRepo:DataStoreRepository

    @Inject
    lateinit var musicRepo:MusicRepository

    @Inject
    lateinit var audioManager: AudioManager

    override fun onCreate() {
        super.onCreate()
        Variables.isServiceOn = true
        fun setLateinits(){
            notificationManager = NotificationManagerCompat.from(this@MusicService)
            receiver = MusicReceiver()
            notifications = Notifications(this,Constants.CHANNEL_ID)
            audioFocusChangeListener = MyAudioFocusChangeListener(customPlayer)
       //     mediaController = MediaControllerCompat(this, mediaSession.sessionToken)
        }
        setLateinits()
        mediaSession.apply {
            setCallback(
                MediaSessionCallback(
                    this@MusicService,
                    this,
                    audioManager,
                    audioFocusChangeListener,
                    noisyReceiver,
                    noisyIntentFilter
                )
            )
            this@MusicService.sessionToken = sessionToken
        }

        registerReceiver(receiver, IntentFilter(Constants.ACTION_REPLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_PREVIOUS), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_PAUSE_PLAY), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SKIP_TO_NEXT), RECEIVER_NOT_EXPORTED)
        registerReceiver(receiver, IntentFilter(Constants.ACTION_SHUFFLE), RECEIVER_NOT_EXPORTED)
        registerReceiver(noisyReceiver,noisyIntentFilter, RECEIVER_NOT_EXPORTED)

        customPlayer.apply{
            initAttributes(this@MusicService)
            currentMusicHolder.asLiveData().observeForever(){
                startForegroundService(serviceIntent)
            }
            isShuffledHolder.asLiveData().observeForever(){
                startForegroundService(serviceIntent)
            }
            isLoopingHolder.asLiveData().observeForever(){
                startForegroundService(serviceIntent)
            }
            isPlayingHolder.asLiveData().observeForever(){
                startForegroundService(serviceIntent)
            }
        }       // observe LiveData
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        if (clientPackageName == packageName) {
            return BrowserRoot("MediaSessionExperiment", null)
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
        notificationManager.createNotificationChannel(serviceChannel)
        MediaButtonReceiver.handleIntent(mediaSession,intent)                // 일단 이건 지우지 말고 Keep

        //mediaController
        val notification = mediaSession.noti(this,customPlayer)
        notificationManager.notify(1,notification)
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        customPlayer.stop()
        audioManager.abandonAudioFocus(audioFocusChangeListener)
        mediaSession.release()
        Variables.isServiceOn = false
        super.onDestroy()
    }
}