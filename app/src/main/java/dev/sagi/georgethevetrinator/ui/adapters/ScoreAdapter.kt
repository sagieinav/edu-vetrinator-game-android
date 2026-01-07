package dev.sagi.georgethevetrinator.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.sagi.georgethevetrinator.R
import dev.sagi.georgethevetrinator.model.entities.ScoreRecord
import com.google.android.material.textview.MaterialTextView
import dev.sagi.georgethevetrinator.databinding.ItemScoreRecordBinding

class ScoreAdapter(
    private val scores: List<ScoreRecord>,
    private val onItemClicked: (ScoreRecord) -> Unit
) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    class ScoreViewHolder(val binding: ItemScoreRecordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val binding = ItemScoreRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScoreViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val record = scores[position]

        holder.binding.apply {
            tvRecordRank.text = "#${position + 1}"
            tvRecordName.text = record.playerName
            tvRecordScore.text = record.score.toString()
            root.setOnClickListener { onItemClicked(record) }
        }
    }

    override fun getItemCount() = scores.size
}