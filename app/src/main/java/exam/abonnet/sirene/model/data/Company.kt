package exam.abonnet.sirene.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Company(@PrimaryKey(autoGenerate = true) var id:Long? = null,
                   var idApi:Long? = null,
                   var companyName:String = "",
                   var activity: String = "",
                   var adress: String = "",
                   var postalCode: String = "",
                   var department:String = "",
                   var dateStartActivity: String = "",
                   var sirenNumber: String = "",
                   var siretNumber:String = "",
                   var status: String = "",
                   var archive:Boolean = false): Serializable
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Company

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "$companyName ($department)"
    }
}