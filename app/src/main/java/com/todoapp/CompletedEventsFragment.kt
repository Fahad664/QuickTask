package com.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.todoapp.network_api.BasePoint
import com.todoapp.network_api.ApiResponse
import com.todoapp.adapters.EventsRvAdapter
import com.todoapp.databinding.FragmentFinishEventsBinding
import com.todoapp.models.ApiResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompletedEventsFragment : Fragment() {
    lateinit var binding : FragmentFinishEventsBinding
    lateinit var apiResponse : ApiResponse
    private var dataList : ArrayList<ApiResponseModel> = arrayListOf()
    lateinit var rvAdapter : EventsRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?, ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFinishEventsBinding.inflate(inflater, container, false)

        //dataList.add(ApiResponseModel(1,"All Event","Full House is the Grand prize in any Tambola game played. It means all 15 Numbers of a ticket","Oct","18",1,1))

        rvAdapter = EventsRvAdapter(requireContext(),dataList)
        binding.rvAllEvents.layoutManager = LinearLayoutManager(context)
        binding.rvAllEvents.adapter = rvAdapter

        getServerConnection()
        getCompletedEvents()

        return binding.root
    }

    private fun getServerConnection(){
        val retrofit = BasePoint.getEvents()
        apiResponse = retrofit!!.create(ApiResponse::class.java)
    }
    private fun getCompletedEvents() {
        val params: MutableMap<String, String> = HashMap()
        params["status"] = "Completed"
        apiResponse.getAllEvents(params).enqueue(object : Callback<ArrayList<ApiResponseModel>> {
            override fun onResponse(
                call: Call<ArrayList<ApiResponseModel>>,
                response: Response<ArrayList<ApiResponseModel>>, ) {
                dataList.clear()
                dataList.addAll(response.body()!!)
                rvAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<ArrayList<ApiResponseModel>>, t: Throwable) {
                Toast.makeText(activity, t.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })
    }


    companion object {

    }
}