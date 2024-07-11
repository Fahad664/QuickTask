package com.todoapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.PopupWindow
import android.widget.Toast
import android.window.OnBackInvokedCallback
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.PopupWindowCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.todoapp.adapters.EventsRvAdapter
import com.todoapp.databinding.FragmentActiveBinding
import com.todoapp.models.ApiResponseModel
import com.todoapp.network_api.ApiResponse
import com.todoapp.network_api.BasePoint
import com.todoapp.utils.NetworkHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class ActiveFragment() : Fragment() {
    private lateinit var binding: FragmentActiveBinding
    private lateinit var apiResponse: ApiResponse
    private var dataList: ArrayList<ApiResponseModel> = arrayListOf()
    lateinit var rvAdapter: EventsRvAdapter
    var selectedItemPosition = 1
    private var isNetworkOk: Boolean = false
    private val tag = "MyTag"
    lateinit var createDialog: AlertDialog

    // Flag to track whether Wi-Fi is enabled
    private var isWifiEnabled: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentActiveBinding.inflate(inflater, container, false)
        getServerConnection()
        getPendingEvents()

        rvAdapter = EventsRvAdapter(requireContext(), dataList)
        binding.rvAllEvents.layoutManager = LinearLayoutManager(context)
        binding.rvAllEvents.adapter = rvAdapter
        setItemTouchHelper()

        //handle item menu click
        rvAdapter.optionsMenuClickListener(object : EventsRvAdapter.MenuOptionClickListener {
            override fun onOptionMenuClicked(view: View, position: Int) {
                performOptionsMenuClick(view, position)
            }
        })

        //Add new data
        binding.fab.setOnClickListener {
            showBottomSheetDialog()
        }

        isNetworkOk = isNetworkAvailable(requireContext())
        logOutput("Check Internet $isNetworkOk")

        return binding.root
    }

    private fun getServerConnection() {
        val retrofit = BasePoint.getEvents()
        apiResponse = retrofit!!.create(ApiResponse::class.java)
    }

    private fun getPendingEvents() {
        val params: MutableMap<String, String> = HashMap()
        params["status"] = "Pending"
        //There are Two methods enqueue and execute. We use enqueue method for calling the network at
        //background thread(asynchronous) using the main thread. When we already at background thread then use execute.
        apiResponse.getAllEvents(params)
            .enqueue(object : Callback<ArrayList<ApiResponseModel>> {
                override fun onResponse(
                    call: Call<ArrayList<ApiResponseModel>>,
                    response: Response<ArrayList<ApiResponseModel>>,
                ) {
                    if(response.isSuccessful){
                        dataList.clear()
                        dataList.addAll(response.body()!!)
                        rvAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<ArrayList<ApiResponseModel>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Response Failed", Toast.LENGTH_SHORT).show()
                    //Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            })
    }

    //handle the menu options click
    private fun performOptionsMenuClick(view: View, position: Int) {
        //Inflate a Custom Popup Menu layout
        val popupView = layoutInflater.inflate(R.layout.custom_popup_menu, null)
        //Create PopupWindow Parameters
        val width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        val height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        //val height = WindowManager.LayoutParams.WRAP_CONTENT
        val focusable = true

        //Create a PopupWindow
        val popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.elevation = 10f

        // Show the content view in PopupWindow at the desired location
        PopupWindowCompat.showAsDropDown(popupWindow, view, -154, 0, Gravity.CENTER)


        //Get the position of the last visible item in a RecyclerView
        val layoutManager = binding.rvAllEvents.layoutManager as LinearLayoutManager
        val lastItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()

        // Get the last item view
        val lastItemView = binding.rvAllEvents.getChildAt(lastItemPosition)

        // Get the coordinates of the last item
        val location =
            IntArray(2) //This array will be used to store the x and y coordinates of a view.
        lastItemView?.getLocationInWindow(location) //Fetch the current screen coordinates of the view and stores them in the location array.


        // Show the PopupWindow at the last item coordinates
//        popupWindow.showAtLocation(
//            binding.rvAllEvents,
//            Gravity.NO_GRAVITY,
//            location[0],
//            location[1] + lastItemView?.height!!
//        )


        // Handle menu item clicks here
        val editMenu = popupView.findViewById<View>(R.id.tvEdit)
        val cancelMenu = popupView.findViewById<View>(R.id.tvCancel)
        val completedMenu = popupView.findViewById<View>(R.id.tvCompleted)

        //implement the edit action here
        editMenu.setOnClickListener {

            val bottomSheet1 = BottomSheetDialog(requireActivity())
            bottomSheet1.setContentView(R.layout.bottom_sheet)

            val eventTitle = bottomSheet1.findViewById<AppCompatEditText>(R.id.etTitle)
            val eventDescription = bottomSheet1.findViewById<AppCompatEditText>(R.id.etDescription)
            val eventDate = bottomSheet1.findViewById<AppCompatEditText>(R.id.etDatePicker)
            val eventPriority = bottomSheet1.findViewById<AutoCompleteTextView>(R.id.acTvPriority)
            val eventStatus = bottomSheet1.findViewById<AutoCompleteTextView>(R.id.etStatus)
            val eventUpdateButton = bottomSheet1.findViewById<AppCompatButton>(R.id.btnAddEvent)

            //Set Existing Data
            eventUpdateButton?.text = "Update Event"
            eventTitle?.setText(dataList[position].title)
            eventDescription?.setText(dataList[position].description)
            eventDate?.setText(dataList[position].dateOfEventCreation)
            val priorityInt = dataList[position].priority
            val priorityText = when (priorityInt) {
                1 -> "High"
                2 -> "Medium"
                3 -> "Low"
                else -> "Unknown Priority"
            }
            eventPriority?.setText(priorityText)
            eventStatus?.setText(dataList[position].status.toString())

            //Create the instance of our calendar.
            val c = Calendar.getInstance()
            val setDate = OnDateSetListener { _, year, month, day ->
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, month)
                c.set(Calendar.DAY_OF_MONTH, day)

                selectedEventFormat = "$year-${month + 1}-$day"
                eventDate?.setText(selectedEventFormat)
            }
            eventDate?.setOnClickListener {
                DatePickerDialog(
                    requireContext(), setDate, c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            //Select Event Priority
            val priority = resources.getStringArray(R.array.eventPriority)
            val arrayAdapter1 = ArrayAdapter(requireContext(), R.layout.dropdown_item, priority)
            eventPriority?.setAdapter(arrayAdapter1)
            eventPriority?.setOnItemClickListener { _, _, position, _ ->
                selectedItemPosition = position + 1
            }

            //Select Event Status
            val status = resources.getStringArray(R.array.eventStatus)
            val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.dropdown_item, status)
            eventStatus?.setAdapter(arrayAdapter2)

            eventUpdateButton?.setOnClickListener {
                val params: MutableMap<String, String> = HashMap()
                params["id"] = dataList[position].id.toString()
                params["title"] = eventTitle?.text.toString()
                params["description"] = eventDescription?.text.toString()
                params["start_date"] = selectedEventFormat
                params["priority"] = selectedItemPosition.toString();
                params["status"] = eventStatus?.text.toString()

                apiResponse.editEvents(params)
                    .enqueue(object : Callback<String> {
                        override fun onResponse(
                            call: Call<String>,
                            response: Response<String>,
                        ) {
                            bottomSheet1.dismiss()
                        }

                        override fun onFailure(
                            call: Call<String>,
                            t: Throwable,
                        ) {
                            Toast.makeText(requireContext(), "Response Failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }

            bottomSheet1.show()
            popupWindow.dismiss()
        }

        cancelMenu.setOnClickListener {
            val params: MutableMap<String, String> = HashMap()
            params["id"] = dataList[position].id.toString()
            params["status"] = "Cancel"
            dataList.removeAt(position)
            rvAdapter.notifyDataSetChanged()

            apiResponse.updateEventStatus(params)
                .enqueue(object : Callback<ArrayList<ApiResponseModel>> {
                    override fun onResponse(
                        call: Call<ArrayList<ApiResponseModel>>,
                        response: Response<ArrayList<ApiResponseModel>>,
                    ) {

                    }

                    override fun onFailure(
                        call: Call<ArrayList<ApiResponseModel>>,
                        t: Throwable,
                    ) {

                    }
                })
            popupWindow.dismiss()
        }

        completedMenu.setOnClickListener {
            val params: MutableMap<String, String> = HashMap()
            params["id"] = dataList[position].id.toString()
            params["status"] = "Completed"
            dataList.removeAt(position)
            rvAdapter.notifyDataSetChanged()
            apiResponse.updateEventStatus(params)
                .enqueue(object : Callback<ArrayList<ApiResponseModel>> {
                    override fun onResponse(
                        call: Call<ArrayList<ApiResponseModel>>,
                        response: Response<ArrayList<ApiResponseModel>>,
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Event Completed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ArrayList<ApiResponseModel>>,
                        t: Throwable,
                    ) {

                    }
                })
            popupWindow.dismiss()
        }

        //Show Default Popup Menu
        /*val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.item_menu, popupMenu.menu)
        //popupMenu.inflate(R.menu.item_menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.statusEdit -> {

                }
                R.id.statusCancel -> {
                    val params: MutableMap<String, String> = HashMap()
                    params["status"] = "Cancel"
                    params["id"] = position.toString()
                    apiService.getUpdateEventStatus(params).enqueue(object : Callback<ArrayList<ApiResponseModel>>{
                        override fun onResponse(
                            call: Call<ArrayList<ApiResponseModel>>,
                            response: Response<ArrayList<ApiResponseModel>>,
                        ) {
                            if(response.isSuccessful){
                            rvAdapter.notifyItemChanged(position)
                            }
                        }

                        override fun onFailure(
                            call: Call<ArrayList<ApiResponseModel>>,
                            t: Throwable,
                        ) {

                        }
                    })
                }
                R.id.statusCompleted -> {

                }
            }
            true
        }
        popupMenu.show()*/
    }

    //Adding swipe to delete functionality
    private fun setItemTouchHelper() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemPosition = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.RIGHT) {
                    //Getting the item at a particular position
                    val saveDeletedItem = dataList[itemPosition]
                    Log.d("Tag", saveDeletedItem.toString())

                    //Remove item from our array list
                    dataList.removeAt(itemPosition)

                    //Notify our item is removed from adapter
                    rvAdapter.notifyItemRemoved(itemPosition)

                    // Show a SnackBar with an Undo option
                    showUndoSnackBar(saveDeletedItem, itemPosition)
                }
            }


            //which is called when a child view within the RecyclerView
            //is being drawn due to user interaction.
            override fun onChildDraw(
                canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean,
            ) {

                var itemView = viewHolder.itemView
                //calculate the height of the swiped view
                val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                Log.d("check", itemView.toString())

                //Get the position of swipe item
                val position = viewHolder.adapterPosition
                // Check if the position is valid
                if (position != RecyclerView.NO_POSITION && position < dataList.size) {
                    // Access the dataList only if the position is valid
                    var priority = dataList[position].priority

                    // Set the background color for swipe
                    val paint = Paint()
                    when (priority) {
                        1 -> paint.color = Color.RED
                        2 -> paint.color =
                            ContextCompat.getColor(requireContext(), R.color.bg_medium)

                        3 -> paint.color = ContextCompat.getColor(requireContext(), R.color.bg_low)
                    }

                    // Draw the background color round during moving swipe into right direction
                    val cornerRadius = 26f
                    val rectRight = RectF(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.left.toFloat(),
                        itemView.bottom.toFloat()
                    )

                    // Draw the background color round during moving swipe into left direction
                    /*val rectLeft = RectF(
                            itemView.right - dX,
                            itemView.top.toFloat(),
                            itemView.left.toFloat(),
                            itemView.bottom.toFloat()
                        )*/
                    canvas.drawRoundRect(rectRight, cornerRadius, cornerRadius, paint)
                }

                // Draw the delete icon
                val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.delete)
                val iconWidth = deleteIcon?.intrinsicWidth
                val iconHeight = deleteIcon?.intrinsicHeight

                val scaleFactor = 1.5f // Adjust the scale factor as needed

                // Calculate the scaled dimensions
                val newIconWidth = (iconWidth?.times(scaleFactor))?.toInt()
                val newIconHeight = (iconHeight?.times(scaleFactor))?.toInt()

                val halfIcon = iconWidth?.div(2)

                // Calculate position of delete icon on the left side
                val top = itemView.top + ((height - newIconHeight!!) / 2).toInt()
                val bottom = top + newIconHeight
                val left = itemView.left + halfIcon!!
                val right = left + newIconWidth!!

                // Calculate position of delete icon on the right side
                /*val right = itemView.right - halfIcon!!
                val left = right - newIconWidth!!*/

                // Draw the delete icon
                deleteIcon.setBounds(left, top, right, bottom)
                deleteIcon.draw(canvas)

                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

            }

        }).attachToRecyclerView(binding.rvAllEvents)
    }

    private fun showUndoSnackBar(saveDeletedItem: ApiResponseModel, itemPosition: Int) {
        Snackbar.make(binding.rvAllEvents, "Deleted", Snackbar.LENGTH_LONG)
            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    //After dismiss the snackbar, item will be delete from database
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        deleteEvent(saveDeletedItem)
                    }
                    Log.d("event1", event.toString())
                }
            })
            .setAction(
                "Undo", View.OnClickListener {
                    // Add the item back to the ArrayList at the same position
                    dataList.add(itemPosition, saveDeletedItem)

                    Log.d("why", saveDeletedItem.toString())

                    // Notify the adapter that the item is inserted
                    rvAdapter.notifyItemInserted(itemPosition)
                }
            ).show()
    }

    private fun deleteEvent(saveDeletedItem: ApiResponseModel) {
        apiResponse.deleteEvents(saveDeletedItem.id!!).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                /*if (response.body().equals("Successfully Deleted")) {
                    Toast.makeText(activity, "Delete", Toast.LENGTH_LONG).show()
                }*/

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(activity, t.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })

    }

    //It is necessary to send a date according to database format
    var selectedEventFormat = "2023-11-20"

    //Apply bottom sheet
    var bottomSheet: BottomSheetDialog? = null
    private fun showBottomSheetDialog() {
        bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet?.setContentView(R.layout.bottom_sheet)

        /*bottomSheet.behavior.skipCollapsed = true
        bottomSheet.behavior.state = STATE_EXPANDED
        bottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheet.window?.setSoftInputMode(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)*/

        val eventTitle = bottomSheet?.findViewById<AppCompatEditText>(R.id.etTitle)
        val eventDescription = bottomSheet?.findViewById<AppCompatEditText>(R.id.etDescription)
        val eventDate = bottomSheet?.findViewById<AppCompatEditText>(R.id.etDatePicker)
        val eventPriority = bottomSheet?.findViewById<AutoCompleteTextView>(R.id.acTvPriority)
        val eventStatus = bottomSheet?.findViewById<AutoCompleteTextView>(R.id.etStatus)
        val eventAddButton = bottomSheet?.findViewById<AppCompatButton>(R.id.btnAddEvent)

        //Create the instance of our calendar.
        val c = Calendar.getInstance()
        val setDate = OnDateSetListener { _, year, month, day ->
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.DAY_OF_MONTH, day)

            //val myFormat = "MM/dd/yy"
            //val dateFormat = SimpleDateFormat(myFormat, Locale.US)
            //eventDate?.setText(dateFormat.format(c.getTime()))

            selectedEventFormat = "$year-${month + 1}-$day"
            //selectedEventFormat = "$year-${month + 1}-$day"
            eventDate?.setText(selectedEventFormat)
        }
        eventDate?.setOnClickListener {
            DatePickerDialog(
                requireContext(), setDate, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //Select Event Priority
        val priority = resources.getStringArray(R.array.eventPriority)
        val arrayAdapter1 = ArrayAdapter(requireContext(), R.layout.dropdown_item, priority)
        eventPriority?.setAdapter(arrayAdapter1)
        eventPriority?.setOnItemClickListener { _, _, position, _ ->
            selectedItemPosition = position + 1
        }

        //Select Event Status
        val status = resources.getStringArray(R.array.eventStatus)
        val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.dropdown_item, status)
        eventStatus?.setAdapter(arrayAdapter2)


        eventAddButton?.setOnClickListener {
            val params: MutableMap<String, String> = HashMap()
            params["title"] = eventTitle?.text.toString()
            params["description"] = eventDescription?.text.toString()
            params["start_date"] = selectedEventFormat
            params["priority"] = selectedItemPosition.toString();
            params["status"] = eventStatus?.text.toString()
            apiResponse.addNewEvents(params)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        bottomSheet?.dismiss()
                        getPendingEvents()
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(requireContext(), "Response Failed", Toast.LENGTH_LONG)
                            .show()
                    }
                })

        }

        bottomSheet?.show()

    }

    //Error Check, using Constant TAG Logging
    private fun logOutput(data: String) {
        Log.d(tag, "LogOutput: $data")
    }


    //Check Internet is Connected or Not
    fun isNetworkAvailable(context: Context): Boolean {
        //That line of Code allows the app to query information about the state of network connectivity, such as
        //whether the device is connected to the internet and the type of connection (e.g., Wi-Fi, cellular).
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (connectivity != null) {
                //Check type transport type (cellular, Wi-Fi, etc.) and also check the network
                //supports certain features (like internet connectivity).
                val capabilities =
                    connectivity.getNetworkCapabilities(connectivity.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    ) {
                        Log.i("Internet", "Network is Available")
                        return true
                    }
                }
            }
        } else {
            // For below 29 Api
            if (connectivity.activeNetworkInfo?.isConnectedOrConnecting == true) {
                return true
            }
        }
        // If no internet connection, show an alert dialog
        showConnectivityPopup(requireContext())
        return false
    }

    //Show Dialog for Internet Connection
    private fun showConnectivityPopup(context: Context) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setTitle("No Internet Connection")
            .setMessage("Please Connect The Internet.")
            .setPositiveButton("Enable Wi-Fi") { _, _ ->
                enableWifi()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)

        createDialog = builder.create()
        createDialog.show()
    }

    //Goto Wifi Setting Screen
    private fun enableWifi() {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        startActivity(intent)
    }


//    override fun onBackPressed() {
//        super.onBackPressed()
//        if (isWifiEnabled) {
//            createDialog?.dismiss()
//        }
//    }


    //Due to gesture navigation feature, onBackPressed method has been deprecated. Now we given method
//    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
//        override fun handleOnBackPressed() {
//            // Your business logic to handle the back pressed event
//            if (isWifiEnabled){
//                createDialog?.dismiss()
//            }
//            Log.d(tag, "onBackPressedCallback: handleOnBackPressed")
//        }
//    }

//    private val onBackInvokedCallback = if (Build.VERSION.SDK_INT >= 33) {
//        OnBackInvokedCallback {
//            Log.d(TAG, "onBackInvokedCallback: onBackInvoked")
//        }
//    } else {
//        null
//    }


    companion object {

    }

}