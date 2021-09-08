package com.example.bookreviewapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bookreviewapp.dao.HistoryDao
import com.example.bookreviewapp.dao.ReviewDao
import com.example.bookreviewapp.model.History
import com.example.bookreviewapp.model.Review


//DB 룸사용 -> gradle추가
@Database(entities = [History::class, Review::class], version = 2)
abstract class AppDatabase :RoomDatabase(){

    //DB는 Dao에서 꺼내옴
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao() : ReviewDao
}


fun getAppDatabase(context : Context) :AppDatabase {

    val migration_1_2 = object :Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase) {
            //db에 직접 쿼리문을 작성하여 어떤 새로운 데이터 테이블을 바꿨는지 작성
            database.execSQL("CREATE TABLE `REVIEW` (`id` INTEGER, `review` TEXT," + "PRIMARY KEY(`id`))")
        }

    }

    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "BookSearchDB"
    )
        .addMigrations(migration_1_2)
        .build()
}