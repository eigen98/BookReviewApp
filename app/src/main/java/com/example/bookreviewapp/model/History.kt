package com.example.bookreviewapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History (//모델클래스는 data클래스

    @PrimaryKey val uid : Int?,
    @ColumnInfo(name = "keyword") val keyword : String?,


)