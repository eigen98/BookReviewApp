package com.example.bookreviewapp.model

import com.google.gson.annotations.SerializedName

data class Book(
    @SerializedName("itemId") val id : Long,
    @SerializedName("title") val title : String,
    @SerializedName("description") val description : String,
    @SerializedName("coverSmallurl") val coverSmallUrl : String
)

