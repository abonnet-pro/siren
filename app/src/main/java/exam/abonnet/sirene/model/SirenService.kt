package exam.abonnet.sirene.model

import android.graphics.BitmapFactory
import android.util.JsonReader
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
                                "id" -> company.id = reader.nextInt().toLong()
                                "siren" -> company.sirenNumber = reader.nextString()
                                "siret" -> company.siretNumber = reader.nextString()
                                "nom_raison_sociale" -> company.companyName = reader.nextString()
                                "departement" -> company.department = reader.nextString()
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