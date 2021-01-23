package exam.abonnet.sirene.model

import androidx.room.*
import exam.abonnet.sirene.model.data.Company

@Dao
interface CompanyDAO
{
    @Insert
    fun insert(company: Company): Long

    @Update
    fun update(company: Company)

    @Delete
    fun delete(company: Company)

    @Query("SELECT COUNT(*) FROM Company WHERE idApi = :companyIdApi AND archive = 0")
    fun checkCompanyExist(companyIdApi: Long): Int

    @Query("SELECT id FROM Company WHERE idApi = :companyIdApi AND archive = 0")
    fun getIdCompany(companyIdApi: Long): Long
}