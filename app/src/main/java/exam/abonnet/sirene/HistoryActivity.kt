package exam.abonnet.sirene

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import exam.abonnet.sirene.model.CompanyDAO
import exam.abonnet.sirene.model.LinkDAO
import exam.abonnet.sirene.model.ResearchDAO
import exam.abonnet.sirene.model.SirenDatabase
import exam.abonnet.sirene.model.data.Research

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

        findViewById<ImageButton>(R.id.buttonRecent).setOnClickListener {
            if(listRecent.adapter != null)
            {
                listRecent.adapter = null
                buttonRecent.background = getDrawable(android.R.drawable.arrow_up_float)
            }
            else
            {
                listRecent.adapter = ResearchHistoryAdapter(this, R.layout.history_list, listRecentResearch)
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
                listRecent.adapter = ResearchHistoryAdapter(this, R.layout.history_list, listRecentResearch)
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
                listPrevious.adapter = ResearchHistoryAdapter(this, R.layout.history_list, listPreviousResearch)
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
                listPrevious.adapter = ResearchHistoryAdapter(this, R.layout.history_list, listPreviousResearch)
                buttonPrevious.background = getDrawable(android.R.drawable.arrow_down_float)
            }
        }
    }
}