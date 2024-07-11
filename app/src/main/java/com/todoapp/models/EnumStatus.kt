package com.todoapp.models

import com.google.gson.annotations.SerializedName

enum class EnumStatus{
    @SerializedName("Cancel")
    Cancel,

    @SerializedName("Pending")
    Pending,

    @SerializedName("Completed")
    Completed,
}