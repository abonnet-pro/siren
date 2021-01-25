package exam.abonnet.sirene

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
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

        val listRecentResearch = researchDAO.getAllResearchActive()
        listRecent.adapter = ArrayAdapter<Research>(applicationContext,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            listRecentResearch)

        listRecent.setOnItemClickListener{ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val research = listRecent.getItemAtPosition(i) as Research
            val returnIntent = Intent()
            returnIntent.putExtra("research", research)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        val listPreviousResearch = researchDAO.getAllPreviousResearch()
        listPrevious.adapter = ArrayAdapter<Research>(applicationContext,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            listPreviousResearch)

        listPrevious.setOnItemClickListener{ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val research = listPrevious.getItemAtPosition(i) as Research
            val returnIntent = Intent()
            returnIntent.putExtra("research", research)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        findViewById<ImageButton>(R.id.buttonRecent).setOnClickListener {
            if(listRecent.adapter != null) listRecent.adapter = null
            else listRecent.adapter = ArrayAdapter<Research>(applicationContext,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                listRecentResearch)
        }

        findViewById<ImageButton>(R.id.buttonPrevious).setOnClickListener {
            if(listPrevious.adapter != null) listPrevious.adapter = null
            else listPrevious.adapter = ArrayAdapter<Research>(applicationContext,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                listRecentResearch)
        }
    }
}