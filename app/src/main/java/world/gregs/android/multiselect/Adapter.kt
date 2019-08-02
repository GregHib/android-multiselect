package world.gregs.android.multiselect

import android.util.SparseBooleanArray
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import world.gregs.android.multiselect.Model.Companion.models

class Adapter(private val listener: AdapterRowListener) : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    private val selectedItems = SparseBooleanArray()
    private var deletedItems = mutableListOf<Model>()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnLongClickListener {
        var title: TextView = view.findViewById(R.id.title)
        var message: TextView = view.findViewById(R.id.description)
        var container: RelativeLayout = view.findViewById(R.id.container)

        init {
            view.setOnLongClickListener(this)
        }

        override fun onLongClick(view: View): Boolean {
            listener.onRowLongClicked(adapterPosition)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = models[position]

        //Display text view data
        holder.title.text = model.text
        holder.message.text = model.description

        //Change the row state to activated
        holder.itemView.isActivated = selectedItems.get(position, false)

        //Apply click events
        applyClickEvents(holder, position)
    }

    private fun applyClickEvents(holder: MyViewHolder, position: Int) {

        holder.container.setOnClickListener {
            if (selectedItems.size() > 0) {
                listener.onRowSelection(position)
            } else {
                listener.onRowAction(position)
            }
        }

        holder.container.setOnLongClickListener { view ->
            listener.onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        }
    }

    override fun getItemId(position: Int): Long {
        return models[position].id.toLong()
    }

    override fun getItemCount(): Int {
        return models.size
    }

    fun toggleSelection(pos: Int) {
        currentSelectedIndex = pos
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos)
        } else {
            selectedItems.put(pos, true)
        }
        notifyItemChanged(pos)
    }

    fun selectionCount(): Int = selectedItems.size()

    fun clearSelections() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<Int> {
        val items = ArrayList<Int>(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    fun removeData(position: Int) {
        val removed = models.removeAt(position)
        deletedItems.add(removed)
        resetCurrentIndex()
    }

    fun clearBackup() {
        deletedItems.clear()
    }

    fun restoreData(): Int {
        val count = deletedItems.size
        models.addAll(deletedItems)
        clearBackup()
        return count
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    interface AdapterRowListener {

        fun onRowAction(position: Int)

        fun onRowSelection(position: Int)

        fun onRowLongClicked(position: Int)
    }

    companion object {
        private var currentSelectedIndex = -1
    }
}