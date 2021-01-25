package exam.abonnet.sirene.model

import androidx.room.*
import exam.abonnet.sirene.model.data.Company
import exam.abonnet.sirene.model.data.Research

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
}