package com.madkit.myapplication.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.madkit.myapplication.R
import com.madkit.myapplication.activities.TaskListActivity
import com.madkit.myapplication.firebase.FirestoreClass
import com.madkit.myapplication.models.Task
import org.w3c.dom.Text

class TaskListItemAdapter(private val context: Context, private var list: ArrayList<Task>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            if (position == list.size - 1) {
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.llTaskItem.visibility = View.GONE
            } else {
                holder.tvAddTaskList.visibility = View.GONE
                holder.llTaskItem.visibility = View.VISIBLE
            }
            holder.tvTaskListTitle.text = model.title
            holder.tvAddTaskList.setOnClickListener {
                holder.tvAddTaskList.visibility = View.GONE
                holder.cvAddTaskListName.visibility = View.VISIBLE
            }
            holder.ibCloseListName.setOnClickListener {
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.cvAddTaskListName.visibility = View.GONE
            }
            holder.ibDoneListName.setOnClickListener {
                val listName = holder.etTaskListName.text.toString()
                if (listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.createTaskList(listName)
                    }
                }else {
                    Toast.makeText(context, "Please Enter List Name", Toast.LENGTH_SHORT).show()
                }
            }
            holder.ibEditListName.setOnClickListener {
                holder.etEditTaskListName.setText(model.title)
                holder.llTitleView.visibility = View.GONE
                holder.cvEditTasListName.visibility = View.VISIBLE
            }
            holder.ibCloseEditableView.setOnClickListener {
                holder.llTitleView.visibility = View.VISIBLE
                holder.cvEditTasListName.visibility = View.GONE
            }
            holder.ibDoneEditListName.setOnClickListener {
                val listName = holder.etEditTaskListName.text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position, listName, model)
                    }
                }else {
                    Toast.makeText(context, "Please Enter List Name", Toast.LENGTH_SHORT).show()
                }
            }
            holder.ibDeleteList.setOnClickListener {
                alertDialogForDeleteList(position, model.title)
            }

            holder.tvAddCard.setOnClickListener {
                holder.tvAddCard.visibility = View.GONE
                holder.cvAddCard.visibility = View.VISIBLE
            }
            holder.ibCloseCardName.setOnClickListener {
                holder.tvAddCard.visibility = View.VISIBLE
                holder.cvAddCard.visibility = View.GONE
            }

            holder.ibDoneCardName.setOnClickListener {
                val cardName = holder.etCardName.text.toString()
                if(cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.addCardToTask(position, cardName)
                    }
                }else {
                    Toast.makeText(context, "Please Enter Card Name", Toast.LENGTH_SHORT).show()
                }
            }
            holder.rvCardList.layoutManager = LinearLayoutManager(context)
            holder.rvCardList.setHasFixedSize(true)
            val adapter = CardListItemsAdapter(context, model.cards)
            holder.rvCardList.adapter = adapter
        }
    }

    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun alertDialogForDeleteList(position: Int, title:String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){dialogInterface, which->
            dialogInterface.dismiss()
            if(context is TaskListActivity){
                context.deleteTasList(position)
            }
        }
        builder.setNegativeButton("No"){dialogInterface, which->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvAddTaskList = view.findViewById<TextView>(R.id.tv_add_task_list)
        var llTaskItem = view.findViewById<LinearLayout>(R.id.ll_task_item)
        var llTitleView = view.findViewById<LinearLayout>(R.id.ll_title_view)
        var tvTaskListTitle = view.findViewById<TextView>(R.id.tv_task_list_title)
        var cvAddTaskListName = view.findViewById<CardView>(R.id.cv_add_task_list_name)
        var ibCloseListName = view.findViewById<ImageButton>(R.id.ib_close_list_name)
        var ibDoneListName = view.findViewById<ImageButton>(R.id.ib_done_list_name)
        var etTaskListName = view.findViewById<EditText>(R.id.et_task_list_name)
        var etEditTaskListName = view.findViewById<EditText>(R.id.et_edit_task_list_name)
        var ibEditListName = view.findViewById<ImageButton>(R.id.ib_edit_list_name)
        var ibCloseEditableView = view.findViewById<ImageButton>(R.id.ib_close_editable_view)
        var cvEditTasListName = view.findViewById<CardView>(R.id.cv_edit_task_list_name)
        var ibDoneEditListName = view.findViewById<ImageButton>(R.id.ib_done_edit_list_name)
        var ibDeleteList = view.findViewById<ImageButton>(R.id.ib_delete_list)
        var tvAddCard = view.findViewById<TextView>(R.id.tv_add_card)
        var cvAddCard = view.findViewById<CardView>(R.id.cv_add_card)
        var ibCloseCardName = view.findViewById<ImageButton>(R.id.ib_close_card_name)
        var ibDoneCardName = view.findViewById<ImageButton>(R.id.ib_done_card_name)
        var etCardName = view.findViewById<EditText>(R.id.et_card_name)
        var rvCardList = view.findViewById<RecyclerView>(R.id.rv_card_list)


    }
}