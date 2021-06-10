package com.nagase.nagasho.myapplayout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class RecyclerAdapter(var goals: kotlin.collections.MutableList<String>, var targets: kotlin.collections.MutableList<String>, var themes: kotlin.collections.MutableList<String>, var habitnumbers:kotlin.collections.MutableList<String>, var results:kotlin.collections.MutableList<String>) : RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>(){

    class RecyclerViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val goaldataText = view.goaldataText
        val targetdataText = view.targetdataText
        val themedataText = view.themedatacardText
        val habitnumberdataText=view.habitnumberdataText
        val resultText=view.resultText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = layoutInflater.inflate(R.layout.list_item, parent, false)
        return RecyclerViewHolder(item)
    }

    override fun getItemCount(): Int = goals.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.view.let{
            //it.sampleImg.setImageResource(R.mipmap.ic_launcher_round)
            it.goaldataText.text = goals[position]
            it.targetdataText.text = targets[position]
            it.themedatacardText.text = themes[position]
            it.habitnumberdataText.text=habitnumbers[position]
            it.resultText.text = results[position]

        }
    }
}