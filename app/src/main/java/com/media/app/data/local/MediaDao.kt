package com.media.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    @Query("SELECT * FROM media_tracks ORDER BY title ASC")
    fun getAllTracksFlow(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_tracks WHERE isOffline = 1")
    suspend fun getAllLocalTracks(): List<MediaEntity>

    @Query("SELECT * FROM media_tracks WHERE id = :id LIMIT 1")
    suspend fun getTrackById(id: String): MediaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<MediaEntity>)

    @Update
    suspend fun updateTrack(track: MediaEntity)

    @Query("DELETE FROM media_tracks WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM media_tracks WHERE id NOT IN (:validIds) AND isOffline = 1")
    suspend fun deleteRemovedLocalTracks(validIds: List<String>)

    @Query("DELETE FROM media_tracks WHERE isOffline = 1")
    suspend fun deleteAllOfflineTracks()

    @Transaction
    suspend fun reconcile(validTracks: List<MediaEntity>) {
        if (validTracks.isEmpty()) {
            deleteAllOfflineTracks()
        } else {
            insertTracks(validTracks)
            val validIds = validTracks.map { it.id }
            deleteRemovedLocalTracks(validIds)
        }
    }
}
