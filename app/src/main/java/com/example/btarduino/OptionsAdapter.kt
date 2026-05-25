package com.example.btarduino

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OptionsAdapter(
    private val items: MutableList<Option>,
    private val onSend: (Option) -> Unit,
    private val onEdit: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<OptionsAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.optionTitle)
        val charText: TextView = v.findViewById(R.id.optionChar)
        val editBtn: ImageButton = v.findViewById(R.id.editBtn)
        val deleteBtn: ImageButton = v.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_option, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.charText.text = holder.itemView.context
            .getString(R.string.char_will_send, item.character)

        holder.itemView.setOnClickListener { onSend(item) }
        holder.editBtn.setOnClickListener {
            val p = holder.bindingAdapterPosition
            if (p != RecyclerView.NO_POSITION) onEdit(p)
        }
        holder.deleteBtn.setOnClickListener {
            val p = holder.bindingAdapterPosition
            if (p != RecyclerView.NO_POSITION) onDelete(p)
        }
    }
}
