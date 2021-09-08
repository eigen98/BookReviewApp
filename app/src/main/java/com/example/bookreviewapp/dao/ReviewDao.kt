package com.example.bookreviewapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bookreviewapp.model.Review


@Dao
interface ReviewDao {

    @Query("SELECT * FROM review WHERE id ==:id")
    fun getOneReview(id : Int) : Review

    @Insert(onConflict = OnConflictStrategy.REPLACE) //새로 저장하면 대체되는 형식
    fun saveReview(review: Review)


}