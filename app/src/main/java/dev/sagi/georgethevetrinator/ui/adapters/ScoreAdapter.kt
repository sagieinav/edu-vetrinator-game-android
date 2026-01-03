package dev.sagi.georgethevetrinator.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.sagi.georgethevetrinator.R
import dev.sagi.georgethevetrinator.model.entities.ScoreRecord
import com.google.android.material.textview.MaterialTextView

class ScoreAdapter(
    private val scores: List<ScoreRecord>,
    private val onItemClicked: (ScoreRecord) -> Unit
) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRank: MaterialTextView = view.findViewById(R.id.tv_record_rank)
        val tvName: MaterialTextView = view.findViewById(R.id.tv_record_name)
        val tvScore: MaterialTextView = view.findViewById(R.id.tv_record_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_score_record, parent, false)
        return ScoreViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val record = scores[position]
        holder.tvRank.text = "#${position + 1}"
        holder.tvName.text = record.playerName
        holder.tvScore.text = record.score.toString()
        holder.itemView.setOnClickListener { onItemClicked(record) }
    }

    override fun getItemCount() = scores.size
}