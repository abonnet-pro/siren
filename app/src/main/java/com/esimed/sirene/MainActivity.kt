package com.esimed.sirene

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import com.esimed.sirene.model.*
import com.esimed.sirene.model.data.CodeNaf
import com.esimed.sirene.model.data.Company
import com.esimed.sirene.model.data.Link
import com.esimed.sirene.model.data.Research
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity()
{
    private lateinit var researchDAO: ResearchDAO
    private lateinit var companyDAO: CompanyDAO
    private lateinit var linkDAO: LinkDAO
    private lateinit var nafDAO: CodeNafDAO

    private lateinit var progressBar: ProgressBar
    private lateinit var textNoResult: TextView
    private lateinit var buttonSearchCompany: ImageButton
    private lateinit var editSearchCompany: EditText
    private lateinit var listCompanySearch: ListView
    private lateinit var buttonReconnection: Button
    private lateinit var editPostal: EditText
    private lateinit var editDepartment: EditText
    private lateinit var editNaf: AutoCompleteTextView
    private lateinit var editActivity: AutoCompleteTextView
    private lateinit var svc: SirenService
    private lateinit var prefs: SharedPreferences

    private val LAUNCH_HISTORY_ACTIVITY = 1
    private val MY_DATA_KEYS_FORM = "myDataForm"
    private val MY_DATA_KEYS_LIST = "myDataList"
    private var myDataForm: HashMap<String, String>? = null
    private var myDataList: ArrayList<Company>? = null
    private val DAY_MAX_ARCHIVE = 90
    private val MILLISECONDS = 1000
    private val SECONDS = 60
    private val MINUTS = 60
    private val HOURS = 24

    inner class QueryCompanyTask(private val svc:SirenService,
                                 private val listCompanySearch: ListView,
                                 private val progressBar: ProgressBar,
                                 private val textNoResult: TextView,
                                 private val research: Research): AsyncTask<String, Void, List<Company>>()
    {
        override fun doInBackground(vararg params: String?): List<Company>?
        {
            val query = params[0] ?: return emptyList()
            val searchId = researchDAO.insert(research)
            val listCompany = svc.getCompany(query)
            if(!listCompany.isNullOrEmpty()) myDataList = listCompany as ArrayList<Company>

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

    inner class QueryActivityTask(): AsyncTask<String, Void, List<CodeNaf>>()
    {
        override fun doInBackground(vararg params: String?): List<CodeNaf>?
        {
            val query = params[0] ?: return emptyList()
            return nafDAO.getNafByDescription(query)
        }

        override fun onPostExecute(result: List<CodeNaf>?)
        {
            val adapter = result?.let {
                ArrayAdapter<CodeNaf>(this@MainActivity,
                    android.R.layout.simple_dropdown_item_1line, it)
            }
            editActivity.setAdapter(adapter)
        }
    }

    inner class QueryCodeNafTask(): AsyncTask<String, Void, List<String>>()
    {
        override fun doInBackground(vararg params: String?): List<String>?
        {
            val query = params[0] ?: return emptyList()
            val listNafCode = nafDAO.getNafCodeByDescription(query)

            return listNafCode
        }

        override fun onPostExecute(result: List<String>?)
        {
            val adapter = result?.let {
                ArrayAdapter<String>(this@MainActivity,
                    android.R.layout.simple_dropdown_item_1line, it)
            }
            editNaf.setAdapter(adapter)
        }
    }

    inner class QueryNafTask(): AsyncTask<String, Void, CodeNaf>()
    {
        override fun doInBackground(vararg params: String?): CodeNaf?
        {
            val query = params[0] ?: return null
            val nafCode = nafDAO.getCodeNaf(query)

            return nafCode
        }

        override fun onPostExecute(result: CodeNaf?)
        {
            editActivity.setText(result?.description)
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
                editNaf.text.clear()
                editActivity.text.clear()

                if(!research.codeNaf.isEmpty()) editNaf.setText(research.codeNaf)
                if(!research.department.isEmpty()) editDepartment.setText(research.department)
                if(!research.postCode.isEmpty()) editPostal.setText(research.postCode)
                if(!research.description.isEmpty()) editActivity.setText(research.description)

                val listCompany = researchDAO.getCompanyByResearch(researchId)
                myDataList = listCompany as ArrayList<Company>

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
        val dbNaf = NafDatabase.getDatabase(this)

        nafDAO = dbNaf.codeNafDAO()
        researchDAO = db.researchDAO()
        companyDAO = db.companyeDAO()
        linkDAO = db.linkDAO()
        svc = SirenService()
        prefs = getPreferences(MODE_PRIVATE)

        initializationView()
        initializationListener()
        checkInternetConnection()
        checkMemoryResearch()
        checkPreferences()
        checkBundle(savedInstanceState)
    }

    private fun initializationView()
    {
        progressBar = findViewById(R.id.progressBarSearchCompany)
        textNoResult = findViewById(R.id.textNoResult)
        buttonSearchCompany = findViewById(R.id.buttonSearchCompany)
        editSearchCompany = findViewById(R.id.editSearchCompany)
        listCompanySearch = findViewById(R.id.listCompanySearch)
        buttonReconnection = findViewById(R.id.buttonReconnection)
        editDepartment = findViewById(R.id.editDepartment)
        editPostal = findViewById(R.id.editPostal)
        editNaf = findViewById(R.id.editCodeNaf)
        editActivity = findViewById(R.id.editActivity)
    }

    private fun initializationListener()
    {
        buttonSearchCompany.setOnClickListener {
            if (!checkInternetConnection()) return@setOnClickListener
            if(!checkEntries()) return@setOnClickListener
            savePreferences()
            launchResearch()
        }

        listCompanySearch.setOnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            intent = Intent(this@MainActivity, DetailCompanyActivity::class.java)
            val company = listCompanySearch.getItemAtPosition(i) as Company
            intent.putExtra("company", company)
            startActivity(intent)
        }

        buttonReconnection.setOnClickListener {
            checkInternetConnection()
        }

        editSearchCompany.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?)
            {
                val textSearchCompany = editSearchCompany.text.toString()
                myDataForm?.set("companyName", textSearchCompany)

                if(textSearchCompany.isEmpty())
                {
                    editNaf.isEnabled = false
                    editActivity.isEnabled = false
                    editPostal.isEnabled = false
                    editDepartment.isEnabled = false
                }
                else
                {
                    editNaf.isEnabled = true
                    editActivity.isEnabled = true
                    editPostal.isEnabled = true
                    editDepartment.isEnabled = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        editActivity.setOnItemClickListener { parent, view, position, id ->
            val naf = parent.getItemAtPosition(position) as CodeNaf
            editNaf.setText(naf.codeNAFAPE)
        }

        editNaf.setOnItemClickListener { parent, view, position, id ->
            val codeNaf = parent.getItemAtPosition(position) as String
            QueryNafTask().execute(codeNaf)
        }

        editActivity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                myDataForm?.set("activity", editNaf.text.toString())
                QueryActivityTask().execute(editActivity.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        editNaf.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                myDataForm?.set("codeNaf", editNaf.text.toString())
                QueryCodeNafTask().execute(editNaf.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        editDepartment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                myDataForm?.set("department", editDepartment.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        editPostal.setOnTouchListener { v, event ->
            editDepartment.text.clear()
            false
        }

        editDepartment.setOnTouchListener { v, event ->
            editPostal.text.clear()
            false
        }

        editPostal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                myDataForm?.set("postal", editPostal.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun checkEntries(): Boolean
    {
        var noError = true

        if(editDepartment.length() == 1)
        {
            editDepartment.background = getDrawable(R.drawable.custom_research_wrong)
            Toast.makeText(this@MainActivity, getString(R.string.wrong_entry), Toast.LENGTH_LONG).show()
            noError = false
        }
        else
        {
            editDepartment.background = getDrawable(R.drawable.selector_edit)
        }

        if(editPostal.length() in 1..4)
        {
            editPostal.background = getDrawable(R.drawable.custom_research_wrong)
            Toast.makeText(this@MainActivity, getString(R.string.wrong_entry), Toast.LENGTH_LONG).show()
            noError = false
        }
        else
        {
            editPostal.background = getDrawable(R.drawable.selector_edit)
        }

        if(editActivity.text.toString().isNotEmpty() && editNaf.text.toString().isEmpty())
        {
            editActivity.background = getDrawable(R.drawable.custom_research_wrong)
            Toast.makeText(this@MainActivity, getString(R.string.wrong_entry), Toast.LENGTH_LONG).show()
            noError = false
        }
        else
        {
            editActivity.background = getDrawable(R.drawable.selector_edit)
        }


        if(editNaf.length() in 1..4)
        {
            editNaf.background = getDrawable(R.drawable.custom_research_wrong)
            Toast.makeText(this@MainActivity, getString(R.string.wrong_entry), Toast.LENGTH_LONG).show()
            noError = false
        }
        else
        {
            editNaf.background = getDrawable(R.drawable.selector_edit)
        }

        return noError
    }

    private fun checkPreferences()
    {
        val companyName = prefs.getString("company_name", "")
        val companyDepartment = prefs.getString("company_department", "")
        val companyPostal = prefs.getString("company_postal", "")
        val companyActivity = prefs.getString("company_activity", "")
        val companyNAF = prefs.getString("company_naf", "")

        editSearchCompany.setText(companyName)
        editDepartment.setText(companyDepartment)
        editPostal.setText(companyPostal)
        editActivity.setText(companyActivity)
        editNaf.setText(companyNAF)
    }

    private fun savePreferences()
    {
        val editor = prefs.edit()
        editor.putString("company_name", editSearchCompany.text.toString())
        editor.putString("company_department", editDepartment.text.toString())
        editor.putString("company_postal", editPostal.text.toString())
        editor.putString("company_activity", editActivity.text.toString())
        editor.putString("company_naf", editNaf.text.toString())
        editor.apply()
    }

    private fun checkBundle(savedInstanceState: Bundle?)
    {
        if (savedInstanceState != null && savedInstanceState.containsKey(MY_DATA_KEYS_FORM) && savedInstanceState.containsKey(MY_DATA_KEYS_LIST))
        {
            myDataForm = savedInstanceState.getSerializable(MY_DATA_KEYS_FORM) as HashMap<String, String>
            myDataList = savedInstanceState.getSerializable(MY_DATA_KEYS_LIST) as ArrayList<Company>

            val name = myDataForm?.get("companyName")
            val post = myDataForm?.get("postal")
            val dep = myDataForm?.get("department")
            val naf = myDataForm?.get("codeNaf")
            val activity = myDataForm?.get("activity")

            if(name != null)
            {
                if(name.isNotEmpty()) editSearchCompany.setText(name)
            }
            if(post != null)
            {
                if(post.isNotEmpty()) editPostal.setText(post)
            }
            if(dep != null)
            {
                if(dep.isNotEmpty()) editDepartment.setText(dep)
            }
            if(naf != null)
            {
                if(naf.isNotEmpty()) editNaf.setText(naf)
            }
            if(activity != null)
            {
                if(activity.isNotEmpty()) editNaf.setText(activity)
            }

            if(!myDataList.isNullOrEmpty())
            {
                listCompanySearch.adapter = ArrayAdapter<Company>(
                    applicationContext,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    myDataList!!
                )
            }
        }
        else
        {
            myDataForm = HashMap()
            myDataList = ArrayList()
        }
    }

    private fun launchResearch() {
        val textQuery = editSearchCompany.text.toString()
        val postalQuery = editPostal.text.toString()
        val departmentQuery = editDepartment.text.toString()
        val nafQuery = editNaf.text.toString()
        val activityQuery = editActivity.text.toString()

        if (textQuery.isEmpty())
        {
            listCompanySearch.adapter = null
            textNoResult.visibility = View.VISIBLE
            myDataList = ArrayList()
            return
        }
        else
        {
            textNoResult.visibility = View.INVISIBLE
        }

        val query = String.format(SirenService.queryUrl, textQuery, postalQuery, departmentQuery, nafQuery)
        val researchId = researchDAO.checkRequestExist(query)
        if (researchId != null)
        {
            val listCompany = researchDAO.getCompanyByResearch(researchId)
            myDataList = listCompany as ArrayList<Company>

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
        else {
            val dateRequest = SirenDatabase.sdf.format(Date())
            val research = Research(
                request = query,
                dateRequest = dateRequest,
                textQuery = textQuery,
                postCode = postalQuery,
                department = departmentQuery,
                codeNaf = nafQuery,
                description = activityQuery
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
                buttonSearchCompany.isClickable = false
                return false
            }
            else
            {
                buttonReconnection.visibility = View.INVISIBLE
                buttonSearchCompany.isClickable = true
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
            buttonSearchCompany.isClickable = false
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

        val listResearchArchive = researchDAO.getAllResearchArchive()
        for(research in listResearchArchive)
        {
            val dat = SirenDatabase.sdf.parse(research.dateRequest)
            val today = Date()
            if(getDayDifference(dat, today) > 90)
            {
                researchDAO.delete(research)
            }
        }
    }

    private fun getDayDifference(firstDate: Date, secondDate: Date): Int
    {
        val firstDateTime = (firstDate.time / MILLISECONDS / SECONDS / MINUTS / HOURS).toInt()
        val secondDateDateTime = (secondDate.time / MILLISECONDS / SECONDS / MINUTS / HOURS).toInt()

        return secondDateDateTime - firstDateTime
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
            R.id.action_delete ->
            {
                editSearchCompany.text.clear()
                editSearchCompany.background = getDrawable(R.drawable.custom_research_enabled)
                myDataForm?.set("companyName", "")
                editDepartment.text.clear()
                editDepartment.background = getDrawable(R.drawable.selector_edit)
                myDataForm?.set("department", "")
                editPostal.text.clear()
                editPostal.background = getDrawable(R.drawable.selector_edit)
                myDataForm?.set("postal", "")
                editNaf.text.clear()
                editNaf.background = getDrawable(R.drawable.selector_edit)
                myDataForm?.set("codeNaf", "")
                editActivity.text.clear()
                editActivity.background = getDrawable(R.drawable.selector_edit)
                myDataForm?.set("activity", "")
                listCompanySearch.adapter = null
                myDataList = ArrayList()
                textNoResult.visibility = View.INVISIBLE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putSerializable(MY_DATA_KEYS_FORM, myDataForm)
        outState.putSerializable(MY_DATA_KEYS_LIST, myDataList)
    }
}