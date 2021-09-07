package com.example.bookreviewapp

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bookreviewapp.dao.HistoryDao
import com.example.bookreviewapp.model.History


//DB 룸사용 -> gradle추가
@Database(entities = [History::class], version = 1)
abstract class AppDatabase :RoomDatabase(){

    //DB는 Dao에서 꺼내옴
    abstract fun historyDao(): HistoryDao
}