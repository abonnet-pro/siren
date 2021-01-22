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

    @Query("select Company.* from company inner join link on Company.id = Link.idCompany inner join Research on Research.id = Link.idResearch where Research.id = :researchId")
    fun getCompanyByResearch(researchId: Long): List<Company>

    @Query("SELECT * FROM Research WHERE archive = 0")
    fun getAllResearchActive(): List<Research>
}