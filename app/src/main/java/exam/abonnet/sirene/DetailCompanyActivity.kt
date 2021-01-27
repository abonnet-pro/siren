package exam.abonnet.sirene

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import exam.abonnet.sirene.model.SirenDatabase
import exam.abonnet.sirene.model.data.Company

class DetailCompanyActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val company = intent.getSerializableExtra("company") as Company

        val company_name_detail = findViewById<TextView>(R.id.company_name_detail)
        val company_adress_detail = findViewById<TextView>(R.id.company_adress_detail)
        val company_activity_detail = findViewById<TextView>(R.id.company_activity_detail)
        val company_date_creation_detail = findViewById<TextView>(R.id.company_date_creation_detail)
        val company_juridical_detail = findViewById<TextView>(R.id.company_juridical_detail)
        val company_siren_detail = findViewById<TextView>(R.id.company_siren_detail)
        val company_siret_detail = findViewById<TextView>(R.id.company_siret_detail)

        company_name_detail.text = String.format(getString(R.string.company_name_detail), company.companyName)
        company_adress_detail.text = String.format(getString(R.string.company_adress_detail), company.adress)
        company_activity_detail.text = String.format(getString(R.string.company_activity_detail), company.activity)
        company_date_creation_detail.text = String.format(getString(R.string.company_date_creation_detail), SirenDatabase.sdf.format(SirenDatabase.sdfCompany.parse(company.dateStartActivity)))
        company_juridical_detail.text = String.format(getString(R.string.company_juridical_detail), company.status)
        company_siren_detail.text = String.format(getString(R.string.company_siren_detail), company.sirenNumber)
        company_siret_detail.text = String.format(getString(R.string.company_siret_detail), company.siretNumber)
    }
}