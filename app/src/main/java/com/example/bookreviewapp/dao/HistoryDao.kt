package com.example.bookreviewapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookreviewapp.model.History


@Dao //DATA Access Object DB를 사용해 데이터를 조회하거나 조작하는 기능을 전담하도록 만든 오브젝트
interface HistoryDao {
    @Query("SELECT * FROM history") //history테이블에서 모든 데이터를 가져옴
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM history WHERE keyword == :keyword")
    fun delete(keyword : String)

}