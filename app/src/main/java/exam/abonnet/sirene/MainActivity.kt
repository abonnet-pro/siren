package exam.abonnet.sirene

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import exam.abonnet.sirene.model.*
import exam.abonnet.sirene.model.data.Company
import exam.abonnet.sirene.model.data.Link
import exam.abonnet.sirene.model.data.Research
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity()
{

    private lateinit var researchDAO: ResearchDAO
    private lateinit var companyDAO: CompanyDAO
    private lateinit var linkDAO: LinkDAO

    inner class QueryCompanyTask(private val svc:SirenService,
                                  private val listCompanySearch: ListView,
                                  private val progressBar: ProgressBar,
                                  private val textNoResult: TextView,
                                 private val textQuery: String
    ): AsyncTask<String, Void, List<Company>>()
    {
        override fun doInBackground(vararg params: String?): List<Company>?
        {
            val query = params[0] ?: return emptyList()

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
            val dateRequest = sdf.format(Date())
            val research = Research(request = query, dateRequest = dateRequest, textQuery = textQuery)
            val searchId = researchDAO.insert(research)

            val listCompany = svc.getCompany(query)

            for(company in listCompany)
            {
                val companyId = company.id ?: continue
                if(companyDAO.checkCompanyExist(companyId) == 0) companyDAO.insert(company)
                val link = Link(idCompany = companyId, idResearch = searchId)
                linkDAO.insert(link)
            }

            return listCompany
        }

        override fun onPreExecute()
        {
            listCompanySearch.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            textNoResult.visibility = View.INVISIBLE
        }

        override fun onPostExecute(result: List<Company>?)
        {
            listCompanySearch.adapter = ArrayAdapter<Company>(applicationContext,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                result!!)
            listCompanySearch.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
            textNoResult.visibility = View.INVISIBLE

            if(result.isEmpty())
            {
                textNoResult.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = SirenDatabase.getDatabase(this)
        researchDAO = db.researchDAO()
        companyDAO = db.companyeDAO()
        linkDAO = db.linkDAO()

        val progressBar = findViewById<ProgressBar>(R.id.progressBarSearchCompany)
        val textNoResult = findViewById<TextView>(R.id.textNoResult)
        val buttonSearchCompany = findViewById<ImageButton>(R.id.buttonSearchCompany)
        val editSearchCompany = findViewById<EditText>(R.id.editSearchCompany)
        val listCompanySearch = findViewById<ListView>(R.id.listCompanySearch)

        val svc = SirenService()

        buttonSearchCompany.setOnClickListener {
            val textQuery = editSearchCompany.text.toString()
            val query = String.format(SirenService.queryUrlCompany, textQuery)
            val researchId = researchDAO.checkRequestExist(query)
            if(researchId != null)
            {
                val listCompany = researchDAO.getCompanyByResearch(researchId)

                listCompanySearch.adapter = ArrayAdapter<Company>(applicationContext,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    listCompany
                )
                listCompanySearch.visibility = View.VISIBLE

                if(listCompany.isEmpty())
                {
                    textNoResult.visibility = View.VISIBLE
                }
            }
            else
            {
                QueryCompanyTask(svc, listCompanySearch, progressBar, textNoResult, textQuery).execute(query)
            }
        }
    }
}