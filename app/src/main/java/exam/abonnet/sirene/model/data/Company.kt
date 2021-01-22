package exam.abonnet.sirene.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Company(@PrimaryKey var id:Long? = null,
                   var companyName:String = "",
                   var sirenNumber: String = "",
                   var siretNumber:String = "",
                   var department:String = "",
                   var archive:Boolean = false)
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