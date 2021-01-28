package com.esimed.sirene

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.esimed.sirene.model.data.Research

class ResearchHistoryAdapter(private val activity: Context, private val ressource:Int, private val listResearch: List<Research>) :  ArrayAdapter<Research>(activity, ressource, listResearch)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val dateHistory = getItem(position)?.dateRequest
        val requestHistory = getItem(position)?.textQuery
        val departmentHistory = getItem(position)?.department
        val postalHistory = getItem(position)?.postCode
        val nafHistory = getItem(position)?.codeNaf
        val descriptionHistory = getItem(position)?.description

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(ressource, parent, false)

        val dateView = view?.findViewById<TextView>(R.id.date_history)
        val requestView = view?.findViewById<TextView>(R.id.request_history)
        val departmentView = view?.findViewById<TextView>(R.id.departmentHistory)
        val postalView = view?.findViewById<TextView>(R.id.postalHistory)
        val nafView = view?.findViewById<TextView>(R.id.nafHistory)
        val descriptionView = view?.findViewById<TextView>(R.id.activityHistory)

        dateView?.text = if(dateHistory.isNullOrBlank()) "-" else dateHistory
        requestView?.text = if(requestHistory.isNullOrBlank()) "-" else requestHistory
        departmentView?.text = if(departmentHistory.isNullOrBlank()) "-" else departmentHistory
        postalView?.text = if(postalHistory.isNullOrBlank()) "-" else postalHistory
        nafView?.text = if(nafHistory.isNullOrBlank()) "-" else nafHistory
        descriptionView?.text = if(descriptionHistory.isNullOrBlank()) "-" else descriptionHistory

        return view
    }
}