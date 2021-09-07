package com.example.bookreviewapp.model

import com.google.gson.annotations.SerializedName

data class BestSellerDto(
    @SerializedName("title") val title : String,
    @SerializedName("item") val books : List<Book>,

)
//전체 모델에서 데이터를 연결시켜주는 개념 DADto
