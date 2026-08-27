package com.media.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.media.app.domain.model.MediaTrack

@Entity(tableName = "media_tracks")
data class MediaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String,
    val durationSeconds: Long,
    val localPath: String?,
    val isOffline: Boolean,
    val dateModified: Long
) {
    fun toDomain(): MediaTrack {
        return MediaTrack(
            id = id,
            title = title,
            artist = artist,
            thumbnailUrl = thumbnailUrl,
            durationSeconds = durationSeconds,
            sourceUrl = localPath,
            isOffline = isOffline,
            localPath = localPath
        )
    }
}
