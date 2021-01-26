package exam.abonnet.sirene

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import exam.abonnet.sirene.model.data.Research
import org.w3c.dom.Text

class ResearchHistoryAdapter(private val activity: Context, private val ressource:Int, private val listResearch: List<Research>) :  ArrayAdapter<Research>(activity, ressource, listResearch)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val dateHistory = getItem(position)?.dateRequest
        val requestHistory = getItem(position)?.textQuery
        val departmentHistory = getItem(position)?.department
        val postalHistory = getItem(position)?.postCode

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(ressource, parent, false)

        val dateView = view?.findViewById<TextView>(R.id.date_history)
        val requestView = view?.findViewById<TextView>(R.id.request_history)
        val departmentView = view?.findViewById<TextView>(R.id.departmentHistory)
        val postalView = view?.findViewById<TextView>(R.id.postalHistory)

        dateView?.text = if(dateHistory.isNullOrBlank()) "-" else dateHistory
        requestView?.text = if(requestHistory.isNullOrBlank()) "-" else requestHistory
        departmentView?.text = if(departmentHistory.isNullOrBlank()) "-" else departmentHistory
        postalView?.text = if(postalHistory.isNullOrBlank()) "-" else postalHistory

        return view
    }
}