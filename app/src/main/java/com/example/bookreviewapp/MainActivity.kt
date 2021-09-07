package com.example.bookreviewapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bookreviewapp.adapter.BookAdapter
import com.example.bookreviewapp.adapter.HistoryAdapter
import com.example.bookreviewapp.api.BookService
import com.example.bookreviewapp.databinding.ActivityMainBinding
import com.example.bookreviewapp.model.BestSellerDto
import com.example.bookreviewapp.model.History
import com.example.bookreviewapp.model.SearchBookDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var adapter : BookAdapter
    private lateinit var historyAdapter : HistoryAdapter
    private lateinit var bookService : BookService

    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initBookRecyclerView()
        initHistoryRecyclerView()

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "BookSearchDB"
        ).build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://book.interpark.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        bookService = retrofit.create(BookService::class.java) //구현체//retrofit 인터페이스의 구현체 생성

        bookService.getBestSellerBooks(getString(R.string.interparkAPIKey))
                .enqueue(object : Callback<BestSellerDto>{
                    override fun onResponse(call: Call<BestSellerDto>, response: Response<BestSellerDto>) {
                        //성공처리
                        if(response.isSuccessful.not()){
                            Log.d(TAG, "NOT! SUCCESS")
                            return
                        }
                        response.body()?.let{
                            Log.d(TAG,it.toString())

                            it.books.forEach{book ->
                                Log.d(TAG, book.toString())
                            }
                            adapter.submitList(it.books) //리스트를 대체해줌
                        }


                    }

                    override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {

                        //실패처리
                        Log.d(TAG, t.toString())
                    }

                })


    }

    private fun search(keyword : String){

        bookService.getBooksByName(getString(R.string.interparkAPIKey),keyword)
            .enqueue(object : Callback<SearchBookDto>{
                override fun onResponse(call: Call<SearchBookDto>, response: Response<SearchBookDto>) {

                    hideHistoryView()
                    saveSearchKeyword(keyword)


                    //성공처리
                    if(response.isSuccessful.not()){
                        Log.d(TAG, "NOT! SUCCESS")
                        return
                    }
                    response.body()?.let{
                        Log.d(TAG,it.toString())

                        it.books.forEach{book ->
                            Log.d(TAG, book.toString())
                        }
                        adapter.submitList(response.body()?.books.orEmpty()) //리스트를 대체해줌// 리사이클러뷰가 다시 초기화되면서 새로 내려온 books가 다시 summit되면서 리사이클러뷰ㅠ갱신
                    }//없다면 빈 리스트


                }

                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {

                    //실패처리
                    hideHistoryView()
                    Log.d(TAG, t.toString())
                }

            })
    }


    fun initBookRecyclerView(){
        adapter = BookAdapter()

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this) //레이아웃매니저
        binding.bookRecyclerView.adapter = adapter//어댑터 장착
    }

    private fun initHistoryRecyclerView(){
        historyAdapter = HistoryAdapter (historyDeleteClickedListenr = {
            deleteSearchKeyword(it)
        })
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
        initSearchEditText()
    }

    private fun initSearchEditText(){
        binding.searchEditText.setOnKeyListener{ v, keyCode, event ->//키가 눌렸을 때 이벤트
            if(keyCode==KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {//엔터가 눌렸을 때
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true //true는 이벤트를 처리를 했음을 알림
            }
            return@setOnKeyListener false
        }

        binding.searchEditText.setOnTouchListener{v, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                showHistoryView()

            }
            return@setOnTouchListener false
        }
    }

    private fun showHistoryView(){
        Thread{
            val keywords = db.historyDao().getAll().reversed()//최신순서대로 가져옴

            runOnUiThread(){//ui작업을 하기 위해서
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keywords.orEmpty())
            }
        }
        binding.historyRecyclerView.isVisible = true
    }
    private fun hideHistoryView(){
        binding.historyRecyclerView.isVisible = false
    }

    private fun saveSearchKeyword(keyword : String){
        Thread {
            db.historyDao().insertHistory(History(null, keyword))
        }.start()
    }
    private fun deleteSearchKeyword(keyword : String){
        Thread{
            db.historyDao().delete(keyword)
            showHistoryView()
            //뷰 갱신
        }.start()
    }
    companion object{
        private const val TAG = "MainActivity"

    }
}