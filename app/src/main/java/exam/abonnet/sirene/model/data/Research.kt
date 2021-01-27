package exam.abonnet.sirene.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Research(@PrimaryKey(autoGenerate = true) var id:Long? = null,
                    var request:String = "",
                    var dateRequest: String = "",
                    var textQuery: String ="",
                    var archive:Boolean = false,
                    var department:String = "",
                    var postCode:String = "",
                    var codeNaf: String = "",
                    var description: String = ""): Serializable
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Research

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return if(department == "" && postCode == "")
            "$dateRequest $textQuery"
        else if(department == "")
            "$dateRequest $textQuery ($postCode)"
        else
            "$dateRequest $textQuery ($department)"
    }
}