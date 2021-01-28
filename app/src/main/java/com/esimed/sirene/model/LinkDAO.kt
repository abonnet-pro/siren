package com.esimed.sirene.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.esimed.sirene.model.data.Link

@Dao
interface LinkDAO
{
    @Insert
    fun insert(link: Link): Long

    @Update
    fun update(link: Link)

    @Delete
    fun delete(link: Link)
}