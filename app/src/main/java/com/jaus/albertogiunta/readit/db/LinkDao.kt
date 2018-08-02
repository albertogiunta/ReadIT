package com.jaus.albertogiunta.readit.db

import android.arch.persistence.room.*
import com.jaus.albertogiunta.readit.model.Link
import org.joda.time.DateTime

@Dao
interface LinkDao {

    @Query("SELECT MAX(id) FROM link")
    fun getMaxId(): Int

    @Query("SELECT * FROM link WHERE id = :id")
    fun getLinkById(id: Int): Link

    @Query("SELECT * FROM link ORDER BY id DESC")
    fun getAllLinksFromMostRecent(): List<Link>

    @Query("SELECT COUNT(*) FROM link WHERE seen = :seen AND timestamp < :beforeTimestamp")
    fun getAllUnseenExpiredLinks(seen: Boolean = false, beforeTimestamp: Long = DateTime.now().minusHours(24).millis): Int

    @Query("SELECT COUNT(*) FROM link WHERE seen = :seen")
    fun getAllSeenLinksCount(seen: Boolean = true): Int

    @Insert
    fun insert(vararg repos: Link)

    @Update
    fun update(vararg repos: Link)

    @Delete
    fun delete(vararg repos: Link)

    @Query("DELETE FROM link")
    fun deleteAll()

    @Query("DELETE FROM link WHERE timestamp < :limitTimestamp")
    fun deleteAllOlderThan24h(limitTimestamp: Long)

}