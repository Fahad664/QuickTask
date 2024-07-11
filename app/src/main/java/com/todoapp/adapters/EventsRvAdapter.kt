package com.todoapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.todoapp.R
import com.todoapp.models.ApiResponseModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventsRvAdapter(private val context:Context, private val dataList : ArrayList<ApiResponseModel>):Adapter<EventsRvAdapter.Events>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Events {
        val view = LayoutInflater.from(context).inflate(R.layout.event_rv_item, parent,false)
        return Events(view)
    }

    override fun onBindViewHolder(holder: Events, position: Int) {
        val data = dataList[position]

        /*// Parse the API date into day, date, and month
        val parseDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(data.dateOfEventCreation)
        val day = SimpleDateFormat("E", Locale.getDefault()).format(parseDate)
        val date = SimpleDateFormat("dd", Locale.getDefault()).format(parseDate)
        val month = SimpleDateFormat("MMM", Locale.getDefault()).format(parseDate)*/

        //If you need to perform more complex date calculations, then this approach with the
        //Calendar object might be a better choice...
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd") // Adjust the format according to your API response
        val parseDate = apiDateFormat.parse(data.dateOfEventCreation.toString())
        val calendar = Calendar.getInstance()
        calendar.time = parseDate

        // Format the Calendar's date components as "E", "dd", and "MMM"
        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) // "Tue"
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString() // "3"
        val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) // "Jul"

        with(holder){
            title.text = data.title
            description.text = data.description
            day.text = dayOfWeek
            date.text = dayOfMonth
            month.text = monthName
            cardView.setCardBackgroundColor(when(data.priority) {
                1 -> ContextCompat.getColor(context, R.color.high)
                2 -> ContextCompat.getColor(context, R.color.medium)
                3 -> ContextCompat.getColor(context, R.color.low)   //select yourself define color
                else -> ContextCompat.getColor(context, android.R.color.white)  //select android define color
                //Color.WHITE --> Can also be used
            })
            status.text = data.status.toString()
        }

        /*holder.title.text = dataList[position].title
        holder.description.text = dataList[position].description
        //After parsing, Assign the date into separate text widget
        holder.day.text = dayOfWeek
        holder.date.text = dayOfMonth
        holder.month.text = monthName
        holder.cardView.setCardBackgroundColor(when(data.priority){
            1 -> ContextCompat.getColor(context, R.color.high)
            2 -> ContextCompat.getColor(context, R.color.medium)
            3 -> ContextCompat.getColor(context, R.color.low) //select yourself define color
            else -> ContextCompat.getColor(context, android.R.color.white) //select android define color
            //Color.WHITE --> Can also be used
        })*/

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Events(itemView: View):RecyclerView.ViewHolder(itemView){
        val title : AppCompatTextView = itemView.findViewById(R.id.title)
        val description : AppCompatTextView = itemView.findViewById(R.id.description)
        val day : AppCompatTextView = itemView.findViewById(R.id.day)
        val date : AppCompatTextView = itemView.findViewById(R.id.date)
        val month : AppCompatTextView = itemView.findViewById(R.id.month)
        val status : AppCompatTextView = itemView.findViewById(R.id.status)
        private val popupMenuOption : AppCompatImageView = itemView.findViewById(R.id.options)
        val cardView : CardView = itemView.findViewById(R.id.cardView)

        init {
            //handle the menu options click using interface method for getting item adapter position
            popupMenuOption.setOnClickListener {
                menuClick?.onOptionMenuClicked(it, adapterPosition)
                /*val popupMenu = PopupMenu(context, popupMenuOption)
                popupMenu.inflate(R.menu.item_menu)
                //popupMenu.menuInflater.inflate(R.menu.item_menu,popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item?.itemId) {
                        R.id.cancel -> {
                            status.text = "Cancel"
                        }

                        R.id.update -> {

                        }

                        R.id.completed -> {

                        }
                    }
                    false
                }
                popupMenu.show()*/
            }
        }

    }


    interface MenuOptionClickListener{
        fun onOptionMenuClicked(view: View, position: Int)
    }

    private var menuClick : MenuOptionClickListener? = null

    //function to bind the MenuOptionClickListener
    fun optionsMenuClickListener(itemClick : MenuOptionClickListener){
        this.menuClick = itemClick
    }

}