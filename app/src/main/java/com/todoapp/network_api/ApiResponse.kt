package com.todoapp.network_api
import com.todoapp.models.ApiResponseModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap
//This is a Interface Class. It is used to define endpoints.
interface ApiResponse {
    @GET("AllEvents.php")
    fun getAllEvents(@QueryMap params: Map<String, String>):Call<ArrayList<ApiResponseModel>>

    @FormUrlEncoded
    @POST("UpdateEventStatus.php")
    fun updateEventStatus(@FieldMap params: Map<String, String>):Call<ArrayList<ApiResponseModel>>

    @FormUrlEncoded
    @POST("UpdateEvents.php")
    fun editEvents(@FieldMap params: Map<String, String>):Call<String>

    @FormUrlEncoded
    @POST("AddEvents.php")
    fun addNewEvents(@FieldMap params: Map<String, String>):Call<String>

    @FormUrlEncoded
    @POST("DeleteEvents.php")
    fun deleteEvents(@Field("id") id:Int):Call<String>

}