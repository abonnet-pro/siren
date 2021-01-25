package exam.abonnet.sirene

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import exam.abonnet.sirene.model.*
import exam.abonnet.sirene.model.data.Company
import exam.abonnet.sirene.model.data.Link
import exam.abonnet.sirene.model.data.Research
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity()
{
    private lateinit var researchDAO: ResearchDAO
    private lateinit var companyDAO: CompanyDAO
    private lateinit var linkDAO: LinkDAO

    private lateinit var progressBar: ProgressBar
    private lateinit var textNoResult: TextView
    private lateinit var buttonSearchCompany: ImageButton
    private lateinit var editSearchCompany: EditText
    private lateinit var listCompanySearch: ListView
    private lateinit var buttonReconnection: Button
    private lateinit var editPostal: EditText
    private lateinit var editDepartment: EditText
    private lateinit var svc: SirenService

    private val LAUNCH_HISTORY_ACTIVITY = 1

    inner class QueryCompanyTask(private val svc:SirenService,
                                  private val listCompanySearch: ListView,
                                  private val progressBar: ProgressBar,
                                  private val textNoResult: TextView,
                                 private val research: Research
    ): AsyncTask<String, Void, List<Company>>()
    {
        override fun doInBackground(vararg params: String?): List<Company>?
        {
            val query = params[0] ?: return emptyList()
            val searchId = researchDAO.insert(research)
            val listCompany = svc.getCompany(query)

            for(company in listCompany)
            {
                val companyIdApi = company.idApi ?: continue
                var companyId: Long
                companyId = if(companyDAO.checkCompanyExist(companyIdApi) == 0)
                {
                    companyDAO.insert(company)
                }
                else
                {
                    companyDAO.getIdCompany(companyIdApi)
                }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_HISTORY_ACTIVITY)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                val research = data?.getSerializableExtra("research") as Research
                val researchId = research.id ?: return
                editSearchCompany.setText(research.textQuery)
                editDepartment.text.clear()
                editPostal.text.clear()
                if(!research.department.isEmpty()) editDepartment.setText(research.department)
                if(!research.postCode.isEmpty()) editPostal.setText(research.postCode)

                val listCompany = researchDAO.getCompanyByResearch(researchId)

                listCompanySearch.adapter = ArrayAdapter<Company>(
                    applicationContext,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    listCompany
                )
                listCompanySearch.visibility = View.VISIBLE
                textNoResult.visibility = View.INVISIBLE

                if (listCompany.isEmpty())
                {
                    textNoResult.visibility = View.VISIBLE
                }
            }
            if (resultCode == Activity.RESULT_CANCELED)
            {
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
        svc = SirenService()

        progressBar = findViewById(R.id.progressBarSearchCompany)
        textNoResult = findViewById(R.id.textNoResult)
        buttonSearchCompany = findViewById(R.id.buttonSearchCompany)
        editSearchCompany = findViewById(R.id.editSearchCompany)
        listCompanySearch = findViewById(R.id.listCompanySearch)
        buttonReconnection = findViewById(R.id.buttonReconnection)
        editDepartment = findViewById(R.id.editDepartment)
        editPostal = findViewById(R.id.editPostal)

        checkInternetConnection()
        checkMemoryResearch()

        buttonSearchCompany.setOnClickListener {
            if(!checkInternetConnection()) return@setOnClickListener
            launchResearch()
        }

        listCompanySearch.setOnItemClickListener{ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            intent = Intent(this@MainActivity, DetailCompanyActivity::class.java)
            val company = listCompanySearch.getItemAtPosition(i) as Company
            intent.putExtra("company", company)
            startActivity(intent)
        }

        buttonReconnection.setOnClickListener {
            checkInternetConnection()
        }

        editPostal.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?)
            {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                editDepartment.text.clear()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        editDepartment.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?)
            {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                editPostal.text.clear()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun launchResearch() {
        val textQuery = editSearchCompany.text.toString()
        val postalQuery = editPostal.text.toString()
        val departmentQuery = editDepartment.text.toString()

        if (textQuery.isEmpty()) {
            listCompanySearch.adapter = null
            textNoResult.visibility = View.VISIBLE
            return
        } else {
            textNoResult.visibility = View.INVISIBLE
        }

        val query = String.format(SirenService.queryUrl, textQuery, postalQuery, departmentQuery)
        val researchId = researchDAO.checkRequestExist(query)
        if (researchId != null)
        {
            val listCompany = researchDAO.getCompanyByResearch(researchId)

            listCompanySearch.adapter = ArrayAdapter<Company>(
                applicationContext,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                listCompany
            )
            listCompanySearch.visibility = View.VISIBLE
            textNoResult.visibility = View.INVISIBLE

            if (listCompany.isEmpty())
            {
                textNoResult.visibility = View.VISIBLE
            }
        } else {
            val dateRequest = SirenDatabase.sdf.format(Date())
            val research = Research(
                request = query,
                dateRequest = dateRequest,
                textQuery = textQuery,
                postCode = postalQuery,
                department = departmentQuery
            )

            QueryCompanyTask(svc, listCompanySearch, progressBar, textNoResult, research).execute(
                query
            )
        }
    }

    private fun checkInternetConnection(): Boolean
    {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null)
        {
            val networkState = networkInfo.state
            if (networkState.compareTo(NetworkInfo.State.CONNECTED) != 0)
            {
                AlertDialog.Builder(this)
                    .setTitle(R.string.connection)
                    .setMessage(R.string.no_connection)
                    .setPositiveButton(R.string.retry) { dialogInterface: DialogInterface, i: Int ->
                        checkInternetConnection()
                    }
                    .setNegativeButton(R.string.close, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()

                buttonReconnection.visibility = View.VISIBLE
                editSearchCompany.isEnabled = false
                buttonSearchCompany.isEnabled = false
                return false
            }
            else
            {
                buttonReconnection.visibility = View.INVISIBLE
                editSearchCompany.isEnabled = true
                buttonSearchCompany.isEnabled = true
                return true
            }
        }
        else
        {
            AlertDialog.Builder(this)
                .setTitle(R.string.connection)
                .setMessage(R.string.no_connection)
                .setPositiveButton(R.string.retry) { dialogInterface: DialogInterface, i: Int ->
                    checkInternetConnection()
                }
                .setNegativeButton(R.string.close, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

            buttonReconnection.visibility = View.VISIBLE
            editSearchCompany.isEnabled = false
            buttonSearchCompany.isEnabled = false
            return false
        }
    }

    private fun checkMemoryResearch()
    {
        val listResearchActive = researchDAO.getAllResearchActive()
        for (research in listResearchActive)
        {
            if (research.dateRequest < SirenDatabase.sdf.format(Date()))
            {
                research.archive = true
                researchDAO.update(research)

                val listCompanyBySearch = researchDAO.getCompanyByResearch(research.id!!)
                listCompanyBySearch.forEach {
                    it.archive = true
                    companyDAO.update(it)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.action_history ->
            {
                intent = Intent(this@MainActivity, HistoryActivity::class.java)
                startActivityForResult(intent, LAUNCH_HISTORY_ACTIVITY)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}