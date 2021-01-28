package com.esimed.sirene.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.esimed.sirene.model.data.CodeNaf

@Database(entities = [CodeNaf::class], version = 1)
abstract class NafDatabase : RoomDatabase()
{
    abstract fun codeNafDAO() : CodeNafDAO

    companion object
    {
        var INSTANCE: NafDatabase? = null

        fun getDatabase(context: Context): NafDatabase
        {
            if (INSTANCE == null)
            {
                INSTANCE = Room
                    .databaseBuilder(context, NafDatabase::class.java, "naf.db")
                    .createFromAsset("database/activityNAF.db")
                    .build()
            }
            return INSTANCE!!
        }
    }
}