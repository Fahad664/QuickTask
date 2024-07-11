package com.todoapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.todoapp.network_api.BasePoint
import com.todoapp.network_api.ApiResponse
import com.todoapp.adapters.EventsRvAdapter
import com.todoapp.databinding.FragmentAllEventsBinding
import com.todoapp.models.ApiResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllEventsFragment : Fragment() {
    lateinit var binding : FragmentAllEventsBinding
    lateinit var rvAdapter : EventsRvAdapter
    lateinit var dataList : ArrayList<ApiResponseModel>
    lateinit var apiResponse: ApiResponse

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllEventsBinding.inflate(inflater, container,false)
        getServerConnection()
        getAllEvents()

        dataList = arrayListOf()
        /*dataList.add(ApiResponseModel(1,"All Event","Full House is the Grand prize in any Tambola game played. It means all 15 Numbers of a ticket","Oct","18",1,1))
        dataList.add(ApiResponseModel(1,"All Event","Full House is the Grand prize in any Tambola game played. It means all 15 Numbers of a ticket","Oct","18",3,1))
        dataList.add(ApiResponseModel(1,"All Event","Full House is the Grand prize","Oct","18",2,1))
        dataList.add(ApiResponseModel(1,"All Event","Full House is the Grand prize in any Tambola game played. It means all 15 Numbers of a ticket","Oct","18",1,1))
        dataList.add(ApiResponseModel(1,"All Event","Full House is the Grand prize in any Tambola game played. It means all 15 Numbers of a ticket","Oct","18",2,1))
        dataList.add(ApiResponseModel(1,"All Event","Full House is the Grand prize in any Tambola game played. It means all 15 Numbers of a ticket","Oct","18",3,1))*/

        rvAdapter = EventsRvAdapter(requireContext(), dataList)
        binding.rvAllEvents.layoutManager = LinearLayoutManager(context)
        binding.rvAllEvents.adapter = rvAdapter

        return binding.root
    }

    //Connect with server
    private fun getServerConnection() {
        var retrofit  = BasePoint.getEvents()
        apiResponse = retrofit!!.create(ApiResponse::class.java)
    }

    //Fetch Data
    private fun getAllEvents() {
        val params: MutableMap<String, String> = HashMap()
        apiResponse.getAllEvents(params).enqueue(object : Callback<ArrayList<ApiResponseModel>>{
            override fun onResponse(
                call: Call<ArrayList<ApiResponseModel>>,
                response: Response<ArrayList<ApiResponseModel>>,
            ) {
                //Toast.makeText(activity,response.body()?.get(0).toString(), Toast.LENGTH_LONG).show()
                Log.d("check", response.toString())
                dataList.clear()
               /* for(i in response.body()!!){
                    dataList.add(i)
                }*/
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