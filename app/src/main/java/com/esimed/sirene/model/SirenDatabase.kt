package com.esimed.sirene.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.esimed.sirene.model.data.Company
import com.esimed.sirene.model.data.Link
import com.esimed.sirene.model.data.Research
import java.text.SimpleDateFormat
import java.util.*

@Database(entities = [Company::class, Research::class, Link::class], version = 1)
abstract class SirenDatabase : RoomDatabase()
{
    abstract fun companyeDAO() : CompanyDAO
    abstract fun researchDAO() : ResearchDAO
    abstract fun linkDAO() : LinkDAO

    companion object
    {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
        val sdfCompany = SimpleDateFormat("yyyyMMdd", Locale.FRENCH)
        var INSTANCE: SirenDatabase? = null

        fun getDatabase(context: Context): SirenDatabase
        {
            if (INSTANCE == null)
            {
                INSTANCE = Room
                    .databaseBuilder(context, SirenDatabase::class.java, "siren.db")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }
    }
}