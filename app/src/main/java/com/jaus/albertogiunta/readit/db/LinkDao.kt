package com.jaus.albertogiunta.readit.db

import android.arch.persistence.room.*
import com.jaus.albertogiunta.readit.model.Link

@Dao
interface LinkDao {

    @Query("SELECT * FROM link")
    fun getAllLinks(): List<Link>

    @Insert
    fun insert(vararg repos: Link)

    @Update
    fun update(vararg repos: Link)

    @Delete
    fun delete(vararg repos: Link)

    @Query("DELETE FROM link")
    fun deleteAll()

}