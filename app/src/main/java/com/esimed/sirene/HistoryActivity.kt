package com.esimed.sirene

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.esimed.sirene.model.CompanyDAO
import com.esimed.sirene.model.LinkDAO
import com.esimed.sirene.model.ResearchDAO
import com.esimed.sirene.model.SirenDatabase
import com.esimed.sirene.model.data.Research

class HistoryActivity : AppCompatActivity()
{
    private lateinit var researchDAO: ResearchDAO
    private lateinit var companyDAO: CompanyDAO
    private lateinit var linkDAO: LinkDAO
    private lateinit var listRecent: ListView
    private lateinit var listPrevious: ListView
    private lateinit var buttonRecent: ImageButton
    private lateinit var buttonPrevious: ImageButton

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val db = SirenDatabase.getDatabase(this)
        researchDAO = db.researchDAO()
        companyDAO = db.companyeDAO()
        linkDAO = db.linkDAO()

        listRecent = findViewById(R.id.listRecent)
        listPrevious = findViewById(R.id.listPrevious)
        buttonRecent = findViewById(R.id.buttonRecent)
        buttonPrevious = findViewById(R.id.buttonPrevious)

        val listRecentResearch = researchDAO.getAllRecentResearch()
        listRecent.adapter = ResearchHistoryAdapter(this, R.layout.history_list, listRecentResearch)

        listRecent.setOnItemClickListener{ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val research = listRecent.getItemAtPosition(i) as Research
            val returnIntent = Intent()
            returnIntent.putExtra("research", research)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        val listPreviousResearch = researchDAO.getAllPreviousResearch()
        listPrevious.adapter = ResearchHistoryAdapter(this, R.layout.history_list, listPreviousResearch)

        listPrevious.setOnItemClickListener{ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val research = listPrevious.getItemAtPosition(i) as Research
            val returnIntent = Intent()
            returnIntent.putExtra("research", research)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        listRecent.setOnItemLongClickListener { parent, view, position, id ->
            val research = listRecent.getItemAtPosition(position) as Research

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Archiver la recherche ?")
            builder.setCancelable(true)
            builder.setPositiveButton("Oui") { _: DialogInterface, _: Int ->
                research.archive = true
                researchDAO.update(research)
                val listRecentSearch = researchDAO.getAllRecentResearch()
                listRecent.adapter = ResearchHistoryAdapter(this, R.layout.history_list, listRecentSearch)
                val listPreviousSearch = researchDAO.getAllPreviousResearch()
                listPrevious.adapter = ResearchHistoryAdapter(this, R.layout.history_list, listPreviousSearch)
            }
            builder.setNegativeButton("Non") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            val dialog = builder.create()
            dialog.show()

            true
        }

        findViewById<ImageButton>(R.id.buttonRecent).setOnClickListener {
            if(listRecent.adapter != null)
            {
                listRecent.adapter = null
                buttonRecent.background = getDrawable(android.R.drawable.arrow_up_float)
            }
            else
            {
                val listRecentSearch = researchDAO.getAllRecentResearch()
                listRecent.adapter = ResearchHistoryAdapter(this, R.layout.history_list, listRecentSearch)
                buttonRecent.background = getDrawable(android.R.drawable.arrow_down_float)
            }
        }

        findViewById<TextView>(R.id.label_recent).setOnClickListener {
            if(listRecent.adapter != null)
            {
                listRecent.adapter = null
                buttonRecent.background = getDrawable(android.R.drawable.arrow_up_float)
            }
            else
            {
                val listRecentSearch = researchDAO.getAllRecentResearch()
                listRecent.adapter = ResearchHistoryAdapter(this, R.layout.history_list, listRecentSearch)
                buttonRecent.background = getDrawable(android.R.drawable.arrow_down_float)
            }
        }

        findViewById<ImageButton>(R.id.buttonPrevious).setOnClickListener {
            if(listPrevious.adapter != null)
            {
                listPrevious.adapter = null
                buttonPrevious.background = getDrawable(android.R.drawable.arrow_up_float)
            }
            else
            {
                val listPreviousSearch = researchDAO.getAllPreviousResearch()
                listPrevious.adapter = ResearchHistoryAdapter(this, R.layout.history_list, listPreviousSearch)
                buttonPrevious.background = getDrawable(android.R.drawable.arrow_down_float)
            }
        }

        findViewById<TextView>(R.id.label_previous).setOnClickListener {
            if(listPrevious.adapter != null)
            {
                listPrevious.adapter = null
                buttonPrevious.background = getDrawable(android.R.drawable.arrow_up_float)
            }
            else
            {
                val listPreviousSearch = researchDAO.getAllPreviousResearch()
                listPrevious.adapter = ResearchHistoryAdapter(this, R.layout.history_list, listPreviousSearch)
                buttonPrevious.background = getDrawable(android.R.drawable.arrow_down_float)
            }
        }
    }
}