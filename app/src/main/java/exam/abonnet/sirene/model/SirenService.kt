package exam.abonnet.sirene.model

import android.graphics.BitmapFactory
import android.util.JsonReader
import android.util.JsonToken
import exam.abonnet.sirene.model.data.Company
import java.io.IOException
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class SirenService
{
    companion object
    {
        val apiUrl = "https://entreprise.data.gouv.fr/api/sirene/v1/full_text"
        val queryUrlCompany = "$apiUrl/%s"
    }

    fun getCompany(query: String): List<Company>
    {
        val url = URL(query)
        var conn: HttpsURLConnection? = null
        try
        {
            conn = url.openConnection() as HttpsURLConnection
            conn.connect()
            val code = conn.responseCode
            if (code != HttpsURLConnection.HTTP_OK)
            {
                return emptyList()
            }
            val inputStream = conn.inputStream ?: return emptyList()
            val reader = JsonReader(inputStream.bufferedReader())
            val results = mutableListOf<Company>()

            reader.beginObject()
            while(reader.hasNext())
            {
                if(reader.nextName() == "etablissement")
                {
                    reader.beginArray()
                    while(reader.hasNext())
                    {
                        reader.beginObject()
                        val company = Company()
                        while(reader.hasNext())
                        {
                            when(reader.nextName())
                            {
                                "id" -> if(reader.peek() == JsonToken.NULL) reader.skipValue() else company.idApi = reader.nextInt().toLong()
                                "siren" -> if(reader.peek() == JsonToken.NULL) reader.skipValue() else company.sirenNumber = reader.nextString()
                                "siret" -> if(reader.peek() == JsonToken.NULL) reader.skipValue() else company.siretNumber = reader.nextString()
                                "nom_raison_sociale" -> if(reader.peek() == JsonToken.NULL) reader.skipValue() else company.companyName = reader.nextString()
                                "departement" -> if(reader.peek() == JsonToken.NULL) reader.skipValue() else company.department = reader.nextString()
                                "libelle_activite_principale_entreprise" -> if(reader.peek() == JsonToken.NULL) reader.skipValue() else company.activity = reader.nextString()
                                "geo_adresse" -> if(reader.peek() == JsonToken.NULL) reader.skipValue() else company.adress = reader.nextString()
                                "code_postal" -> if(reader.peek() == JsonToken.NULL) reader.skipValue() else company.postalCode = reader.nextString()
                                "date_debut_activite" -> if(reader.peek() == JsonToken.NULL) reader.skipValue() else company.dateStartActivity = reader.nextString()
                                "libelle_nature_juridique_entreprise" -> if(reader.peek() == JsonToken.NULL) reader.skipValue() else company.status = reader.nextString()
                                else -> reader.skipValue()
                            }
                        }
                        reader.endObject()
                        results.add(company)
                    }
                    reader.endArray()
                }
                else
                {
                    reader.skipValue()
                }
            }
            reader.endObject()

            return results
        }
        catch (e: IOException)
        {
            return emptyList();
        }
        finally
        {
            conn?.disconnect()
        }
    }
}