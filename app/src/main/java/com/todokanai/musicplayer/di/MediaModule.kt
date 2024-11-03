package com.todokanai.musicplayer.di

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationManagerCompat
import com.todokanai.musicplayer.myobjects.Constants
import com.todokanai.musicplayer.player.CustomPlayer
import com.todokanai.musicplayer.player.PlayerStateHolders
import com.todokanai.musicplayer.servicemodel.MyAudioFocusChangeListener
import com.todokanai.musicplayer.tools.Notifications
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MediaModule {

    @Singleton
    @Provides
    fun provideMyAudioFocusChangeListener():MyAudioFocusChangeListener{
        return MyAudioFocusChangeListener()
    }

    @Singleton
    @Provides
    fun provideMediaSession(@ApplicationContext context:Context):MediaSessionCompat{
        return MediaSessionCompat(context, Constants.MEDIA_SESSION_TAG)
    }

    @Singleton
    @Provides
    fun provideNotifications(notificationManager:NotificationManagerCompat,mediaSession:MediaSessionCompat):Notifications{
        return Notifications(notificationManager,mediaSession)
    }
    @Singleton
    @Provides
    fun provideCustomPlayer(stateHolders:PlayerStateHolders): CustomPlayer {
        return CustomPlayer(stateHolders)
    }

}