package exam.abonnet.sirene.model.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey
    (
        entity = Company::class,
        parentColumns = ["id"],
        childColumns = ["idCompany"],
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey
    (
    entity = Research::class,
    parentColumns = ["id"],
    childColumns = ["idResearch"],
    onDelete = ForeignKey.CASCADE
    )
])
data class Link(@PrimaryKey(autoGenerate = true) var id:Long? = null,
                var idCompany:Long,
                var idResearch:Long)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Link

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}