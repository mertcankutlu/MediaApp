package com.media.app.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.SessionToken
import com.media.app.playback.MediaPlaybackService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlaybackModule {

    @Provides
    @Singleton
    fun provideSessionToken(
        @ApplicationContext context: Context
    ): SessionToken {
        return SessionToken(
            context,
            ComponentName(context, MediaPlaybackService::class.java)
        )
    }
}
