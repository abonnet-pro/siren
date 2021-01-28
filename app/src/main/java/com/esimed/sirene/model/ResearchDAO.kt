package com.esimed.sirene.model

import androidx.room.*
import com.esimed.sirene.model.data.Company
import com.esimed.sirene.model.data.Research

@Dao
interface ResearchDAO
{
    @Insert
    fun insert(research: Research): Long

    @Update
    fun update(research: Research)

    @Delete
    fun delete(research: Research)

    @Query("SELECT id FROM Research WHERE request = :request AND archive = 0")
    fun checkRequestExist(request: String): Long?

    @Query("SELECT Company.* FROM company INNER JOIN link ON Company.id = Link.idCompany INNER JOIN Research ON Research.id = Link.idResearch WHERE Research.id = :researchId")
    fun getCompanyByResearch(researchId: Long): List<Company>

    @Query("SELECT * FROM Research WHERE archive = 0")
    fun getAllResearchActive(): List<Research>

    @Query("SELECT * FROM Research WHERE archive = 1")
    fun getAllResearchArchive(): List<Research>

    @Query("SELECT * FROM research WHERE archive = 0 ORDER BY id DESC")
    fun getAllRecentResearch(): List<Research>

    @Query("SELECT * FROM research WHERE archive = 1 ORDER BY id DESC")
    fun getAllPreviousResearch(): List<Research>
}