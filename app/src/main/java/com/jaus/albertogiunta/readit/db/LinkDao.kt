package com.jaus.albertogiunta.readit.db

import android.arch.persistence.room.*
import com.jaus.albertogiunta.readit.model.Link

@Dao
interface LinkDao {

    @Query("SELECT * FROM link WHERE id = :arg0")
    fun getLinkById(id: Int): Link

    @Query("SELECT * FROM link ORDER BY id DESC")
    fun getAllLinksFromMostRecent(): List<Link>

    @Insert
    fun insert(vararg repos: Link)

    @Update
    fun update(vararg repos: Link)

    @Delete
    fun delete(vararg repos: Link)

    @Query("DELETE FROM link")
    fun deleteAll()

    @Query("DELETE FROM link WHERE timestamp < :arg0")
    fun deleteAllOlderThan24h(limitTimestamp: Long)

}