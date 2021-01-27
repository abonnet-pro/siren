package exam.abonnet.sirene.model

import androidx.room.Dao
import androidx.room.Query
import exam.abonnet.sirene.model.data.CodeNaf

@Dao
interface CodeNafDAO
{
    @Query("SELECT * FROM codeNaf")
    fun getAllCodeNaf(): List<CodeNaf>

    @Query("SELECT * FROM CodeNaf WHERE description LIKE '%' || :description || '%'")
    fun getNafByDescription(description: String): List<CodeNaf>

    @Query("SELECT * FROM CodeNaf WHERE description = :description")
    fun getCodeNaf(description: String): CodeNaf?
}