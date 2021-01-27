package exam.abonnet.sirene.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class CodeNaf(@PrimaryKey var codeNAFAPE: String,
              var description: String,
              var section: Int,
              var descriptionSection: String): Serializable
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CodeNaf

        if (codeNAFAPE != other.codeNAFAPE) return false

        return true
    }

    override fun hashCode(): Int {
        return codeNAFAPE.hashCode()
    }

    override fun toString(): String {
        return description
    }
}