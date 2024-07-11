package com.todoapp.models
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
//When we get the data from Server then this class is called a POJO class. That contains the
//objects to be obtained from the JSON file.
data class ApiResponseModel(
    @SerializedName("id")
    val id : Int? = null,

    @SerializedName("title")
    val title : String,

    @SerializedName("description")
    val description : String,

    @SerializedName("dateOfEventCreation")
    val dateOfEventCreation : String,

    @SerializedName("priority")
    val priority : Int,

    @SerializedName("status")
    val status : EnumStatus

)